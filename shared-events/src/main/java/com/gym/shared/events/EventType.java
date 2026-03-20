package com.gym.shared.events;

public final class EventType {

    public static final String UPSERT = "UPSERT";
    public static final String STATUS_CHANGED = "STATUS_CHANGED";
    public static final String ENTRY_REGISTERED = "ENTRY_REGISTERED";
    public static final String EXIT_REGISTERED = "EXIT_REGISTERED";

    private EventType() {
    }
}

