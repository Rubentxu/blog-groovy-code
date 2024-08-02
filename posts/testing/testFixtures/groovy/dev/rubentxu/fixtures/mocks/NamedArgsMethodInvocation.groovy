package dev.rubentxu.fixtures.mocks


import org.spockframework.lang.SpreadWildcard

class NamedArgsMethodInvocation extends MethodInvocation<Map<String, Object>> {
    Map<String, Object> namedArgs
    private String methodName
    Object output

    NamedArgsMethodInvocation(String methodName, Map<String, Object> args, Object output) {
        this.methodName = methodName
        this.output = output
        this.namedArgs = args
    }

    @Override
    Boolean verifyArgs(Map<String, Object> expectedNamedArgs) {
        ensure(expectedNamedArgs.size() <= namedArgs.size(),
                """
        Number of arguments are not the same, Actual: ${namedArgs.size()}, Expected: ${expectedNamedArgs.size()}
          Actual: ${namedArgs}
          Expected: ${expectedNamedArgs}
        """)

        expectedNamedArgs.each { String key, value ->
            // Ignora el argumento si es '_'
            if (!(containsSpreadWildcard(value))) {
                def actualValue = namedArgs[key]
                ensure(checkArgsAreEqual(value ,actualValue),
                        """Argument with name '${key}' is not the expected,
                                    Expected: >${value}<
                                    Actual:   >${actualValue}<
                    """)
            }
        }
        return this
    }

    @Override
    boolean equals(Object o) {
        ensure(o instanceof NamedArgsMethodInvocation,
                """You are passing positional arguments and you are expected to use named arguments.
                    Redefine method invocation 'steps.${this.methodName}' with the correct arguments.
                    For example:
                    steps.${this.methodName}(${this.namedArgs.collect { key, value -> "${key} : expectedValue" }.join(", ") })
                  """)

        NamedArgsMethodInvocation that = (NamedArgsMethodInvocation) o
        return Objects.equals(methodName, that.methodName) &&  verifyArgs(that.namedArgs)
    }


    Boolean containsSpreadWildcard(argument) {
        if (argument instanceof Map.Entry) {
            return argument.value instanceof SpreadWildcard
        }
        if(argument instanceof List) {
            return argument.any { it instanceof SpreadWildcard }
        }
        return argument instanceof SpreadWildcard
    }

}