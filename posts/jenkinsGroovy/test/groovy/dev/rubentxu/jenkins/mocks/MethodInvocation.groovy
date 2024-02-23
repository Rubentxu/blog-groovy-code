package dev.rubentxu.jenkins.mocks


abstract class MethodInvocation<T> {

    abstract Boolean verifyArgs(T expectedArgs)



    Boolean checkArgsAreEqual(expectedArg, actualArg) {
        def expected = expectedArg
        def actual = actualArg
        if (expectedArg instanceof Map) {
            expected = expectedArg.value
        }

        return expected == actual
    }

    static void ensure(boolean condition, String message) {
        if (!condition) {
            throw new AssertFailException(message)
        }
    }
}