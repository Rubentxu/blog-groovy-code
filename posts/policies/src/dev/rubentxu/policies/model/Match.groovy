package dev.rubentxu.policies.model

import dev.rubentxu.policies.interfaces.DataModel


class Match implements DataModel {
    List<String> kinds

    static Match fromMap(Map map) {
        return new Match(
                kinds: map.kinds
        )
    }

    @Override
    Map<String, Object> toMap() {
        return kinds
    }
}
