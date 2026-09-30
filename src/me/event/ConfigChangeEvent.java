package me.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ConfigChangeEvent<T> extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String configKey;
    private final T oldValue;
    private final T newValue;

    public ConfigChangeEvent(String configKey, T oldValue, T newValue) {
        this.configKey = configKey;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public String getConfigKey() {
        return configKey;
    }

    public T getOldValue() {
        return oldValue;
    }

    public T getNewValue() {
        return newValue;
    }
}
