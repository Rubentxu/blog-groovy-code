package dev.rubentxu.policies.model

import dev.rubentxu.policies.interfaces.DataModel


class PolicySpec implements DataModel {
    List<Rule> rules

    static PolicySpec fromMap(Map map) {
        return new PolicySpec(
                rules: map.rules.collect { Rule.fromMap(it) }
        )
    }

    @Override
    Map<String, Object> toMap() {
        return rules.collect { it.toMap() }
    }
}