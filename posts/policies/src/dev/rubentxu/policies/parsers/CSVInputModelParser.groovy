package dev.rubentxu.policies.parsers

import dev.rubentxu.policies.interfaces.IResourceParser
import dev.rubentxu.policies.interfaces.StructuredResource
import dev.rubentxu.policies.model.GenericResource

import java.nio.file.Path

class CSVInputModelParser implements IResourceParser {

    private Script steps

    CSVInputModelParser(Script steps) {
        this.steps = steps
    }

    @Override
    StructuredResource parse(Path resourcePath) {

        assert steps.fileExists(file: resourcePath.toString()): "File CSV not found in path: ${resourcePath.toString()}"
        List<Map> data = steps.readCSV(file: resourcePath.toString())

        GenericResource resource = new GenericResource(
                apiVersion: 'v1',
                kind: 'CSVResource',
                spec: [rows: data],
                status: [:]
        )
        return resource

    }

    @Override
    String getType() {
        return 'CSVResource'
    }
}