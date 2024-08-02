package dev.rubentxu.policies.model

import com.cloudbees.groovy.cps.NonCPS


class InspectionResourceResult extends InspectionResult {
    List<Rule> rulesNotExecuted = []

    @NonCPS
    @Override
    String toString() {
        return "ResultValidation: isExecuted: ${isExecuted}, isValid: ${passed}, rulesNotExecuted: ${rulesNotExecuted.collect {it.name} }, errors: ${policyBreaches.collect { k, v -> "$k: $v" }}"
    }
}
