define [
  "viewmodels/DashboardViewModel"
  "viewmodels/CreateTicketViewModel"
  "viewmodels/ShowTicketViewModel"
], (
  DashboardViewModel
  CreateTicketViewModel
  ShowTicketViewModel
) -> [
  ["/", "dashboard", DashboardViewModel]
  ["/about", "about"]
  ["/create", "createticket", CreateTicketViewModel]
  ["/tickets/{ticketId}", "showticket", ShowTicketViewModel]
]
