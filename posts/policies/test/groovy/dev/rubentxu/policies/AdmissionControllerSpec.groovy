package dev.rubentxu.policies

import dev.rubentxu.fixtures.mocks.BaseTest
import dev.rubentxu.policies.interfaces.IResourceInspector
import dev.rubentxu.policies.interfaces.IResourceParser
import dev.rubentxu.policies.interfaces.StructuredResource
import dev.rubentxu.policies.model.InspectionResourceResult
import dev.rubentxu.policies.model.ValidationPolicy
import dev.rubentxu.policies.parsers.CSVInputModelParser
import dev.rubentxu.policies.parsers.JsonInputModelParser
import dev.rubentxu.policies.parsers.YamlInputModelParser
import dev.rubentxu.policies.parsers.YamlRulesParser

import java.nio.file.Path
import java.nio.file.Paths

class AdmissionControllerSpec extends BaseTest {


    def "Debería validar un recurso si pasa la inspección"() {
        given:
        def inspector = Mock(IResourceInspector)
        IResourceParser rulesParser = Mock(YamlRulesParser)
        def controller = new AdmissionController(inspector, rulesParser)
        def resource = Mock(StructuredResource)
        def policy = new ValidationPolicy()

        when:
        def result = controller.admit(resource, policy)

        then:
        1 * inspector.inspectResource(resource, policy) >> new InspectionResourceResult(passed: true, isExecuted: true, policyBreaches: [:])
        result.passed == true
        result.isExecuted == true
        controller.admittedResources.size() == 1
        controller.notExcutedRules.size() == 0
        controller.rejectedResources.size() == 0

    }

    def "Debería rechazar un recurso inválido"() {
        given:
        def inspector = Mock(IResourceInspector)
        IResourceParser rulesParser = Mock(YamlRulesParser)
        def controller = new AdmissionController(inspector, rulesParser)
        def resource = Mock(StructuredResource)
        def policy = new ValidationPolicy()

        when:
        def result = controller.admit(resource, policy)

        then:
        1 * inspector.inspectResource(resource, policy) >> new InspectionResourceResult(passed: false, isExecuted: true, policyBreaches: ['rule1': ['violation1']])
        result.passed == false
        result.isExecuted == true
        controller.admittedResources.size() == 0
        controller.notExcutedRules.size() == 0
        controller.rejectedResources.size() == 1
    }


    def "Debería validar un recurso a partir de un path del Recurso"() {
        given:
        def inspector = Mock(IResourceInspector)
        IResourceParser rulesParser = Mock(YamlRulesParser)
        def controller = new AdmissionController(inspector, rulesParser)
        def parser = Mock(IResourceParser)
        Path resourcePath = Mock(Path)
        Path rulesPath = Mock(Path)
        def resource = Mock(StructuredResource)

        when:
        controller.registerParser(parser)
        def result = controller.admit(resourcePath, rulesPath, "testType")

        then:
        1 * parser.getType() >> "testType"
        1 * parser.parse(resourcePath) >> resource
        1 * inspector.inspectResource(resource, _) >> new InspectionResourceResult(passed: true, policyBreaches: [:])
        result.passed == true
    }

    def "Debería limpiar el historial de recursos admitidos y rechazados"() {
        given:
        def inspector = Mock(IResourceInspector)
        IResourceParser rulesParser = new YamlRulesParser()
        def controller = new AdmissionController(inspector, rulesParser)
        def resource = Mock(StructuredResource)
        def policy = new ValidationPolicy()

        when:
        controller.admit(resource, policy)
        controller.clearHistory()

        then:
        1 * inspector.inspectResource(resource, policy) >> new InspectionResourceResult(passed: true, policyBreaches: [:])
        controller.admittedResources.size() == 0
        controller.notExcutedRules.size() == 0
        controller.rejectedResources.size() == 0
    }

    def 'Debería obtener las violaciones de un recurso con valor vacio'() {
        given:
        def inspector = Mock(IResourceInspector)
        IResourceParser rulesParser = new YamlRulesParser(steps)
        def controller = new AdmissionController(inspector, rulesParser)
        def resource = Mock(StructuredResource)
        def policy = new ValidationPolicy()
        def violations = ['rule1': ['violation1']]

        when:
        controller.admit(resource, policy)
        def result = controller.getViolations(resource)

        then:
        1 * inspector.inspectResource(resource, policy) >> new InspectionResourceResult(passed: false, policyBreaches: violations)
        result == [:]
    }

