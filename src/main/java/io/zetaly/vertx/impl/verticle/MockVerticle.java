package io.zetaly.vertx.impl.verticle;

import io.quarkus.arc.Unremovable;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import jakarta.enterprise.context.Dependent;
import org.jboss.logging.Logger;

@Dependent
@Unremovable
public class MockVerticle extends AbstractVerticle {

    protected final Logger logger;

    public MockVerticle(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Uni<Void> asyncStart() {
        return Uni.createFrom().voidItem()
                .invoke(voidItem -> logger.info("Hello"));
    }
}
