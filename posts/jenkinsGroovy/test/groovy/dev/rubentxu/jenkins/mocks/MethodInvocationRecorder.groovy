package dev.rubentxu.jenkins.mocks

class MethodInvocationRecorder {

    Map<String, List<MethodInvocation>> methodCalls = [:]

    Boolean containsKey(String methodName) {
        return methodCalls.containsKey(methodName)
    }

    def methodMissing(String methodName, args) {
        // Convertir args a una lista si no lo es
        def argumentList = args instanceof List ? args : args.toList()

        return new Object() {
            def getAt(int pos) {
                def methodMock = verifyInvocation(methodName, pos)
                return methodMock.verifyArgs(argumentList)
            }
        }

    }

    private MethodInvocation verifyInvocation(String methodName, int pos = 0) {
        Integer posIndexed = pos > 0 ? pos - 1 : pos
        def calls = methodCalls[methodName] ?: []
        def result = calls.getAt(posIndexed)
        assert result != null:
                "Step ${methodName} was not called ${pos} times"
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

}
