package dev.rubentxu.jenkins.logger

import com.cloudbees.groovy.cps.NonCPS


enum LogLevel implements Serializable {
    FATAL(100, AnsiColor.RED, AnsiColor.BOLD),
    ERROR(200, AnsiColor.RED, AnsiColor.BOLD),
    WARN(300, AnsiColor.YELLOW, AnsiColor.BOLD),
    INFO(400, AnsiColor.GREEN, AnsiColor.RESET),
    DEBUG(500, AnsiColor.MAGENTA, AnsiColor.ITALIC)

    final int levelNumber
    final AnsiColor color
    final AnsiColor style

    LogLevel(int levelNumber, AnsiColor color, AnsiColor style) {
        this.levelNumber = levelNumber
        this.color = color
        this.style = style
    }

    @NonCPS
    @Override
    String toString() {
        return this.name()
    }
}
