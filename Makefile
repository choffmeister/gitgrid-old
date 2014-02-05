run:
	sbt gruntStart run

test:
	sbt clean gruntTest scct:test printCoverage

dist:
	sbt clean gruntBowerInit pack
