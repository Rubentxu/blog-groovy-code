package dev.rubentxu.fixtures.mocks


class AssertFailException extends Exception {
    AssertFailException(String message) {
        super(formatMessage(message))
    }

    static String formatMessage(String message) {
        Integer maxLength = message.split("\n").max { it.length() }.length()
        def padding = "-" * (maxLength - 2)
        return "\n${padding} \n${message} \n${padding}".toString()
    }
}
