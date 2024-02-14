package dev.rubentxu.jenkins.tools.interfaces

import dev.rubentxu.jenkins.interfaces.IService

import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpRequest.BodyPublisher

interface IHttpClient extends IService {

    HttpResponse<String> get(URI url)

    HttpResponse<String> get(URI url, HttpRequest.Builder options)

    HttpResponse<String> post(URI url, BodyPublisher payload)

    HttpResponse<String> post(URI url, BodyPublisher payload, HttpRequest.Builder options)

    HttpResponse<String> postFile(URI url, String filePath)

    HttpResponse<String> postFile(URI url, String filePath, HttpRequest.Builder options)

    HttpResponse<String> put(URI url, BodyPublisher payload)

    HttpResponse<String> put(URI url, BodyPublisher payload, HttpRequest.Builder options)

    HttpResponse<String> putFile(URI url, String filePath)

    HttpResponse<String> putFile(URI url, String filePath, HttpRequest.Builder options)

    HttpResponse<String> delete(URI url)

    HttpResponse<String> patch(URI url, BodyPublisher payload)

    HttpResponse<String> delete(URI url, HttpRequest.Builder options)

    URI buildUrl(String baseUrl, String path, Map<String, Serializable> queryParams)

    HttpResponse<String> withBasicAuthUsernamePassword(String usernamePasswordCredentialsId, HttpRequest.Builder body)

    HttpResponse<String> withBasicAuthString(String stringCredentialsId, HttpRequest.Builder body)

    Object mapJsonString(String jsonString)
}