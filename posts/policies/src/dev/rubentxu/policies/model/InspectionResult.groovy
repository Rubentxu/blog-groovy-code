package dev.rubentxu.policies.model

import com.cloudbees.groovy.cps.NonCPS

class InspectionResult {
    Boolean passed
    Boolean isExecuted
    Map policyBreaches

    @NonCPS
    @Override
    String toString() {
        return "ResultValidation: isExecuted: ${isExecuted}, isValid: ${passed}, errors: ${policyBreaches.collect { k, v -> "$k: $v" }}"
    }
}