define ["config"], (config) ->
  logLevel = config.logging.verbosity
  levelMap = ["FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE"]
  consoleLevelMap1 = [
    (message) -> console.error(message),
    (message) -> console.error(message),
    (message) -> console.warn(message),
    (message) -> console.info(message),
    (message) -> console.log(message),
    (message) -> console.log(message)
  ]
  consoleLevelMap2 = [
    (message, args) -> console.error(message, args),
    (message, args) -> console.error(message, args),
    (message, args) -> console.warn(message, args),
    (message, args) -> console.info(message, args),
    (message, args) -> console.log(message, args),
    (message, args) -> console.log(message, args)
  ]

  class LoggerService
    trace: (message, args...) => @message.apply(this, [5, message].concat(args))
    debug: (message, args...) => @message.apply(this, [4, message].concat(args))
    info: (message, args...) => @message.apply(this, [3, message].concat(args))
    warn: (message, args...) => @message.apply(this, [2, message].concat(args))
    error: (message, args...) => @message.apply(this, [1, message].concat(args))
    fatal: (message, args...) => @message.apply(this, [0, message].concat(args))
    message: (level, message, args...) =>
      logMethod1 = consoleLevelMap1[level]
      logMethod2 = consoleLevelMap2[level]
      switch args.length
        when 0 then logMethod1("[#{levelMap[level]}] #{message}") if level <= logLevel
        when 1 then logMethod2("[#{levelMap[level]}] #{message}", args[0]) if level <= logLevel
        else logMethod2("[#{levelMap[level]}] #{message}", args) if level <= logLevel

  return new LoggerService()
