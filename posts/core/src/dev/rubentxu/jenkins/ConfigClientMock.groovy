package dev.rubentxu.jenkins

import com.cloudbees.groovy.cps.NonCPS
import dev.rubentxu.jenkins.interfaces.IConfigClient

class ConfigClientMock implements IConfigClient {
    private Map<String, Object> config

    ConfigClientMock(Map<String, Object> config = [:]) {
        this.config = config
    }

    @Override
    void load() {
        // No operation needed for mock
    }

    @Override
    void refresh() {
        // No operation needed for mock
    }

    @NonCPS
    @Override
    def <T> T required(String key, Class<T> type) {
        def value = config.get(key)
        if (value == null) {
            throw new IllegalArgumentException("Configuration error : Key '${key}' not found")
        }
       if (value.getClass() != type) {
            throw new IllegalArgumentException("Configuration error : Key '${key}' is not of type ${type}")
        }
        return value as T
    }

    @NonCPS
    @Override
    def <T> T optional(String key, Class<T> type) {
        def value = config.get(key)
        if (value == null) {
            return null
        }
        if (value.getClass() != type) {
            throw new IllegalArgumentException("Configuration error : Key '${key}' is not of type ${type}")
        }
        return value as T
    }

    @NonCPS
    @Override
    def <T> T optional(String key, T defaultValue) {
        def value = config.get(key)
        if (value == null) {
            return defaultValue
        }
        return value as T
    }

    ConfigClientMock withValue(String key, String value) {
        config.put(key, value)
        return this
    }

    void setConfig(Map<String, String> config) {
        this.config = config
    }
}