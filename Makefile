format:
	${CURDIR}/.ci/run-format ${CURDIR}/Assets

lint: format
	${CURDIR}/.ci/run-lint ${CURDIR}/Assets

.PHONY: format lint
