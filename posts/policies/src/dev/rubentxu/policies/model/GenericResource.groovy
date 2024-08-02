package dev.rubentxu.policies.model

import dev.rubentxu.policies.interfaces.StructuredResource


class GenericResource implements StructuredResource {
    String apiVersion
    String kind
    Map<String, Object> spec
    Map<String, Object> status

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
                spec: this.spec,
                status: this.status
        ]
    }
}