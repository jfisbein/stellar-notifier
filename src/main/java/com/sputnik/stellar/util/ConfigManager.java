package com.sputnik.stellar.util;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConfigManager {
    protected static final String DEFAULT_CONFIG_FILE_NAME = "configuration.properties";
    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);
    private SortedMap<String, ConfigValue> config = new TreeMap<>();

    private List<ConfigManagerListener> listeners = new ArrayList<>();

    private File file;

    public ConfigManager() {
        this("");
    }

    public ConfigManager(String configFileName) {
        if (StringUtils.isNotBlank(configFileName)) {
            file = new File(configFileName);
        } else {
            file = new File(DEFAULT_CONFIG_FILE_NAME);
        }

        loadConfiguration();
    }

    public ConfigManager(File configFile) {
        file = configFile;
        loadConfiguration();
    }

    public String get(String key) {
        return Optional.ofNullable(config.get(key))
                .map(ConfigValue::getValue)
                .orElse(System.getenv(key));
    }

    public String getDescription(String key) {
        return Optional.ofNullable(config.get(key))
                .map(ConfigValue::getDescription)
                .orElse(null);
    }

    public Integer getInt(String key) {
        Integer res = null;
        String value = get(key);
        if (value != null) {
            try {
                res = Integer.parseInt(value);
            } catch (NumberFormatException nfe) {
                log.warn("Value ({}) not numeric.", value);
            }
        }

        return res;
    }

    public void set(String key, String value) {
        set(key, value, null, true);
    }

    private void set(String key, String value, String description, boolean notifyListeners) {
        String oldValue = Optional.ofNullable(config.get(key))
                .map(ConfigValue::getValue)
                .orElse(null);

        ConfigValue newConfigValue = new ConfigValue(value, description);

        config.put(key, newConfigValue);
        saveConfiguration();
        if (notifyListeners) {
            notifyUpdateListeners(key, oldValue, value);
        }
    }

    public void register(String key, String defaultValue, String description) {
        String existingValue = Optional.ofNullable(config.get(key))
                .map(ConfigValue::getValue)
                .orElse(null);

        set(key, defaultValue, description, false);
        notifyRegisterListeners(key, existingValue, defaultValue);
    }

    public void unregister(String key) {
        if (config.containsKey(key)) {
            ConfigValue removed = config.remove(key);
            notifyUnregisterListeners(key, removed);
        }
    }

    public void register(String key, String description) {
        register(key, "", description);
    }

    public List<String> getAllKeys() {
        return new ArrayList<>(config.keySet());
    }

    private void saveConfiguration() {
        log.debug("Saving configuration to file: {}", file.getAbsolutePath());
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            Properties props = new Properties();

            for (Map.Entry<String, ConfigManager.ConfigValue> entry : config.entrySet()) {
                props.put(entry.getKey(), entry.getValue().getValue());
            }

            props.store(writer, "");
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

    private void loadConfiguration() {
        log.info("Loading configuration from file: {}", file.getAbsolutePath());
        if (file.exists()) {
            try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                Properties props = new Properties();
                props.load(reader);
                for (String key : props.stringPropertyNames()) {
                    config.put(key, new ConfigValue(props.getProperty(key), null));
                }
                log.info("Loaded {} values", props.size());
            } catch (IOException e) {
                log.warn(e.getMessage());
            }
        }
    }

    public void registerListener(ConfigManagerListener listener) {
        listeners.add(listener);
    }

    private void notifyRegisterListeners(String key, String existingValue, String defaultValue) {
        for (ConfigManagerListener listener : listeners) {
            try {
                listener.registeredKey(key, existingValue, defaultValue);
            } catch (Exception e) {
                log.warn("Error calling listener: {}.", listener, e);
            }
        }
    }

    private void notifyUnregisterListeners(String key, ConfigValue existingValue) {
        for (ConfigManagerListener listener : listeners) {
            try {
                listener.unregisteredKey(key, existingValue.value);
            } catch (Exception e) {
                log.warn("Error calling listener: {}.", listener, e);
            }
        }
    }

    private void notifyUpdateListeners(String key, String oldValue, String newValue) {
        for (ConfigManagerListener listener : listeners) {
            try {
                listener.updatedValue(key, oldValue, newValue);
            } catch (Exception e) {
                log.warn("Error calling listener: {}.", listener, e);
            }
        }
    }

    public File getConfigFile() {
        return file;
    }

    @Data
    static class ConfigValue {
        protected final String value;
        protected final String description;
    }
}