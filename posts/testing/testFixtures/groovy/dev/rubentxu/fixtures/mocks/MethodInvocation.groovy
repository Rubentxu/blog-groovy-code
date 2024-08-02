package dev.rubentxu.fixtures.mocks


abstract class MethodInvocation<T> {

    abstract Boolean verifyArgs(T expectedArgs)



    Boolean checkArgsAreEqual(expectedArg, actualArg) {
        def expected = expectedArg
        def actual = actualArg instanceof GString || actualArg instanceof String? actualArg.toString() : actualArg
        if (expectedArg instanceof Map) {
            expected = expectedArg.value
        }
        if(expectedArg instanceof Closure<Boolean>) {
            return expectedArg.call(actual)
        }
        return expected == actual
    }

    static void ensure(boolean condition, String message) {
        if (!condition) {
            throw new AssertFailException(message)
        }
    }

    @Override
    def String toString() {
        return "MethodInvocation{${this.getClass().getSimpleName()}"
    }
}