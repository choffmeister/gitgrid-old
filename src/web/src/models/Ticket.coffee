define ["jquery", "ModelBase"], ($, ModelBase) ->
  class Ticket extends ModelBase
    default: () ->
      id: 0
      title: ""
      description: ""
      creatorId: 0
      createdAt: new Date()

    validation: () ->
      title:
        required: true
        maxLength: 64
      description:
        required: true
