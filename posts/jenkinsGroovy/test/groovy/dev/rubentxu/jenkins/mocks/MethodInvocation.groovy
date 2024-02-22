package dev.rubentxu.jenkins.mocks


abstract class MethodInvocation<T> {

    abstract Boolean verifyArgs(T expectedArgs)

    String formatMessage(String message) {
        Integer maxLength = message.split("\n").max { it.length() }.length()
        def padding = "-" * (maxLength - 2)
        return "\n${padding} ${message} \n${padding}".toString()
    }
}