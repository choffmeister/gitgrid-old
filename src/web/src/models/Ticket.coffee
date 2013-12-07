define ["ModelBase"], (ModelBase) ->
  class Ticket extends ModelBase
    config: () ->
      id:
        default: 0
      title:
        default: ""
        validation:
          required: true
          maxLength: 128
      description:
        default: ""
        validation:
          required: true
          maxLength: 65536
      creatorId:
        default: 0
      createdAt:
        default: new Date()
