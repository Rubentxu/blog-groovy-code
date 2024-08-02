package dev.rubentxu.jenkins.interfaces

import dev.rubentxu.jenkins.logger.LogLevel

interface ILogger {

    void setLogLevel(LogLevel level)

    void info(String message)

    void warn(String message)

    void debug(String message)

    void error(String message)

    void fatal(String message)

    void executeWhenDebug(Closure body)

    def <T> void printPrettyLog(LogLevel level, T obj)

    void logPrettyMessages(LogLevel level, List<String> messages)

    void logPrettyError(List<String> msgs)

}
