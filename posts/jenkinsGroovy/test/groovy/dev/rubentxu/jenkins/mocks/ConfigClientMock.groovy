package dev.rubentxu.jenkins.mocks

import dev.rubentxu.jenkins.interfaces.IConfigClient

class ConfigClientMock implements IConfigClient {
    private Map<String, String> config

    ConfigClientMock() {
        this.config = [:]
    }

    @Override
    void load() {
        // No operation needed for mock
    }

    @Override
    String get(String key) {
        return config.get(key)
    }

    @Override
    void refresh() {
        // No operation needed for mock
    }

    ConfigClientMock withValue(String key, String value) {
        config.put(key, value)
        return this
    }

    void setConfig(Map<String, String> config) {
        this.config = config
    }
}