package dev.rubentxu.jenkins.mocks

import org.spockframework.lang.SpreadWildcard
import org.spockframework.lang.Wildcard

class MethodMock {
    List<Object> args
    private String methodName
    Object output

    MethodMock(String methodName, Object args, Object output) {
        this.methodName = methodName
        this.output = output
        this.args = resolveArgs(args)
    }

    List<Object> resolveArgs(args) {
        def temp = args.toList()[0]
        if (temp instanceof Map) {
            return temp.values().asList()
        } else if (temp instanceof List) {
            return temp
        } else {
            return [temp]
        }
    }


    Boolean should(Closure body) {
        body.delegate = this
        def valueExpected = body()
        assert valueExpected == this.output:
                """Expected: >${this.output}< 
                     Actual: >${valueExpected}<
                """
        return true
    }

    Boolean rightShift(Closure body) {
        body.delegate = this
        body() == this.output
    }

    String formatMessage(String message) {
        Integer maxLength = message.split("\n").max { it.length() }.length()
        def padding = "-" * (maxLength - 2)
        return "\n${padding} ${message} \n${padding}".toString()

    }


    MethodMock verifyArgs(List list) {
        assert list.size() <= args.toList().size():
                formatMessage("""
    Number of arguments are not the same, Actual: ${args.size()}, Expected: ${list.size()}
      Actual: ${args}
      Expected: ${list}                
    """)

        for (int i = 0; i < list.size(); i++) {
            // Ignora el argumento si es '_'
            if (!(containsSpreadWildcard(list[i]))) {
                assert list[i] == args[i]:
                        """Argument at index ${i} is not the expected, 
                           Expected: >${args[i]}< 
                             Actual: >${list[i]}<
                        """
            }
        }
        return this
    }

    Boolean containsSpreadWildcard(argument) {
        if(argument instanceof Wildcard) {
            return argument.any { it instanceof SpreadWildcard }
        }
        return false
    }

    @Override
    String toString() {
        return "MethodMock{methodName='${methodName}', args=${args}, output=${output}}"
    }
}