package dev.rubentxu.policies.interfaces


interface StructuredResource extends DataModel {

    String apiVersion()

    String getKind()

    def getSpec()

    def status()
}