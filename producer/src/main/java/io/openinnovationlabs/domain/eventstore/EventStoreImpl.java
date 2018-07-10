package io.openinnovationlabs.domain.eventstore;

import io.openinnovationlabs.adapters.InMemoryAppendOnlyStore;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventStoreImpl implements EventStore {

    private final static Logger LOGGER = LoggerFactory.getLogger(EventStore.class);

    private AppendOnlyStore store;

    Vertx vertx;

    JsonObject config;

    public EventStoreImpl(Vertx vertx, JsonObject config) {
        this.config = config;
        this.vertx = vertx;
        String appendOnlyStoreType = config.getString("appendOnlyStoreType");
        if (appendOnlyStoreType.equals("InMemory")) {
            this.store = new InMemoryAppendOnlyStore();
        } else {
            new IllegalStateException(String.format("appendOnlyStoreType %s currently not implemented", appendOnlyStoreType));
        }

        LOGGER.info("Event Store is up");
    }


    public void append(JsonObject command, Handler<AsyncResult<String>> handler) {
        AppendEventsCommand cmd = command.mapTo(AppendEventsCommand.class);
        store.append(cmd.events);
        LOGGER.info(String.format("Events appended %d", cmd.events.size()));
        handler.handle(Future.succeededFuture("complete"));
    }

    public void loadEvents(JsonObject command, Handler<AsyncResult<JsonObject>> handler) {
        LoadEventsCommand cmd = command.mapTo(LoadEventsCommand.class);
        LoadEventResponse response = new LoadEventResponse(store.loadEvents(cmd.aggregateIdentity));
        LOGGER.info("reply");
        handler.handle(Future.succeededFuture(JsonObject.mapFrom(response)));
    }

    public void clearEvents(Handler<AsyncResult<Void>> handler) {
        Future<Void> fut;
        if (config.getString("appendOnlyStoreType").equals("InMemory")) {
            ((InMemoryAppendOnlyStore) store).clear();
            fut = Future.<Void>succeededFuture();
        } else {
            fut = Future.<Void>failedFuture(new ServiceException(1, "Failed to clear events"));
        }
        handler.handle(fut);
    }
}
