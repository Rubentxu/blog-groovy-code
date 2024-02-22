package dev.rubentxu.jenkins.mocks

import org.spockframework.lang.SpreadWildcard
import org.spockframework.lang.Wildcard

class PositionalArgsMethodInvocation extends MethodInvocation<List> {
    Set<Object> args
    private String methodName
    Object output

    PositionalArgsMethodInvocation(String methodName, Set<Object> args, Object output) {
        this.methodName = methodName
        this.output = output
        this.args = this.args = args.collect { it -> it instanceof Closure ? new Object() : it }
    }


    Boolean rightShift(Closure body) {
        body.delegate = this
        body() == this.output
    }


    Boolean verifyArgs(List list) {
        assert list.size() <= args.toList().size():
                formatMessage("""
    Number of arguments are not the same, Actual: ${args.size()}, Expected: ${list.size()}
      Actual: ${args}
      Expected: ${list}                
    """)

        for (int i = 0; i < list.size(); i++) {
            // Ignora el argumento si es '_'
            if (!(containsSpreadWildcard(list[i]))) {
                assert checkArgsAreEqual(list[i], args[i]):
                        """Argument at index ${i} is not the expected, 
                           Expected: >${args[i]}< 
                             Actual: >${list[i]}<
                        """
            }
        }
        return this
    }

    Boolean checkArgsAreEqual(expectedArg, actualArg) {
        def expected = expectedArg
        def actual = actualArg
        if (expectedArg instanceof Map) {
            expected = expectedArg.value
        }
        if (actualArg instanceof Map.Entry) {
            actual = actualArg.value
        }

        return expected == actual
    }

    Boolean containsSpreadWildcard(argument) {
        if (argument instanceof Map.Entry) {
            return argument.value instanceof SpreadWildcard
        }

        if (argument instanceof Wildcard) {
            return argument.any { it instanceof SpreadWildcard }
        }
        return false
    }

    @Override
    String toString() {
        return "MethodMock{methodName='${methodName}', args=${args}, output=${output}}"
    }
}