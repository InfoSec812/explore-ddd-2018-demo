package io.openinnovationlabs.domain.eventstore;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@ProxyGen
public interface EventStore {

    static EventStore create(Vertx vertx, JsonObject config) {
        return new EventStoreImpl(vertx, config);
    }

    static EventStore createProxy(Vertx vertx, String address) {
        return new EventStoreVertxEBProxy(vertx, address);
    }

    void append(JsonObject command, Handler<AsyncResult<String>> handler);

    void loadEvents(JsonObject command, Handler<AsyncResult<JsonObject>> handler);

    void clearEvents(Handler<AsyncResult<Void>> handler);
}