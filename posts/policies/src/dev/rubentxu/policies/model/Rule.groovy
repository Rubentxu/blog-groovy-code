package dev.rubentxu.policies.model

import dev.rubentxu.policies.interfaces.DataModel


class Rule implements DataModel {
    String name
    Match match
    Validate validate

    static Rule fromMap(Map map) {
        return new Rule(
                name: map.name,
                match: Match.fromMap(map.match),
                validate: Validate.fromMap(map.validate)
        )
    }

    @Override
    Map<String, Object> toMap() {
        return [
                name: this.name,
                match: this.match.toMap(),
                validate: this.validate.toMap()
        ]
    }
}