package com.sputnik.stellar.util;

public interface ConfigManagerListener {
    void registeredKey(String key, String existingValue, String defaultValue);

    void unregisteredKey(String key, String existingValue);

    void updatedValue(String key, String oldValue, String newValue);
}
