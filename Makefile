.PHONY: lint test coverage ci

lint:
	./gradlew ktlintCheck detekt

test:
	./gradlew testDebugUnitTest

coverage:
	./gradlew testDebugUnitTest jacocoTestReport

ci:
	./gradlew ktlintCheck detekt testDebugUnitTest jacocoTestReport
