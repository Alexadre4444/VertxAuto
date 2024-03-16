package io.zetaly.vertx.api.verticle;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.core.Verticle;
import io.zetaly.vertx.api.option.Scale;

import java.util.List;

public interface VerticleManager extends Verticle {

    String MANAGED_VERTICLE_CONFIG_KEY = "managedVerticle";
    String SCALE_CONFIG_KEY = "scale";

    Class<AbstractVerticle> getManagedVerticleClass();

    List<String> getManagedVerticles();

    Uni<String> incrementInstance();

    Scale getScale();
}
