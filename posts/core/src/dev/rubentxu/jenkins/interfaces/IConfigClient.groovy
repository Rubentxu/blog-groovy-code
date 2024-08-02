package dev.rubentxu.jenkins.interfaces

interface IConfigClient {
    void load()

    void refresh()

    def <T> T required(String key, Class<T> type)

    def <T> T optional(String key, Class<T> type)

    def <T> T optional(String key, T defaultValue)
}