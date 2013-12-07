define ["jquery", "underscore", "knockout"], ($, _, ko) ->
  convertOption = (allBindings, opt) ->
    switch Object.prototype.toString.call(opt)
      when "[object Object]" then opt
      else { value: opt, text: opt }

  getValue = (allBindings, opt) ->
    switch Object.prototype.toString.call(opt)
      when "[object Object]" then opt[allBindings.get("selectizeValueField") || "value"]
      else opt

  setOptions = (selectize, allBindings, opts) ->
    opts = opts or allBindings.get("selectizeOptions")
    type = Object.prototype.toString.call(opts)
    switch type
      when "[object Array]"
        for o in opts
          selectize.addOption(convertOption(allBindings, o))
      when "[object Object]"
        for o in _.map(opts, (v, k) => { value: k, text: v })
          selectize.addOption(o)
      when "[object Function]"
        if ko.isObservable(opts)
          opts.subscribe (changes) ->
            for change in changes
              switch change.status
                when "added" then selectize.addOption(convertOption(allBindings, change.value))
                when "deleted" then selectize.removeOption(getValue(allBindings, change.value))
          , null, "arrayChange"
          setOptions(selectize, allBindings, ko.unwrap(opts))
        else
          setOptions(selectize, allBindings, opts())
      else throw new Error("Invalid object for selectizeOptions")

  ko.bindingHandlers.selectize =
    init: (element, valueAccessor, allBindings) ->
      observable = valueAccessor()
      value = ko.unwrap(observable)

      selectize = $(element).selectize({
        persist: false
        valueField: allBindings.get("selectizeValueField") || "value"
        labelField: allBindings.get("selectizeLabelField") || "text"
      })[0].selectize
      setOptions(selectize, allBindings)

      $(element).on "change", () => observable(selectize.getValue())
      selectize.setValue(value)

    update: (element, valueAccessor, allBindings) ->
      observable = valueAccessor()
      value = ko.unwrap(observable)
      selectize = $(element).selectize()[0].selectize

      selectize.setValue(value)

  ko.bindingHandlers.selectizeMultiText =
    init: (element, valueAccessor, allBindings) ->
      observable = valueAccessor()
      value = ko.unwrap(observable)

      selectize = $(element).selectize({
        delimiter: ","
        persist: false
        valueField: allBindings.get("selectizeValueField") || "value"
        labelField: allBindings.get("selectizeLabelField") || "text"
        create: (input) =>
          value: input
          text: input
      })[0].selectize

      $(element).on "change", () =>
        observable(selectize.getValue().split(","))
      for v in value
        selectize.addOption({ value: v, text: v })
      selectize.setValue(value)

    update: (element, valueAccessor, allBindings) ->
      observable = valueAccessor()
      value = ko.unwrap(observable)
      selectize = element.selectize

      for v in value
        selectize.addOption({ value: v, text: v })
      selectize.setValue(value)

  return null
