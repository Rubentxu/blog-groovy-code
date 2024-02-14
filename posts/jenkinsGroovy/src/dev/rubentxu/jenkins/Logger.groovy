package dev.rubentxu.jenkins

import com.cloudbees.groovy.cps.NonCPS
import dev.rubentxu.jenkins.interfaces.ILogger

class Logger implements ILogger {

    static final String RESET = '\u001B[0m'

    static final String BOLD = '\u001B[1m'
    static final String ITALIC = '\u001B[3m'
    static final String RED = '\u001B[31m'
    static final String GREEN = '\u001B[32m'
    static final String YELLOW = '\u001B[33m'
    static final String MAGENTA = '\u001B[35m'
    static final Map<String, Integer> LEVEL_NUMBERS = [
            'FATAL': 100,
            'ERROR': 200,
            'WARN' : 300,
            'INFO' : 400,
            'DEBUG': 500,
    ]
    String logLevel = 'INFO'
    protected Script steps

    Logger(Script steps) {
        this.steps = steps
    }

    @Override
    void log(String level, String message) {
        if (LEVEL_NUMBERS[level] && shouldLog(level)) {
            Map formatOpts = [
                    color: '',
                    level: level,
                    text : message,
                    style: '',
                    reset: RESET
            ]
            switch (level) {
                case ['FATAL', 'ERROR']:
                    formatOpts.color = RED
                    formatOpts.style = BOLD
                    break
                case 'WARN':
                    formatOpts.color = YELLOW
                    formatOpts.style = BOLD
                    break
                case 'INFO':
                    formatOpts.color = GREEN
                    break
                case 'DEBUG':
                    formatOpts.color = MAGENTA
                    formatOpts.style = ITALIC
                    break
            }
            steps.ansiColor('xterm') {
                String msg = "${formatOpts.color}${formatOpts.style}[${formatOpts.level}] ${formatOpts.text}${formatOpts.reset}"
                this.steps.echo msg
            }
        }
    }

    private Boolean shouldLog(String level) {
        return LEVEL_NUMBERS[level] <= LEVEL_NUMBERS[logLevel]
    }

    @Override
    void info(String message) {
        log('INFO', message)
    }

    @Override
    void warn(String message) {
        log('WARN', message)
    }

    @Override
    void debug(String message) {
        log('DEBUG', message)
    }

    @Override
    void error(String message) {
        log('ERROR', message)
    }

    @Override
    void fatal(String message) {
        log('FATAL', message)
    }

    @Override
    void whenDebug(Closure body) {
        if (logLevel == 'DEBUG') {
            body()
        }
    }

    @Override
    <T> void prettyPrint(String levelLog, T obj) {
        if (shouldLog(levelLog)) {
            log(levelLog, prettyPrintExtend(obj, 0, new StringBuilder()).toString())
        }
    }

    protected <T> StringBuilder prettyPrintExtend(T obj, Integer level = 0, StringBuilder sb = new StringBuilder()) {
        def indent = { lev -> sb.append('  ' * lev) }
        if (obj instanceof Map) {
            sb.append('{\n')
            obj.each { name, value ->
                // if(name.contains('.'))  return // skip keys like "a.b.c", which are redundant
                indent(level + 1).append(name)
                (value instanceof Map) ? sb.append(' ') : sb.append(' = ')
                prettyPrintExtend(value, level + 1, sb)
                sb.append('\n')
            }
            indent(level).append('}')
        } else if (obj instanceof List) {
            sb.append('[\n')
            obj.eachWithIndex { value, index ->
                indent(level + 1)
                def isLatestElement = (index == obj.size() - 1)
                isLatestElement ? prettyPrintExtend(value, level + 1, sb) : prettyPrintExtend(value, level + 1, sb).append(',')
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
    void prettyMessages(String level, List<String> messages) {
        log(level, createPrettyMessage(messages))
    }

    @Override
    void prettyError(List<String> msgs) {
        error(createPrettyMessage(msgs))
    }

    @NonCPS
    static String createPrettyMessage(List<String> msgs) {
        return [
                '===========================================',
                msgFlatten(null, msgs.findAll { !it?.isEmpty() }).join('\n'),
                '===========================================',
        ].join('\n')
    }


    @NonCPS
    protected static List<String> msgFlatten(def list, def msgs) {
        list = list ?: []
        if (!(msgs instanceof String) && !(msgs instanceof GString)) {
            msgs.each { msg ->
                list = msgFlatten(list, msg)
            }
        } else {
            list += msgs
        }

        return list
    }

}
