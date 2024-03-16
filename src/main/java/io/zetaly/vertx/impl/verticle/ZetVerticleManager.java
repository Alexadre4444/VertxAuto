package io.zetaly.vertx.impl.verticle;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.zetaly.vertx.api.exception.InvalidManagedClassException;
import io.zetaly.vertx.api.exception.InvalidScaleException;
import io.zetaly.vertx.api.option.Scale;
import io.zetaly.vertx.api.verticle.VerticleManager;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Dependent
public class ZetVerticleManager extends AbstractVerticle implements VerticleManager {

    protected final Logger logger;
    private final List<String> managedVerticles = new ArrayList<>();
    private long timerId;

    @Inject
    public ZetVerticleManager(Logger logger) {
        this.logger = logger;
    }

    private AbstractVerticle getNewManagedVerticleInstance() {
        return CDI.current().select(getManagedVerticleClass()).get();
    }

    @Override
    public Uni<Void> asyncStart() {
        return checkConfiguration()
                .onItem()
                .invoke(ignored -> {
                    timerId = vertx.setPeriodic(1000, this::incrementInstance);
                });
    }

    @Override
    public Uni<Void> asyncStop() {
        return Uni.createFrom().voidItem()
                .invoke(voidItem -> vertx.cancelTimer(timerId));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<AbstractVerticle> getManagedVerticleClass() {
        if (config() == null || !config().containsKey(MANAGED_VERTICLE_CONFIG_KEY)) {
            throw new InvalidManagedClassException("No class defined for manager verticle. Add a config property named '"
                    + MANAGED_VERTICLE_CONFIG_KEY + "'.");
        }
        ClassLoader classLoader = getClass().getClassLoader();
        Class<?> managedClass;
        try {
            managedClass = classLoader.loadClass(config().getString(MANAGED_VERTICLE_CONFIG_KEY));
        } catch (ClassNotFoundException e) {
            throw new InvalidManagedClassException("Defined class not found: " + config().getString(MANAGED_VERTICLE_CONFIG_KEY));
        }
        if (!getAbstractVerticleClass().isAssignableFrom(managedClass)) {
            throw new InvalidManagedClassException("Defined class does not extends '" + getAbstractVerticleClass().getName() + "': " + managedClass);
        }
        return (Class<AbstractVerticle>) managedClass;
    }

    @Override
    public List<String> getManagedVerticles() {
        return managedVerticles;
    }

    @Override
    public Uni<String> incrementInstance() {
        return vertx.deployVerticle(getNewManagedVerticleInstance())
                .onItem().invoke(managedVerticles::add)
                .onItem().invoke(deploymentID -> logger.info("New '" + getManagedVerticleClass().getName() + "' deployed."))
                .onItem().invoke(ignored -> logger.info("Number of managed verticle for '" + getManagedVerticleClass().getName() + "': " + getManagedVerticles().size()));
    }

    public CompletableFuture<String> incrementInstance(long id) {
        return incrementInstance().subscribeAsCompletionStage();
    }

    @Override
    public Scale getScale() {
        if (config() == null || !config().containsKey(SCALE_CONFIG_KEY)) {
            throw new InvalidScaleException("No scale defined for manager verticle. Add a config property named '"
                    + SCALE_CONFIG_KEY + "'.");
        }
        return Scale.valueOf(config().getString(SCALE_CONFIG_KEY));
    }

    private Uni<Void> checkConfiguration() {
        return Uni.createFrom().voidItem()
                .invoke(this::getManagedVerticleClass)
                .invoke(this::getScale);
    }

    private Class<AbstractVerticle> getAbstractVerticleClass() {
        return AbstractVerticle.class;
    }
}
