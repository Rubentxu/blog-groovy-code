package dev.rubentxu.policies.parsers

import dev.rubentxu.policies.interfaces.IResourceParser
import dev.rubentxu.policies.interfaces.StructuredResource
import dev.rubentxu.policies.model.GenericResource

import java.nio.file.Path


class JsonInputModelParser implements IResourceParser {

    private Script steps

    JsonInputModelParser(Script steps) {
        this.steps = steps
    }


    @Override
    StructuredResource parse(Path resourcePath) {

        assert steps.fileExists(file: resourcePath.toString()): "File Json not found in path: ${resourcePath.toString()}"
        Map data = steps.readJSON(file: resourcePath.toString(), encoding: 'UTF-8') as Map

        if(data.kind && data.apiVersion) {
            return new GenericResource(
                    apiVersion: data.apiVersion,
                    kind: data.kind,
                    spec: data.spec,
                    status: data.status
            )
        }

        return new GenericResource(
                apiVersion: 'v1',
                kind: 'YamlResource',
                spec: data,
                status: [:]
        )

    }

    @Override
    String getType() {
        return 'JsonResource'
    }
}