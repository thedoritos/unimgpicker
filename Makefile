format:
	find ./Assets -name "*.cs" | xargs clang-format -i

.PHONY: format
