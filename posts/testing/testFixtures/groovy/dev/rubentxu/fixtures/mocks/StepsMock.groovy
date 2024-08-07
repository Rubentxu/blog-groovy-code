package dev.rubentxu.fixtures.mocks

import dev.rubentxu.fixtures.mocks.utils.CSVReaderUtil
import groovy.io.FileType
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.yaml.snakeyaml.Yaml

import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap

//@CompileStatic
class StepsMock extends Script {

    Map<String, Object> dynamicProps
    Map<String, Integer> invocationCounts
    private MethodInvocationRecorder recorder = new MethodInvocationRecorder()
//    ProviderCredentialsMock credentialsProvider

    MethodInvocationRecorder validate() {
        return recorder
    }

    StepsMock() {
        dynamicProps = [
                env                  : [
                        JENKINS_URL: 'https://jenkins.rubentxu.dev',
                        JOB_NAME   : 'pruebas/blog/develop',
                        WORKSPACE  : '.',
                ],
                params               : [:],
                currentBuild         : [:],
                timeout              : this.&defaultMethodClosure,
                configFileProvider   : this.&defaultMethodClosure,
                ansiColor            : this.&defaultMethodClosure,
                container            : this.&defaultMethodClosure,
                dir                  : this.&dirMock,
                readYaml             : this.&readYamlMock,
                readJSON             : this.&readJsonMock,
                readFile             : this.&readFileMock,
                readProperties       : this.&readPropertiesMock,
                fileExists           : this.&fileExistsMock,
                withCredentials      : this.&defaultMethodClosure,
                withEnv              : this.&defaultMethodClosure,
                withSonarQubeEnv     : this.&defaultMethodClosure,
                node                 : this.&defaultMethodClosure,
                stage                : this.&defaultMethodClosure,
                retry                : this.&retryMock,
                libraryResource      : this.&libraryResourceMock,
                archiveArtifacts     : {},
                configFile           : {},
                waitUntil            : this.&waitUntilMock,
                writeFile            : {},
                writeJSON            : this.&writeJSONMock,
                writeYaml            : this.&writeYAMLMock,
                writeMavenPom        : {},
                usernamePassword     : this.&usernamePasswordMock,
                usernameColonPassword: this.&usernameColonPasswordMock,
                string               : this.&tokenCredentialMock,
                token                : this.&tokenCredentialMock,
                unstash              : {},
                publishHTML          : {},
                parallel             : this.&parallelMock,
                emailext             : {},
                sleep                : {},
                string               : {},
                file                 : {},
                sh                   : { args -> if (args?.returnStatus) { return 0 } else { return '' } },
                echo                 : { System.out.println(it.toString()) }, // groovylint-disable-line SystemOutPrint
                error                : { String message -> throw new Exception(message) },
                catchError           : this.&defaultMethodClosure,
                readCSV              : this.&readCSVMock,

        ]

        invocationCounts = new ConcurrentHashMap()
//        credentialsProvider = new ProviderCredentialsMock()

    }

    def usernamePasswordMock(Map args) {
        String usernameVariable = args.usernameVariable
        String passwordVariable = args.passwordVariable
        this.env[usernameVariable] = 'usernameMock'
        this.env[passwordVariable] = 'passwordMock'
    }

    def usernameColonPasswordMock(Map args) {
        String variable = args.variable
        this.env[variable] = 'userMock:passwordMock'
    }

    def tokenCredentialMock(Map args) {
        String variable = args.variable
        this.env[variable] = 'secretToken'
    }

    def withCredentialsMock(Map args, Closure body) {
        def credentialsId = args.credentialsId
        def credentials = credentialsProvider.getCredentials(credentialsId)
        dynamicProps.env['CREDENTIALS'] = credentials
        body.delegate = this
        return body()
    }


    def defaultMethodClosure(ignored, Closure body) {
        body.delegate = this
        return body()
    }

    def catchErrorMock(ignored, messageIgnored, Closure body) {
        return defaultMethodClosure(ignored, body)
    }

    def timeoutMock(_, closure) {
        closure.delegate = this
        return closure()
    }


    /**
     * Simulate a real invocation for the jenkins retry step
     * @param times number of times to retry
     * @param body closure to be executed
     * @return the output of the closure
     * @throws Exception if the closure fails after the number of retries
     */
    def retryMock(int times, body) {
        def retries = 0
        while (retries < times) {
            try {
                return body()
            } catch (Exception e) {
                retries++
            }
        }
        throw new Exception("Retry failed after ${times} attempts")
    }

    /**
     * Simulate a real invocation for the jenkins libraryResource step
     * @param args jenkins libraryResource step arguments
     * @return the content of the file (or script) in the library
     */
    String libraryResourceMock(args) {
        assert (args instanceof Map && args.resource != null) || args instanceof String
        return '#!/bin/bash\necho "mocked script content"'
    }

    def dirMock(String path, Closure block) {
        def currentPwd = dynamicProps.env?.PWD
        def currentDir = currentPwd ?: '.'
        dynamicProps.env['PWD'] = Paths.get(currentDir).toAbsolutePath().resolve(path).toString()
        def result = block()
        if (currentPwd) {
            dynamicProps.env.PWD = currentPwd
        } else {
            (dynamicProps.env as Map).remove('PWD')
        }
        return result
    }

    def parallelMock(Map args) {
        args.findAll { k, v -> k != 'failFast' }.each { k, v -> v() }
    }

