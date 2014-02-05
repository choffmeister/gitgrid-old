define ["jquery", "knockout"], ($, ko) ->
  getAutoHeight = (element) ->
    width = $(element).width()
    clone = $(element).clone()
      .css({
        "position": "absolute"
        "visibility": "hidden"
        "height": "auto"
        "width": "#{width}px"
        "max-height": "9999px"
      })
      .addClass("slideClone")
      .appendTo("body")
    height = $(clone).height()
    $(clone).remove()

    return height

  ko.bindingHandlers.slideVisible =
    init: (element, valueAccessor, allBindings) ->
      value = ko.unwrap(valueAccessor())
      duration = allBindings.get("slideDuration") or 400

      $(element)
        .css({
          "height": if value is true then "auto" else "0px"
          "max-height": "9999px"
          "overflow": "hidden"
        })
        .css({
          "-webkit-transition": "height #{duration / 1000.0}s ease-in-out"
          "-moz-transition": "height #{duration / 1000.0}s ease-in-out"
          "-ms-transition": "height #{duration / 1000.0}s ease-in-out"
          "-o-transition": "height #{duration / 1000.0}s ease-in-out"
        })

    update: (element, valueAccessor, allBindings) ->
      value = ko.unwrap(valueAccessor())

      if value is true
        $(element).css("height", getAutoHeight(element))
      else
        $(element).css("height", 0)

  return null
