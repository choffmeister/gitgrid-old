define ["underscore", "api", "ViewModelBase", "../models/Ticket"], (_, api, ViewModelBase, Ticket) ->
  class TicketsViewModel extends ViewModelBase
    init: () =>
      api.get("/tickets").then (data) => @tickets = _.map(data, (t) -> new Ticket(t))

  class TicketViewModel extends ViewModelBase
    init: (args) =>
      if args.ticketId?
        api.get("/tickets/#{args.ticketId}").then (data) => @ticket = new Ticket(data)
      else
        @ticket = new Ticket()
        @done()

    add: () => if @validate()
      api.post("/tickets", @ticket.toJS()).then (ticket) =>
        @notifySuccess("Created", "Successfully created new ticket")
        @redirect("/tickets/#{ticket.id}")

    modify: () => if @validate()
      api.put("/tickets/#{@ticket.id()}", @ticket.toJS()).then () =>
        @notifySuccess("Edited", "Successfully edited ticket")
        @redirect("/tickets/#{@ticket.id()}")

    remove: () =>
      api.delete("/tickets/#{@ticket.id()}").then () =>
        @notifySuccess("Deleted", "Successfully deleted ticket")
        @redirect("/tickets")

  return {
    TicketsViewModel: TicketsViewModel
    TicketViewModel: TicketViewModel
  }
