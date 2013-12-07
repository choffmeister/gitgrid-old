define ["jquery", "underscore", "knockout"], ($, _, ko) ->
  detectMode = (element) ->
    tagName = $(element).prop("tagName").toLowerCase()
    switch tagName
      when "select" then "single"
      when "input" then "multi"
      else throw new Error("SelectizeBinding cannot be applied to element of type '#{tagName}")

  ko.bindingHandlers.selectize =
    init: (element, valueAccessor, allBindings) ->
      observable = valueAccessor()
      value = ko.unwrap(observable)
      mode = detectMode(element)

      optionsDefault =
        persist: false
        create: false
      options = $.extend(optionsDefault, allBindings.get("selectizeOptions") || {})
      options.create = true if not options.options

      selectize = $(element).selectize(options)[0].selectize

      switch mode
        when "single"
          $(element).on "change", () => observable(selectize.getValue())
          selectize.setValue(value)
        when "multi"
          $(element).on "change", () =>
            observable(selectize.getValue().split(","))
          for v in value
            selectize.addOption({ value: v, text: v })
          selectize.setValue(value)

    update: (element, valueAccessor, allBindings) ->
      observable = valueAccessor()
      value = ko.unwrap(observable)
      mode = detectMode(element)
      selectize = $(element).selectize()[0].selectize

      switch mode
        when "single"
          selectize.setValue(value)
        when "multi"
          for v in value
            selectize.addOption({ value: v, text: v })
          selectize.setValue(value)

  return null
