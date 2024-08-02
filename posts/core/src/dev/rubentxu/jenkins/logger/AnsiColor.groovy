package dev.rubentxu.jenkins.logger

import com.cloudbees.groovy.cps.NonCPS

enum AnsiColor implements Serializable {
    RESET('\u001B[0m'),
    BOLD('\u001B[1m'),
    ITALIC('\u001B[3m'),
    RED('\u001B[31m'),
    GREEN('\u001B[32m'),
    YELLOW('\u001B[33m'),
    MAGENTA('\u001B[35m')

    final String colorCode

    AnsiColor(String colorCode) {
        this.colorCode = colorCode
    }

    @NonCPS
    @Override
    String toString() {
        return this.colorCode
    }
}