    def readYamlMock(Map args, baseDir = '') {
        if (args?.file?.contains('build')) {
            args.file = args.file.replace('build/', '')
        }
        String content = args.text ?: this.getResourceContent(Paths.get(baseDir as String, args.file as String) as String)
        if (content?.contains('---')) {
            def result = new Yaml().loadAll(content).toList()
            result.size() == 1 ? result[0] : result
        } else {
            return new Yaml().load(content)
        }
    }

    def readJsonMock(Map args, baseDir = '') {
        String content = args.text ?: this.getResourceContent(Paths.get(baseDir as String, args.file as String) as String)
        return new JsonSlurper().parseText(content ?: '')
    }

    def readPropertiesMock(Map args, baseDir = '') {
        String content = args.text ?: this.getResourceContent(Paths.get(baseDir as String, args.file as String) as String)
        Properties properties = new Properties()
        if (content == null) {
            throw new Exception("File not found in resources: ${args.file} for Mock readPropertiesMock")
        }
        properties.load(new StringReader(content))
        Map<String, String> map = new HashMap<String, String>();
        properties.each { k, v -> map[k] = v }
        return map
    }

    def findFilesMock(Map args, baseDir = 'topics') {
        Path parentPath = Paths.get(getClass().getClassLoader().getResource(baseDir as String).toURI()).resolve('../').normalize()

        List<File> files = []
        parentPath.toFile().eachFileRecurse(FileType.FILES) {
            def relativePath = parentPath.relativize(it.toPath())
            if (matchesGlobExpression(args.glob as String, relativePath)) {
                files.add(relativePath.toFile())
            }
        }
        return files
    }

    def readCSVMock(Map args, baseDir = '') {
        URL resourceUrl = getClass().getClassLoader().getResource(Paths.get(baseDir as String, args.file as String).toString())
        if (resourceUrl == null) {
            throw new FileNotFoundException("Archivo no encontrado: " + args.file)
        }
        String filePath = resourceUrl.getFile()
        List<Map> content = CSVReaderUtil.readCSV(filePath)
        return content
    }


    private Boolean matchesGlobExpression(String globPattern, Path path) {
        return FileSystems
                .getDefault()
                .getPathMatcher("glob:${globPattern}")
                .matches(path)
    }

    def readFileMock(Map args, String baseDir = '') {
        return this.getResourceContent(Paths.get(baseDir, args.file as String) as String)
    }

    def writeJSONMock(Map args) {
        if (args.returnText) {
            JsonBuilder builder = new JsonBuilder()
            builder(args.json)
            builder.toString()
        }
    }

    def waitUntilMock(Closure body) {
        for (int i = 0; i < 20; i++) {
            if (body.call()) {
                return
            }
        }
        throw new Exception('Wait until did not return true after 20 retries')
    }

    def writeYAMLMock(Map args) {
        if (args.returnText) {
            return new Yaml().load(args.data as String ?: args.datas as String)
        }
    }

    def fileExistsMock(Map args, String baseDir = '') {
        if (args?.file?.contains('build')) {
            args.file = args.file.replace('build/', '')
        }
        Path path = Paths.get(baseDir, args.file as String)
        println("Resolve fileExistsMock in path: ${path.toAbsolutePath()}")
        return getClass().getClassLoader().getResource(path.toString()) != null ? Boolean.TRUE : Boolean.FALSE
    }


    Object sleep(long arg) {
        // To simulate steps.sleep(time) in a pipeline
        methodMissing('sleep', [arg])
    }

    def getAllDynamicProps() {
        return dynamicProps
    }

    @Override
    def getProperty(String propName) {
        return dynamicProps[propName]
    }

    @Override
    void setProperty(String propName, val) {
        dynamicProps[propName] = val
    }

    @Override
    Object run() {
        return null
    }

    def methodMissing(String methodName, args) {
        // Incrementa el contador de invocaciones del método
        incrementInvocationCount(methodName)

        def prop = dynamicProps[methodName]
        if (prop instanceof Closure) {
            def result = prop(*args)
            recordMethodInvocation(methodName, args, result)
            return result
        }
        throw new Exception("\u001B[1;31m************ Method Missing with name $methodName and args $args **************\u001B[0m")
    }

    private void incrementInvocationCount(String methodName) {
        // Si el método no ha sido invocado antes, inicializa su contador a 0
        if (!invocationCounts.containsKey(methodName)) {
            invocationCounts[methodName] = 0
        }
        // Incrementa el contador de invocaciones del método
        invocationCounts[methodName] = invocationCounts[methodName] + 1
    }

    private void recordMethodInvocation(String methodName, args, result) {
        // Si el método no ha sido registrado antes, crea una nueva lista para él
        if (!this.recorder.containsKey(methodName)) {
            this.recorder.createList(methodName)
        }
        def argumentsResolved = recorder.getArgs(args)

        // Crea una nueva instancia de MethodInvocation dependiendo del tipo de argumentos
        MethodInvocation methodInvocation = argumentsResolved instanceof Map
                ? new NamedArgsMethodInvocation(methodName, argumentsResolved, result)
                : new PositionalArgsMethodInvocation(methodName, argumentsResolved, result)
        // Registra la invocación del método
        this.recorder.addMock(methodName, methodInvocation)
    }

    String getResourceContent(String file) {
        return getClass().getClassLoader().getResource(file.replaceAll('\\\\', '/'))?.text
    }


}
