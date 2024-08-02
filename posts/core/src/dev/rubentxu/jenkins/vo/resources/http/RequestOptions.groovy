package dev.rubentxu.jenkins.vo.resources.http

class RequestOptions {
    Map<String, Serializable> headers
    String clientCertPath
    String clientKeyPath
    List<Integer> checkStatusCodes
    Boolean ignoreSslErrors = false
    Boolean followRedirects = true
    Boolean printCommand = null // null to use the default value in HTTPieTool
    Boolean logResponse = false
    Boolean mapJsonBody = false
    String saveResponseBodyToFile
    Integer timeout = 30
    FormDataCollection formDataCollection
}
