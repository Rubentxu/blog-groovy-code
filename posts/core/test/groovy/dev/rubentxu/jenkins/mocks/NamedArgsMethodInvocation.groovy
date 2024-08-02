package dev.rubentxu.jenkins.mocks

import org.spockframework.lang.SpreadWildcard

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
        ensure(expectedNamedArgs.size() <= namedArgs.size(),
        """
        Number of arguments are not the same, Actual: ${namedArgs.size()}, Expected: ${expectedNamedArgs.size()}
          Actual: ${namedArgs}
          Expected: ${expectedNamedArgs}
        """)

        expectedNamedArgs.each { key, value ->
            // Ignora el argumento si es '_'
            if (!(containsSpreadWildcard(value))) {
                ensure(checkArgsAreEqual(value ,namedArgs[key]),
                       """Argument with name '${key}' is not the expected,
                       Expected: >${value}<
                         Actual: >${namedArgs[key]}<
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
        return false
    }

}