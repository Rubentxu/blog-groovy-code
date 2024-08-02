package dev.rubentxu.policies.interfaces

import dev.rubentxu.policies.model.InspectionResourceResult
import dev.rubentxu.policies.model.ValidationPolicy


interface IResourceInspector {

    void addHelper(String name, Closure body)

    InspectionResourceResult inspectResource(StructuredResource resource, ValidationPolicy policy)

    void resetBinding()

    void clearVariables()

    void setVariable(String name, Object value)

    def evaluate(String expression)

    void evaluateAndAssign(String variableName, String expression)

    void evaluateAndAssignMultiple(Map<String, Object> nameExpressionMap)

    Map<String, List<String>> getViolations(StructuredResource resource, ValidationPolicy policy)

}