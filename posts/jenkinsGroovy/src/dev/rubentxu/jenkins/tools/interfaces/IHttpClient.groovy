package dev.rubentxu.jenkins.tools.interfaces

import dev.rubentxu.jenkins.interfaces.ITool
import dev.rubentxu.jenkins.vo.resources.http.HttpResponse
import dev.rubentxu.jenkins.vo.resources.http.RequestOptions

interface IHttpClient extends ITool {

    HttpResponse<String> get(URI url)

    HttpResponse<String> get(URI url, RequestOptions options)

    HttpResponse<String> post(URI url, Map payload)

    HttpResponse<String> post(URI url, Map payload, RequestOptions options)

    HttpResponse<String> postFile(URI url, String filePath)

    HttpResponse<String> postFile(URI url, String filePath, RequestOptions options)

    HttpResponse<String> put(URI url, Map payload)

    HttpResponse<String> put(URI url, Map payload, RequestOptions options)

    HttpResponse<String> putFile(URI url, String filePath)

    HttpResponse<String> putFile(URI url, String filePath, RequestOptions options)

    HttpResponse<String> delete(URI url)

    HttpResponse<String> patch(URI url, Map payload)

    HttpResponse<String> delete(URI url, RequestOptions options)

    URI buildUrl(String baseUrl, String path, Map<String, Serializable> queryParams)

    HttpResponse<String> withBasicAuthUsernamePassword(String usernamePasswordCredentialsId, RequestOptions body)

    HttpResponse<String> withBasicAuthString(String stringCredentialsId, RequestOptions body)

    Object mapJsonString(String jsonString)
}