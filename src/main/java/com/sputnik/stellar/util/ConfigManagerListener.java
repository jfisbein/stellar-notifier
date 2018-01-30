package com.sputnik.stellar.util;

public interface ConfigManagerListener {
    public void registeredKey(String key, String existingValue, String defaultValue);

    public void unregisteredKey(String key, String existingValue);

    public void updatedValue(String key, String oldValue, String newValue);
}
