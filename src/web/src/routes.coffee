define [
  "viewmodels/DashboardViewModel"
  "viewmodels/CreateTicketViewModel"
  "viewmodels/ShowTicketViewModel"
  "viewmodels/ThrowingViewModel"
], (
  DashboardViewModel
  CreateTicketViewModel
  ShowTicketViewModel
  ThrowingViewModel
) -> [
  ["/", "dashboard", DashboardViewModel]
  ["/about", "about"]
  ["/create", "createticket", CreateTicketViewModel]
  ["/tickets/{ticketId}", "showticket", ShowTicketViewModel]
  ["/throw", "throwing", ThrowingViewModel]
]
