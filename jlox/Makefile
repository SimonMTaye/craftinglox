python :=python3


.PHONY: format
format:
	mvn rewrite:runNoFork

.PHONY: test
test:
	mvn test 

.PHONY: validate
validate:
	mvn test -Dcheckstyle.skip

gen-types: tools/packages/* tools/*.py
	$(python) -m tools
