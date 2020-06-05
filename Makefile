build:
	@go build
	@mv hexTool hex
	@cp hex /Users/taoyeqi/WorkSpace/go/bin/
	@rm hex
	@echo "make finish"