    def "Debería validar un recurso de kubernetes a partir de un path del Recurso y el path de las reglas"() {
        given:
        def inspector = new ResourceInspector()
        IResourceParser rulesParser = new YamlRulesParser(steps)
        def controller = new AdmissionController(inspector, rulesParser)
        def parser = new YamlInputModelParser(steps)
        Path resourcePath = Path.of('policies/inputs/kubernetes-deployment.yaml')
        Path rulesPath = Path.of('policies/rules/policies_kubernetes.yaml')


        when:
        controller.registerParser(parser)
        def result = controller.admit(resourcePath, rulesPath, "YamlResource")

        then:
        result.passed == true
    }

    def "Debería recoger errores de la validaciones de un recurso kubernetes a partir de un path del Recurso y el path de las reglas"() {
        given:
        def inspector = new ResourceInspector()
        IResourceParser rulesParser = new YamlRulesParser(steps)
        def controller = new AdmissionController(inspector, rulesParser)
        def parser = new YamlInputModelParser(steps)
        Path resourcePath = Path.of('policies/inputs/kubernetes-deployment.yaml')
        Path rulesPath = Path.of('policies/rules/policies_kubernetes_error.yaml')


        when:
        controller.registerParser(parser)
        def result = controller.admit(resourcePath, rulesPath, "YamlResource")

        then:
        result.policyBreaches.size() == 3
        result.policyBreaches['validate-min-replicas'] == 'El número mínimo de réplicas debe ser 2'
        result.policyBreaches['validate-cpu-limit'] == 'Los recursos de límite para CPU deben ser 600m'
        result.policyBreaches['validate-memory-request'] == 'Los recursos de solicitud para memoria deben ser al menos 512Mi'
    }

    def "Debería validar un recurso de kubernetes en formato json a partir de un path del Recurso y el path de las reglas"() {
        given:
        def inspector = new ResourceInspector()
        IResourceParser rulesParser = new YamlRulesParser(steps)
        def controller = new AdmissionController(inspector, rulesParser)
        def parser = new JsonInputModelParser(steps)
        Path resourcePath = Path.of('policies/inputs/kubernetes-deployment.json')
        Path rulesPath = Path.of('policies/rules/policies_kubernetes.yaml')


        when:
        controller.registerParser(parser)
        def result = controller.admit(resourcePath, rulesPath, "JsonResource")

        then:
        result.passed == true
    }

    def "Debería recoger errores de la validaciones de un recurso kubernetes en formato json a partir de un path del Recurso y el path de las reglas"() {
        given:
        def inspector = new ResourceInspector()
        IResourceParser rulesParser = new YamlRulesParser(steps)
        def controller = new AdmissionController(inspector, rulesParser)
        def parser = new JsonInputModelParser(steps)
        Path resourcePath = Path.of('policies/inputs/kubernetes-deployment.json')
        Path rulesPath = Path.of('policies/rules/policies_kubernetes_error.yaml')

        when:
        controller.registerParser(parser)
        def result = controller.admit(resourcePath, rulesPath, "JsonResource")

        then:
        result.policyBreaches.size() == 3
        result.policyBreaches['validate-min-replicas'] == 'El número mínimo de réplicas debe ser 2'
        result.policyBreaches['validate-cpu-limit'] == 'Los recursos de límite para CPU deben ser 600m'
        result.policyBreaches['validate-memory-request'] == 'Los recursos de solicitud para memoria deben ser al menos 512Mi'
    }

    def "Debería validar un recurso si no ejecuta la inspección"() {
        given:
        def inspector = Mock(IResourceInspector)
        IResourceParser rulesParser = Mock(YamlRulesParser)
        def controller = new AdmissionController(inspector, rulesParser)
        def resource = Mock(StructuredResource)
        def policy = new ValidationPolicy()

        when:
        def result = controller.admit(resource, policy)

        then:
        1 * inspector.inspectResource(resource, policy) >> new InspectionResourceResult(passed: true, policyBreaches: [:])
        result.passed == true
        controller.admittedResources.size() == 1
        controller.notExcutedRules.size() == 0
        controller.rejectedResources.size() == 0

    }

    def "Debería validar un recurso CSV a partir de un path del Recurso y el path de las reglas"() {
        given:
        IResourceInspector inspector = new ResourceInspector()
        IResourceParser rulesParser = new YamlRulesParser(steps)
        AdmissionController controller = new AdmissionController(inspector, rulesParser)
        IResourceParser csvParser = new CSVInputModelParser(steps)
        Path resourcePath = Paths.get('policies/inputs/personal.csv')
        Path rulesPath = Paths.get('policies/rules/policies_personal_csv.yaml')

        when:
        controller.registerParser(csvParser)
        def result = controller.admit(resourcePath, rulesPath, "CSVResource")

        then:
        result.passed == true
    }



}