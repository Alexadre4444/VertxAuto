package io.zetaly.vertx.impl;

import io.smallrye.mutiny.Uni;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.impl.VertxImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.zetaly.vertx.api.ManagedVertx;
import io.zetaly.vertx.api.option.Scale;
import io.zetaly.vertx.api.verticle.VerticleManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.zetaly.vertx.api.verticle.VerticleManager.MANAGED_VERTICLE_CONFIG_KEY;
import static io.zetaly.vertx.api.verticle.VerticleManager.SCALE_CONFIG_KEY;

@ApplicationScoped
public class ZetManagedVertx implements ManagedVertx {

    protected final Logger logger;
    protected final Vertx vertx;
    protected final Instance<VerticleManager> verticleManagerInstance;
    protected final List<VerticleManager> verticleManagers = new ArrayList<>();

    @Inject
    public ZetManagedVertx(Logger logger, Vertx vertx, Instance<VerticleManager> verticleManagerInstance) {
        this.logger = logger;
        this.vertx = vertx;
        this.verticleManagerInstance = verticleManagerInstance;
    }

    @Override
    public Uni<String> deployManagedVertx(String name) {
        return deployManagedVertx(name, Scale.MANUAL);
    }

    @Override
    public Uni<String> deployManagedVertx(String name, Scale scale) {
        return Uni.createFrom().item(
                        new DeploymentOptions().setConfig(new JsonObject(Map.of(MANAGED_VERTICLE_CONFIG_KEY, name, SCALE_CONFIG_KEY, scale)))
                ).onItem()
                .transformToUni(deploymentOptions -> vertx.deployVerticle(verticleManagerInstance::get, deploymentOptions))
                .invoke(deployementId -> verticleManagers.add(getVerticleManager(deployementId)))
                .invoke(deployementId -> logger.info("New VerticleManager deployed for '" + name + "': " + deployementId));
    }

    @Override
    public Optional<VerticleManager> getManagerOf(String name) {
        return verticleManagers.stream().filter(verticleManager -> verticleManager.getManagedVerticleClass().getName().equals(name))
                .findFirst();
    }

    private VerticleManager getVerticleManager(String deployementId) {
        return (VerticleManager) ((VertxImpl) vertx.getDelegate()).getDeployment(deployementId)
                .getVerticles().stream().findFirst().get();
    }
}
