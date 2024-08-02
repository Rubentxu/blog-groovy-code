package dev.rubentxu.jenkins.mocks

import org.spockframework.lang.SpreadWildcard
import org.spockframework.lang.Wildcard

class PositionalArgsMethodInvocation extends MethodInvocation<List> {
    List<Object> args
    private String methodName
    Object output

    PositionalArgsMethodInvocation(String methodName, List<Object> args, Object output) {
        this.methodName = methodName
        this.output = output
        this.args = args
    }


    Boolean rightShift(Closure body) {
        body.delegate = this
        body() == this.output
    }


    @Override
    Boolean verifyArgs(List list) {
        ensure(list.size() <= args.size(),
                """
        Number of arguments are not the same, Actual: ${args.size()}, Expected: ${list.size()}
          Actual: ${args}
          Expected: ${list}                
        """)

        for (int i = 0; i < list.size(); i++) {
            // Ignora el argumento si es '_'
            if (!(containsSpreadWildcard(list[i]))) {
                ensure(checkArgsAreEqual(list[i], args[i]),
                        """Argument at index ${i} is not the expected, 
                           Expected: >${list[i]}< 
                             Actual: >${args[i]}<
                        """)
            }
        }
        return this
    }

    @Override
    boolean equals(Object o) {
        if (!(o instanceof PositionalArgsMethodInvocation)) return false

        PositionalArgsMethodInvocation that = (PositionalArgsMethodInvocation) o
        return Objects.equals(methodName, that.methodName) &&  verifyArgs(that.args)
    }



    Boolean containsSpreadWildcard(argument) {
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