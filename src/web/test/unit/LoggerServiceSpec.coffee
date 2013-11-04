define ["../../src/core/LoggerService.js"], (log) ->
  describe "LoggerService", () ->
    it "should log simple messages", () ->
      log.trace("trace")
      log.debug("debug")
      log.info("info")
      log.warn("warn")
      log.error("error")
      log.fatal("fatal")

    it "should log messages with object parameters", () ->
      log.trace("trace", 1, { "name": "test1"})
      log.debug("debug", 2, { "name": "test2"})
      log.info("info", 3, { "name": "test3"})
      log.warn("warn", 4, { "name": "test4"})
      log.error("error", 5, { "name": "test5"})
      log.fatal("fatal", 6, { "name": "test6"})
