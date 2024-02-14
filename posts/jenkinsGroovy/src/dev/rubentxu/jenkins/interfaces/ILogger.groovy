package dev.rubentxu.jenkins.interfaces

interface ILogger {

    void setLogLevel(String level)

    void log(String level, String message)

    void info(String message)

    void warn(String message)

    void debug(String message)

    void error(String message)

    void fatal(String message)

    void whenDebug(Closure body)

    def <T> void prettyPrint(String level, T obj)

    void prettyMessages(String level, List<String> messages)

    void prettyError(List<String> msgs)

}
