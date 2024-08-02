package dev.rubentxu.policies.model

import dev.rubentxu.policies.interfaces.DataModel


class Validate implements DataModel {
    Map<String, String> fields
    String predicate
    String message

    static Validate fromMap(Map map) {
        return new Validate(
                fields: map.fields,
                predicate: map.predicate,
                message: map.message
        )
    }

    @Override
    Map<String, Object> toMap() {
        return [
                fields: this.fields,
                predicate: this.predicate,
                message: this.message
        ]
    }
}