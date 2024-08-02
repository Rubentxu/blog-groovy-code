package dev.rubentxu.policies.interfaces


import dev.rubentxu.policies.model.InspectionResourceResult
import dev.rubentxu.policies.model.ValidationPolicy

import java.nio.file.Path

interface IAdmissionController {
    /**
     * Intenta admitir un recurso estructurado.
     * @param resource El recurso a ser admitido.
     * @param policy La política de validación a aplicar.
     * @return el resultado de la inspección.
     */
    InspectionResourceResult admit(StructuredResource resource, ValidationPolicy policy)

    /**
     * Obtiene la lista de recursos que han sido admitidos.
     * @return Una lista no modificable de recursos admitidos.
     */
    List<StructuredResource> getAdmittedResources()

    /**
     * Obtiene la lista de recursos que han sido rechazados.
     * @return Una lista no modificable de recursos rechazados.
     */
    List<StructuredResource> getRejectedResources()

    /**
     * Obtiene las violaciones de reglas para un recurso específico.
     * @param resource El recurso para el cual se quieren obtener las violaciones.
     * @return Un mapa donde las claves son los nombres de las reglas y los valores son los mensajes de violación.
     */
    Map<String, List<String>> getViolations(StructuredResource resource)

    /**
     * Limpia el historial de recursos admitidos y rechazados.
     */
    void clearHistory()

    /**
     * Registra un parser de recursos.
     * @param parser El parser a registrar.
     */
    void registerParser(IResourceParser parser)

    /**
     * Desregistra un parser de recursos.
     * @param parserType El tipo de parser a desregistrar.
     */
    void unregisterParser(String parserType)

    /**
     * Intenta admitir un recurso a partir de un flujo de entrada.
     * @param resourcePath La ruta del recurso a ser admitido.
     * @param rulesPath La ruta de las reglas de validación a aplicar.
     * @param parserType El tipo de parser a utilizar.
     * @return El resultado de la inspección.
     */
    InspectionResourceResult admit(Path resourcePath, Path rulesPath, String parserType)

}