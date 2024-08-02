package dev.rubentxu.jenkins.mocks.credentials

interface Credentials {
    String getId()
}

class UsernamePasswordCredentials implements Credentials {
    String id
    String username
    String password

    UsernamePasswordCredentials(String id, String username, String password) {
        this.id = id
        this.username = username
        this.password = password
    }

    String getId() {
        return id
    }

    String getUsername() {
        return username
    }

    String getPassword() {
        return password
    }
}

class StringCredentials implements Credentials {
    String id
    String secret

    StringCredentials(String id, String secret) {
        this.id = id
        this.secret = secret
    }

    String getId() {
        return id
    }

    String getSecret() {
        return secret
    }
}