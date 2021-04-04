SHELL := bash

.PHONY: test dev

fmt:
	clojure -M:cljfmt-fix

check/outdated:
	clojure -M:outdated

check/clj-kondo:
	clojure -M:kondo --lint src test

check/kibit:
	clojure -M:kibit-fix

check/eastwood:
	clojure -M:eastwood

lint: check/outdated check/clj-kondo check/kibit check/eastwood

test:
	clojure -M:test

main:
	clojure -M:main

web:
	clojure -M:web