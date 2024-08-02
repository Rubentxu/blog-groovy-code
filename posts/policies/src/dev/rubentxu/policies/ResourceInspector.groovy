package dev.rubentxu.policies

import dev.rubentxu.policies.interfaces.IResourceInspector
import dev.rubentxu.policies.interfaces.StructuredResource
import dev.rubentxu.policies.model.InspectionResourceResult
import dev.rubentxu.policies.model.InspectionResult
import dev.rubentxu.policies.model.Rule
import dev.rubentxu.policies.model.Validate
import dev.rubentxu.policies.model.ValidationPolicy

/**
 * This class is responsible for inspecting resources against a set of validation policies.
 * It implements the IResourceInspector interface.
 *
 * @author Rubentxu
 */
class ResourceInspector implements IResourceInspector {
    private Binding binding
    private GroovyShell shell

    /**
     * Constructor for the ResourceInspector class.
     * Initializes the GroovyShell and Binding instances.
     */
    ResourceInspector() {
        resetBinding()
    }

    /**
     * Resets the GroovyShell and Binding instances.
     */
    @Override
    void resetBinding() {
        this.binding = new Binding()
        this.shell = new GroovyShell(binding)
    }

    /**
     * Clears all variables from the binding.
     */
    @Override
    void clearVariables() {
        binding.variables.clear()
    }

    /**
     * Adds a helper closure to the binding.
     *
     * @param name The name of the helper.
     * @param body The closure to be added.
     */
    @Override
    void addHelper(String name, Closure body) {
        setVariable(name, body)
    }

    /**
     * Sets a variable in the binding.
     *
     * @param name The name of the variable.
     * @param value The value of the variable.
     */
    @Override
    void setVariable(String name, Object value) {
        binding.setVariable(name, value)
    }

    /**
     * Evaluates a Groovy expression.
     *
     * @param expression The Groovy expression to be evaluated.
     * @return The result of the evaluation.
     */
    @Override
    def evaluate(String expression) {
        shell.evaluate(expression)
    }

    /**
     * Evaluates a Groovy expression and assigns the result to a variable in the binding.
     *
     * @param variableName The name of the variable.
     * @param expression The Groovy expression to be evaluated.
     */
    @Override
    void evaluateAndAssign(String variableName, String expression) {
        def value = evaluate(expression)
        binding.setVariable(variableName, value)
    }

    /**
     * Evaluates multiple Groovy expressions and assigns the results to variables in the binding.
     *
     * @param nameExpressionMap A map of variable names to Groovy expressions.
     */
    @Override
    void evaluateAndAssignMultiple(Map<String, Object> nameExpressionMap) {
        for (Map.Entry<String, Object> entry : nameExpressionMap.entrySet()) {
            evaluateAndAssign(entry.key, entry.value)
        }
    }

    /**
     * Returns a map of violations for a given resource and policy.
     *
     * @param resource The resource to be inspected.
     * @param policy The policy to be applied.
     * @return A map of violations.
     */
    @Override
    Map<String, List<String>> getViolations(StructuredResource resource, ValidationPolicy policy) {
        return inspectResource(resource, policy).policyBreaches
    }

    /**
     * Inspects a resource against a policy and returns an InspectionResourceResult.
     *
     * @param resource The resource to be inspected.
     * @param policy The policy to be applied.
     * @return An InspectionResourceResult.
     */
    @Override
    InspectionResourceResult inspectResource(StructuredResource resource, ValidationPolicy policy) {
        def violations = [:]
        List<Rule> rulesNotExecuted = []
        policy.spec.rules.each { rule ->
            if (rule.match.kinds.contains(resource.kind)) {
                def result = evaluateFieldsAndPredicate(rule, resource)

                if(!result.isExecuted) { rulesNotExecuted.add(rule) }
                if (!result.passed) {
                    violations.putAll(result.policyBreaches)
                }
            }

        }
        if (policy.spec.rules.isEmpty()) {
            return new InspectionResourceResult(passed: false, isExecuted: false, rulesNotExecuted: [], policyBreaches: ['validate-no-rules': 'No se encontraron reglas para el recurso'])
        }
        return new InspectionResourceResult(passed: violations.isEmpty(), isExecuted: true, rulesNotExecuted: rulesNotExecuted, policyBreaches: violations)
    }


    /**
     * Evaluates fields and predicate of a rule against a resource.
     *
     * @param rule The rule to be evaluated.
     * @param resource The resource to be inspected.
     * @return An InspectionResult.
     */
    private InspectionResult evaluateFieldsAndPredicate(Rule rule, StructuredResource resource) {
        String ruleName = rule.name
        Validate validate = rule.validate
        Map<String, Object> resourceMap = resource.toMap()
        setVariable('resource', resourceMap)

        validate.fields.each { name, fieldExpression ->
            evaluateAndAssign(name, "resource.${fieldExpression}")
        }

        boolean result = evaluate(validate.predicate)
        return new InspectionResult(
                isExecuted: true,
                passed: result,
                policyBreaches: [(ruleName): validate.message],

        )
    }

}