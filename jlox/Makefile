python :=python3


.PHONY: format
format:
	mvn rewrite:runNoFork

.PHONY: test
test:
	mvn test 

.PHONY: validate
validate:
	mvn validate

gen-types-helper: tools/packages/* tools/*.py 
	$(python) -m tools

gen-types: gen-types-helper format
