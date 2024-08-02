package dev.rubentxu.fixtures.mocks

import spock.lang.Specification

class BaseTest extends Specification {

    protected StepsMock steps

    def setup() {
        steps = new StepsMock()
    }
}
