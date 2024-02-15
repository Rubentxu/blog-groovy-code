package dev.rubentxu.jenkins.interfaces

interface IConfigClient {
    void load()
    String get(String key)
    void refresh()
}