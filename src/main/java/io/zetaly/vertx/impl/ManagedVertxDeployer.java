package io.zetaly.vertx.impl;

import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import io.zetaly.vertx.api.ManagedVertx;
import io.zetaly.vertx.impl.verticle.MockVerticle;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class ManagedVertxDeployer {
    protected final Logger logger;
    protected final ManagedVertx managedVertx;

    protected final List<String> verticleClassToDeploy = List.of(
            MockVerticle.class.getName()
    );

    public ManagedVertxDeployer(Logger logger, ManagedVertx managedVertx) {
        this.logger = logger;
        this.managedVertx = managedVertx;
    }

    public void init(@Observes StartupEvent e) {
        logger.info("Starting deployement of VerticleManager...");
        verticleClassToDeploy.forEach(verticleClassToDeploy -> {
            deployManagerFor(verticleClassToDeploy).await().indefinitely();
        });
        logger.info("VerticleManager deployed.");
    }

    protected Uni<Void> deployManagerFor(String abstractVerticleClassName) {
        return Uni.createFrom().voidItem()
                .onItem()
                .transform(voidItem -> managedVertx.getManagerOf(abstractVerticleClassName))
                .onItem()
                .transformToUni(verticleManagerOptional -> verticleManagerOptional.map(verticleManager -> Uni.createFrom().voidItem())
                        .orElseGet(() -> managedVertx.deployManagedVertx(abstractVerticleClassName).replaceWithVoid()));
    }
}
