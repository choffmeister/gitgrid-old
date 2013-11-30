define [
  "viewmodels/DashboardViewModel"
  "viewmodels/CreateTicketViewModel"
], (
  DashboardViewModel
  CreateTicketViewModel
) -> [
  ["/", "dashboard", DashboardViewModel]
  ["/about", "about"]
  ["/create", "createticket", CreateTicketViewModel]
]
