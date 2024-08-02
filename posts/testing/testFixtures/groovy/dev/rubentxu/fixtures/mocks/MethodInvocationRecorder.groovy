package dev.rubentxu.fixtures.mocks


class MethodInvocationRecorder {

    Map<String, List<MethodInvocation>> methodCalls = [:]

    Boolean containsKey(String methodName) {
        return methodCalls.containsKey(methodName)
    }

    def methodMissing(String methodName, args) {
        // Convertir args a una lista si no lo es
        def argumentsResolved = getArgs(args)

        MethodInvocation expectedMethodInvocation = argumentsResolved instanceof Map
                ? new NamedArgsMethodInvocation(methodName, argumentsResolved, null)
                : new PositionalArgsMethodInvocation(methodName, argumentsResolved, null)

        return new Object() {
            def getAt(int pos) {
                def methodMock = verifyInvocation(methodName, pos)
                return methodMock.equals(expectedMethodInvocation)
            }
        }

    }

    def getArgs(args) {
        def argsList= args.toList()
        if (argsList[0] instanceof Map) {
            return argsList[0]
        }
        return argsList
    }

    private MethodInvocation verifyInvocation(String methodName, int pos = 0) {
        def calls = methodCalls[methodName] ?: []
        def result = calls.getAt(pos)
        MethodInvocation.ensure(result != null,
                "Step ${methodName} was not called ${pos} times")
        return result

    }

    void createList(String methodName) {
        methodCalls[methodName] = []
    }

    void addMock(String methodName, MethodInvocation methodInvocation) {
        List<MethodInvocation> invocations = methodCalls[methodName]
        invocations.add(methodInvocation)
    }

    def PositionalArgsMethodInvocation get(String methodName) {
        return methodCalls[methodName]
    }

    @Override
    public String toString() {
        return "MethodInvocationRecorder{" +
                "methodCalls=" + methodCalls +
                '}';
    }
}