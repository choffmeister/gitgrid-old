define ["ModelBase"], (ModelBase) ->
  class Ticket extends ModelBase
    meta: () ->
      id:
        defaultValue: 0
      title:
        defaultValue: ""
        validation:
          required: true
          maxLength: 128
      description:
        defaultValue: ""
        validation:
          required: true
          maxLength: 65536
      creatorId:
        defaultValue: 0
      createdAt:
        defaultValue: new Date()
