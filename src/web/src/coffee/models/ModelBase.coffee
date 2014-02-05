define ["jquery", "underscore", "knockout"], ($, _, ko) ->
  class ModelBase
    constructor: (data) ->
      # merge default values and given concrete data (concrete data wins)
      defaultValues = _.object(_.map(@meta(), (def, name) -> [name, def.defaultValue]))
      raw = $.extend({}, defaultValues, data)

      # wrap raw values in observables
      ko.mapping.fromJS(raw, {}, this)

      # annotate with validation rules
      validation = _.object(_.map(@meta(), (def, name) -> [name, def.validation]))
      for property, rule of validation
        this[property].extend(rule)

    fromJS: (data) => ko.mapping.fromJS(data, this)
    toJS: () => ko.mapping.toJS(this)

    # Implement in subclasses to configure things like default values,
    # validation etc.
    meta: () -> {}

    validate: () =>
      errors = ko.validation.group(this)
      errors.showAllMessages()
      errors().length == 0
