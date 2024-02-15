package dev.rubentxu.jenkins.logger

import dev.rubentxu.jenkins.mocks.StepsExecutorMock
import spock.lang.Specification

class LoggerSpec extends Specification {

    def "test logMessage"() {
        given:
        def steps = new StepsExecutorMock()
        def logger = new Logger(steps)
        def expectedMessage = "\u001B[32m\u001B[0m[INFO] Test message\u001B[0m"

        when:
        logger.logMessage(LogLevel.INFO, "Test message")

        then:
        steps.validate().ansiColor('xterm',_)[1]
        steps.validate().echo(expectedMessage)[1]
    }

    def "test isLoggable"() {
        given:
        def steps = new StepsExecutorMock()
        def logger = new Logger(steps)

        expect:
        logger.isLoggable(LogLevel.INFO) == true
        logger.isLoggable(LogLevel.DEBUG) == false
    }

    def "test info"() {
        given:
        def steps = new StepsExecutorMock()
        def logger = new Logger(steps)

        when:
        logger.info("Test message")

        then:
        steps.validate().ansiColor('xterm') >> { null }
        steps.validate().echo(_)[1]
    }


    def "test printPrettyLog"() {
        given:
        def steps = new StepsExecutorMock()
        def logger = new Logger(steps)

        when:
        logger.printPrettyLog(LogLevel.INFO, "Test message")

        then:
        steps.validate().ansiColor('xterm')
        steps.validate().echo(_)[1]
    }
}
