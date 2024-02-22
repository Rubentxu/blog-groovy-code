package dev.rubentxu.jenkins.mocks

class NamedArgsMethodInvocation extends MethodInvocation<Map<String, Object>> {
    Map<String, Object> namedArgs
    private String methodName
    Object output

    NamedArgsMethodInvocation(String methodName, Map<String, Object> args, Object output) {
        this.methodName = methodName
        this.output = output
        this.namedArgs = args.collectEntries { key, value ->
            [key, value instanceof Closure ? new Object() : value]
        }
    }


    @Override
    Boolean verifyArgs(Map<String, Object> expectedNamedArgs) {
        assert expectedNamedArgs.size() <= namedArgs.size():
                formatMessage("""
Number of arguments are not the same, Actual: ${namedArgs.size()}, Expected: ${expectedNamedArgs.size()}
  Actual: ${namedArgs}
  Expected: ${expectedNamedArgs}
""")

        expectedNamedArgs.each { key, value ->
            // Ignora el argumento si es '_'
            if (!(containsSpreadWildcard(value))) {
                assert checkArgsAreEqual(value ,namedArgs[key]):
                        """Argument with key ${key} is not the expected,
                       Expected: >${namedArgs[key]}<
                         Actual: >${value}<
                    """
            }
        }
        return this
    }

}