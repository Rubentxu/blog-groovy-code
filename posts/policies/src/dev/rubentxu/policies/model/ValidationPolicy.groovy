package dev.rubentxu.policies.model

import dev.rubentxu.policies.interfaces.StructuredResource


class ValidationPolicy implements StructuredResource {
    String apiVersion = 'v1'
    String kind = 'ValidationPolicy'
    Metadata metadata
    PolicySpec spec
    Map status

    @Override
    String apiVersion() {
        return this.apiVersion
    }

    @Override
    def status() {
        return this.status
    }

    @Override
    Map<String, Object> toMap() {
        return [
                apiVersion: this.apiVersion,
                kind: this.kind,
                metadata: this.metadata,
                spec: this.spec.toMap(),
                status: this.status
        ]
    }

    static ValidationPolicy fromMap(Map<String, Object> map) {
        return new ValidationPolicy(
                metadata: map.metadata as Metadata,
                spec: PolicySpec.fromMap(map.spec),
                status: map.status
        )
    }
}


