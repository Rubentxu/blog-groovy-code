package dev.rubentxu.jenkins.tools

import dev.rubentxu.jenkins.Steps
import dev.rubentxu.jenkins.interfaces.IConfigClient
import dev.rubentxu.jenkins.tools.interfaces.IHttpClient
import dev.rubentxu.jenkins.interfaces.IPipelineContext
import groovy.json.JsonSlurper
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublisher
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.nio.file.Paths

class HttpClientTool extends Steps implements IHttpClient {

    private final HttpClient client
    private Path clientCertPath
    private Boolean ignoreSslErrors
    private Integer timeout

    HttpClientTool(IPipelineContext pipeline) {
        super(pipeline)
        initialize(pipeline.getConfigClient())
    }

    @Override
    public HttpResponse<String> get(URI url) {
        HttpRequest request = HttpRequest.newBuilder(url).GET().build()
        return sendRequest(request)
    }

    @Override
    public HttpResponse<String> get(URI url, HttpRequest.Builder options) {
        HttpRequest request = options.uri(url).GET().build()
        return sendRequest(request)
    }

    @Override
    public HttpResponse<String> post(URI url, BodyPublisher payload) {
        HttpRequest request = HttpRequest.newBuilder(url).POST(payload).build()
        return sendRequest(request)
    }

    @Override
    public HttpResponse<String> post(URI url, BodyPublisher payload, HttpRequest.Builder options) {
        HttpRequest request = options.uri(url).POST(payload).build()
        return sendRequest(request)
    }

    @Override
    public HttpResponse<String> postFile(URI url, String filePath) {
        Path file = Paths.get(filePath)
        HttpRequest request = HttpRequest.newBuilder(url).POST(BodyPublishers.ofFile(file)).build()
        return sendRequest(request)
    }

    @Override
    public HttpResponse<String> postFile(URI url, String filePath, HttpRequest.Builder options) {
        Path file = Paths.get(filePath)
        HttpRequest request = options.uri(url).POST(BodyPublishers.ofFile(file)).build()
        return sendRequest(request)
    }

    @Override
    public HttpResponse<String> put(URI url, BodyPublisher payload) {
        HttpRequest request = HttpRequest.newBuilder(url).PUT(payload).build()
        return sendRequest(request)
    }

    @Override
    public HttpResponse<String> put(URI url, BodyPublisher payload, HttpRequest.Builder options) {
        HttpRequest request = options.uri(url).PUT(payload).build()
        return sendRequest(request)
    }

    @Override
    public HttpResponse<String> putFile(URI url, String filePath) {
        Path file = Paths.get(filePath)
        HttpRequest request = HttpRequest.newBuilder(url).PUT(BodyPublishers.ofFile(file)).build()
        return sendRequest(request)
    }

    @Override
    public HttpResponse<String> putFile(URI url, String filePath, HttpRequest.Builder options) {
        Path file = Paths.get(filePath)
        HttpRequest request = options.uri(url).PUT(BodyPublishers.ofFile(file)).build()
        return sendRequest(request)
    }

    @Override
    public HttpResponse<String> delete(URI url) {
        HttpRequest request = HttpRequest.newBuilder(url).DELETE().build()
        return sendRequest(request)
    }

    @Override
    public HttpResponse<String> delete(URI url, HttpRequest.Builder options) {
        HttpRequest request = options.uri(url).DELETE().build()
        return sendRequest(request)
    }

    @Override
    public HttpResponse<String> patch(URI url, BodyPublisher payload) {
        HttpRequest request = HttpRequest.newBuilder(url).method("PATCH", payload).build()
        return sendRequest(request)
    }

    @Override
    public URI buildUrl(String baseUrl, String path, Map<String, Serializable> queryParams) {
        String encodedQueryParams = queryParams.collect { key, value ->
            "${URLEncoder.encode(key, StandardCharsets.UTF_8)}=${URLEncoder.encode(value.toString(), StandardCharsets.UTF_8)}"
        }.join('&')

        return new URI("${baseUrl}${path}?${encodedQueryParams}")
    }


    @Override
    public HttpResponse<String> withBasicAuthUsernamePassword(String usernamePasswordCredentialsId, HttpRequest.Builder body) {
        // Obtén las credenciales de usuario y contraseña utilizando el usernamePasswordCredentialsId
        String username = "username" // reemplaza esto con el código para obtener el nombre de usuario
        String password = "password" // reemplaza esto con el código para obtener la contraseña

        String auth = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes())

        HttpRequest request = body.header("Authorization", auth).build()
        return sendRequest(request)
    }

    @Override
    public HttpResponse<String> withBasicAuthString(String stringCredentialsId, HttpRequest.Builder body) {

        steps.withCredentials([
                steps.string(credentialsId: stringCredentialsId, variable: 'HTTP_BASIC_AUTH_TOKEN')
        ]) {
            return body()
        }
        String credentials = steps.env.HTTP_BASIC_AUTH_TOKEN
        String auth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes())

        HttpRequest request = body.header("Authorization", auth).build()
        return sendRequest(request)
    }


    @Override
    public Object mapJsonString(String jsonString) {
        return new JsonSlurper().parseText(jsonString)
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return client.send(request, BodyHandlers.ofString())
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e)
        }
    }

    @Override
    void initialize(IConfigClient configClient) {
        clientCertPath = configClient.get('httpClient.clientCertPath')
        ignoreSslErrors = configClient.get('httpClient.ignoreSslErrors')
        timeout = configClient.get('httpClient.timeout')
    }
}