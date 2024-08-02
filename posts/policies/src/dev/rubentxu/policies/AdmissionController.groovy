package dev.rubentxu.policies

import dev.rubentxu.policies.interfaces.IAdmissionController
import dev.rubentxu.policies.interfaces.IResourceInspector
import dev.rubentxu.policies.interfaces.IResourceParser
import dev.rubentxu.policies.interfaces.StructuredResource
import dev.rubentxu.policies.model.InspectionResourceResult
import dev.rubentxu.policies.model.InspectionResult
import dev.rubentxu.policies.model.Rule
import dev.rubentxu.policies.model.ValidationPolicy

import java.nio.file.Path

/**
 * This class is responsible for managing the admission of resources based on validation policies.
 * It implements the IAdmissionController interface.
 *
 * @author Rubentxu
 */
class AdmissionController implements IAdmissionController {
    private IResourceInspector inspector
    private IResourceParser policyParser
    private Map<String, IResourceParser> parsers = [:]
    private List<StructuredResource> admittedResources = []
    private List<StructuredResource> rejectedResources = []
    private List<Rule> notExcutedRules = []
    private Map<StructuredResource, InspectionResult> inspectionResults = [:]

    /**
     * Constructor for the AdmissionController class.
     * Initializes the IResourceInspector and IResourceParser instances.
     *
     * @param inspector The IResourceInspector instance.
     * @param policyParser The IResourceParser instance.
     */
    AdmissionController(IResourceInspector inspector, IResourceParser policyParser) {
        this.inspector = inspector
        this.policyParser = policyParser
    }

    /**
     * Registers a parser for a specific resource type.
     *
     * @param parser The IResourceParser instance to be registered.
     */
    @Override
    void registerParser(IResourceParser parser) {
        parsers[parser.type] = parser
    }

    /**
     * Unregisters a parser for a specific resource type.
     *
     * @param parserType The type of the parser to be unregistered.
     */
    @Override
    void unregisterParser(String parserType) {
        parsers.remove(parserType)
    }

    /**
     * Admits a resource based on a validation policy.
     *
     * @param resource The StructuredResource to be admitted.
     * @param policy The ValidationPolicy to be applied.
     * @return An InspectionResourceResult.
     */
    @Override
    InspectionResourceResult admit(StructuredResource resource, ValidationPolicy policy) {
        InspectionResourceResult inspectionResult = inspector.inspectResource(resource, policy)
        notExcutedRules.addAll(inspectionResult.rulesNotExecuted)
        if (inspectionResult.passed) {
            admittedResources.add(resource)
        } else {
            rejectedResources.add(resource)
        }
        return inspectionResult
    }

    /**
     * Admits a resource based on a validation policy, using a specific parser type.
     *
     * @param resourcePath The path of the resource to be admitted.
     * @param rulesPath The path of the rules to be applied.
     * @param parserType The type of the parser to be used.
     * @return An InspectionResourceResult.
     */
    @Override
    InspectionResourceResult admit(Path resourcePath, Path rulesPath, String parserType) {
        IResourceParser parser = parsers[parserType]
        if (!parser) {
            throw new IllegalArgumentException("No parser registered for type: $parserType")
        }

        StructuredResource resource = parser.parse(resourcePath)
        ValidationPolicy policy = policyParser.parse(rulesPath) as ValidationPolicy
        return admit(resource, policy)
    }

    /**
     * Returns a list of admitted resources.
     *
     * @return A list of StructuredResource.
     */
    @Override
    List<StructuredResource> getAdmittedResources() {
        return admittedResources.asUnmodifiable()
    }

    /**
     * Returns a list of rejected resources.
     *
     * @return A list of StructuredResource.
     */
    @Override
    List<StructuredResource> getRejectedResources() {
        return rejectedResources.asUnmodifiable()
    }

    /**
     * Returns a map of violations for a given resource.
     *
     * @param resource The StructuredResource to be inspected.
     * @return A map of violations.
     */
    @Override
    Map<String, List<String>> getViolations(StructuredResource resource) {
        return inspectionResults[resource]?.policyBreaches ?: [:]
    }

    /**
     * Clears the history of admitted and rejected resources.
     */
    @Override
    void clearHistory() {
        admittedResources.clear()
        rejectedResources.clear()
        notExcutedRules.clear()
        inspectionResults.clear()
    }
}