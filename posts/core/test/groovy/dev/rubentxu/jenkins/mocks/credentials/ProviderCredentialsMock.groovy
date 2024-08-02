package dev.rubentxu.jenkins.mocks.credentials

import java.util.concurrent.ConcurrentHashMap

class ProviderCredentialsMock {
    Map<String, Credentials> credentialsConfig

    ProviderCredentialsMock() {
        this.credentialsConfig = new ConcurrentHashMap()
    }

    Credentials getCredentials(String id) {
        return credentialsConfig[id]
    }

    void setCredentials(Map<String, Credentials> credentialsConfig) {
        credentialsConfig.each { id, credentials ->
            this.credentialsConfig[id] = credentials
        }
    }
}