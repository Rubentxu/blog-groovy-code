package dev.rubentxu.jenkins.mocks

import org.spockframework.lang.SpreadWildcard

class MethodMock {
    List<Object> args
    private String methodName
    Object output

    MethodMock(String methodName, List<Object> args, Object output) {
        this.methodName = methodName
        this.args = args
        this.output = output
    }


    Boolean should(Closure body) {
        body.delegate = this
        def valueExpected =  body()
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


    MethodMock verifyArgs(List list) {
        assert list.size() <= args.size():
                "Number of arguments are not the same, expected: ${args.size()}, actual: ${list.size()}"

        for (int i = 0; i < list.size(); i++) {
            // Ignora el argumento si es '_'
            if (!(list[i]?.getAt(0) instanceof SpreadWildcard )) {
                assert list[i] == args[i]:
                        """Argument at index ${i} is not the expected, 
                           Expected: >${args[i]}< 
                             Actual: >${list[i]}<
                        """
            }
        }
        return this
    }
}