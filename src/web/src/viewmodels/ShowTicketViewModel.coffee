define ["log", "api", "ViewModelBase"], (log, api, ViewModelBase) ->
  class ShowTicketViewModel extends ViewModelBase
    init: (args) =>
      api.get("/tickets/#{args.ticketId}").then (ticket) =>
        @ticket = ticket
