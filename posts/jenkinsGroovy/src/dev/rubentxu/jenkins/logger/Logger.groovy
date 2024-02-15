package dev.rubentxu.jenkins.logger

import com.cloudbees.groovy.cps.NonCPS
import dev.rubentxu.jenkins.interfaces.ILogger
import dev.rubentxu.jenkins.logger.AnsiColor
import dev.rubentxu.jenkins.logger.LogLevel

class Logger implements ILogger {

    LogLevel logLevel = LogLevel.INFO
    protected Script steps

    Logger(Script steps) {
        this.steps = steps
    }

    void logMessage(LogLevel level, String message) {
        if (isLoggable(level)) {
            String header = "${level.getColor().colorCode}${level.getStyle().colorCode}[${level}] ";
            String body = message;
            String footer = AnsiColor.RESET.colorCode;

            steps.ansiColor('xterm') {
                String msg = header + body + footer;
                this.steps.echo msg;
            }
        }
    }

    private Boolean isLoggable(LogLevel level) {
        return level.levelNumber <= logLevel.levelNumber
    }

    @Override
    void info(String message) {
        logMessage(LogLevel.INFO, message)
    }

    @Override
    void warn(String message) {
        logMessage(LogLevel.WARN, message)
    }

    @Override
    void debug(String message) {
        logMessage(LogLevel.DEBUG, message)
    }

    @Override
    void error(String message) {
        logMessage(LogLevel.ERROR, message)
    }

    @Override
    void fatal(String message) {
        logMessage(LogLevel.FATAL, message)
    }

    @Override
    void executeWhenDebug(Closure body) {
        if (logLevel == 'DEBUG') {
            body()
        }
    }

    @Override
    <T> void printPrettyLog(LogLevel level, T obj) {
        if (isLoggable(level)) {
            logMessage(level, extendPrettyPrint(obj, 0, new StringBuilder()).toString())
        }
    }

    protected <T> StringBuilder extendPrettyPrint(T obj, Integer level = 0, StringBuilder sb = new StringBuilder()) {
        def indent = { lev -> sb.append('  ' * lev) }
        if (obj instanceof Map) {
            sb.append('{\n')
            obj.each { name, value ->
                // if(name.contains('.'))  return // skip keys like "a.b.c", which are redundant
                indent(level + 1).append(name)
                (value instanceof Map) ? sb.append(' ') : sb.append(' = ')
                extendPrettyPrint(value, level + 1, sb)
                sb.append('\n')
            }
            indent(level).append('}')
        } else if (obj instanceof List) {
            sb.append('[\n')
            obj.eachWithIndex { value, index ->
                indent(level + 1)
                def isLatestElement = (index == obj.size() - 1)
                isLatestElement ? extendPrettyPrint(value, level + 1, sb) : extendPrettyPrint(value, level + 1, sb).append(',')
                sb.append('\n')
            }
            indent(level).append(']')
        } else if (obj instanceof String) {
            sb.append('"').append(obj).append('"')
        } else {
            sb.append(obj)
        }
        return sb
    }

    @Override
    void logPrettyMessages(LogLevel level, List<String> messages) {
        logMessage(level, createPrettyMessage(messages))
    }

    @Override
    void logPrettyError(List<String> msgs) {
        error(createPrettyMessage(msgs))
    }

    @NonCPS
    static String createPrettyMessage(List<String> msgs) {
        return [
                '===========================================',
                flattenMessage(null, msgs.findAll { !it?.isEmpty() }).join('\n'),
                '===========================================',
        ].join('\n')
    }


    @NonCPS
    protected static List<String> flattenMessage(def list, def msgs) {
        list = list ?: []
        if (!(msgs instanceof String) && !(msgs instanceof GString)) {
            msgs.each { msg ->
                list = flattenMessage(list, msg)
            }
        } else {
            list += msgs
        }

        return list
    }

}
