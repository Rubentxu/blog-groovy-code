package dev.rubentxu.jenkins.mocks

class MethodInvocationRecorder {

    Map<String, List<MethodMock>> methodCalls = [:]

    Boolean containsKey(String methodName) {
        return methodCalls.containsKey(methodName)
    }

    def methodMissing(String methodName, args) {
        // Convertir args a una lista si no lo es
        def argumentList = args instanceof List ? args : args.toList()

        return new Object() {
            def getAt(int pos) {
                return verifyInvocation(methodName, pos).verifyArgs(argumentList)
            }
        }

    }

    private MethodMock verifyInvocation(String methodName, int pos = 0) {
        Integer posIndexed = pos > 0 ? pos-1 : pos
        def calls = methodCalls[methodName] ?: []
        def result = calls.getAt(posIndexed)
        assert result != null:
                "Step ${methodName} was not called ${pos} times"
        return result

    }

    void createList(String methodName) {
        methodCalls[methodName] = []
    }

    void addMock(String methodName, MethodMock methodMock) {
        methodCalls[methodName] << methodMock
    }

    def MethodMock get(String methodName) {
        return  methodCalls[methodName]
    }

}
