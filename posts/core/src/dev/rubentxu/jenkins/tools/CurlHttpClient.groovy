package dev.rubentxu.jenkins.tools

import dev.rubentxu.jenkins.Steps
import dev.rubentxu.jenkins.interfaces.IConfigClient
import dev.rubentxu.jenkins.interfaces.IPipelineContext
import dev.rubentxu.jenkins.tools.interfaces.IHttpClient
import dev.rubentxu.jenkins.vo.resources.http.HttpResponse
import dev.rubentxu.jenkins.vo.resources.http.RequestOptions

class CurlHttpClient extends Steps implements IHttpClient {
    private String TOOL_NAME = 'curl'
    private String defaultArgs
    private Boolean debugMode

    CurlHttpClient(IPipelineContext pipeline) {
        super(pipeline)
    }

    @Override
    HttpResponse<String> get(URI url) {
        String result = execute('GET', [url.toString()], true)
        return new HttpResponse<String>(body: result, statusCode: 200, headers: [:])
    }

    @Override
    HttpResponse<String> get(URI url, RequestOptions options) {
        String result = execute('GET', [url.toString()], true)

    }

    @Override
    HttpResponse<String> post(URI url, Map payload) {
        return post(url, payload, new RequestOptions())
    }

    @Override
    HttpResponse<String> post(URI url, Map payload, RequestOptions options) {
        String result = execute('POST', ['-d', payload.collect { k, v -> "${k}=${v}" }.join('&')], true)
        return new HttpResponse<String>(body: result, statusCode: 200, headers: [:])
    }

    @Override
    HttpResponse<String> postFile(URI url, String filePath) {
        String result = execute('POST', ['-d', "@${filePath}"], true)
        return new HttpResponse<String>(body: result, statusCode: 200, headers: [:])
    }

    @Override
    HttpResponse<String> postFile(URI url, String filePath, RequestOptions options) {
        String result = execute('POST', ['-d', "@${filePath}"], true)
        return new HttpResponse<String>(body: result, statusCode: 200, headers: [:])
    }

    @Override
    HttpResponse<String> put(URI url, Map payload) {
        return put(url, payload, new RequestOptions())
    }

    @Override
    HttpResponse<String> put(URI url, Map payload, RequestOptions options) {
        String result = execute('PUT', ['-d', payload.collect { k, v -> "${k}=${v}" }.join('&')], true)
        return new HttpResponse<String>(body: result, statusCode: 200, headers: [:])
    }

    @Override
    HttpResponse<String> putFile(URI url, String filePath) {
        String result = execute('PUT', ['-d', "@${filePath}"], true)
        return new HttpResponse<String>(body: result, statusCode: 200, headers: [:])
    }

    @Override
    HttpResponse<String> putFile(URI url, String filePath, RequestOptions options) {
        String result = execute('PUT', ['-d', "@${filePath}"], true)
        return new HttpResponse<String>(body: result, statusCode: 200, headers: [:])
    }

    @Override
    HttpResponse<String> delete(URI url) {
        return delete(url, new RequestOptions())
    }

    @Override
    HttpResponse<String> patch(URI url, Map payload) {
        return patch(url, payload, new RequestOptions())
    }

    @Override
    HttpResponse<String> delete(URI url, RequestOptions options) {
        String result = execute('DELETE', [url.toString()], true)
        return new HttpResponse<String>(body: result, statusCode: 200, headers: [:])
    }

    @Override
    URI buildUrl(String baseUrl, String path, Map<String, Serializable> queryParams) {
        String urlWithQueryParams = baseUrl + path + '?' + queryParams.collect { k, v -> "${k}=${v}" }.join('&')
        return new URI(urlWithQueryParams)
    }

    @Override
    HttpResponse<String> withBasicAuthUsernamePassword(String usernamePasswordCredentialsId, RequestOptions body) {

    }

    @Override
    HttpResponse<String> withBasicAuthString(String stringCredentialsId, RequestOptions body) {
        // Implementa la funcionalidad aquí
    }

    @Override
    Object mapJsonString(String jsonString) {
        // Implementa la funcionalidad aquí
    }

    @Override
    String execute(String taskName, List<String> options) {
        def args = defaultArgs + options.join(' ')
        def workDir = new File(pomXmlPath).getParent() ?: '.'
        steps.dir(workDir) {
            String tool = debugMode ? "${TOOL_NAME} -v" : TOOL_NAME
            steps.sh(script: "${tool} ${taskName} ${args}".trim(), returnStdout: true)
        }
    }

    @Override
    void configure(IConfigClient configClient) {
        this.defaultArgs = configClient.optional('httpClient.defaultArgs', '')
        this.debugMode = configClient.optional('httpClient.debugMode', false)
    }

    @Override
    void initialize() {

    }
}