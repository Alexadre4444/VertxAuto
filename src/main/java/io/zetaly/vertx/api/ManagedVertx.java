package io.zetaly.vertx.api;

import io.smallrye.mutiny.Uni;
import io.zetaly.vertx.api.option.Scale;
import io.zetaly.vertx.api.verticle.VerticleManager;

import java.util.Optional;

public interface ManagedVertx {
    Uni<String> deployManagedVertx(String name);

    Uni<String> deployManagedVertx(String name, Scale scale);

    Optional<VerticleManager> getManagerOf(String name);
}
