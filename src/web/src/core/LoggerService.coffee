define ["config"], (config) ->
  logLevel = config.logging.verbosity
  levelMap = ["FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE"]
  consoleLevelMap = [console.error, console.error, console.warn, console.info, console.log, console.log]

  class LoggerService
    trace: (message, args...) => @message.apply(this, [5, message].concat(args))
    debug: (message, args...) => @message.apply(this, [4, message].concat(args))
    info: (message, args...) => @message.apply(this, [3, message].concat(args))
    warn: (message, args...) => @message.apply(this, [2, message].concat(args))
    error: (message, args...) => @message.apply(this, [1, message].concat(args))
    fatal: (message, args...) => @message.apply(this, [0, message].concat(args))
    message: (level, message, args...) =>
      consoleLevelMap[level].apply(console, ["[#{levelMap[level]}] #{message}"].concat(args)) if level <= logLevel

  return new LoggerService()
