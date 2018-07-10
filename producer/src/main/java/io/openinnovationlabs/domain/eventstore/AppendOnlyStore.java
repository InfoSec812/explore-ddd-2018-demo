package io.openinnovationlabs.domain.eventstore;

import io.openinnovationlabs.domain.AggregateIdentity;
import io.openinnovationlabs.domain.Event;

import java.util.List;

public interface AppendOnlyStore {

    public void append(List<Event> events);

    public List<Event> loadEvents(AggregateIdentity aggregateIdentity);
}