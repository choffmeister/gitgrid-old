define [
  "viewmodels/TicketViewModels"
  "viewmodels/ProjectViewModels"
  "viewmodels/RepositoryBrowsingViewModel"
  "viewmodels/ThrowingViewModel"
], (
  TicketViewModels
  ProjectViewModels
  RepositoryBrowsingViewModel
  ThrowingViewModel
) -> [
  ["/", "dashboard"]
  ["/about", "about"]
  ["/tickets", "tickets/list", TicketViewModels.TicketsViewModel]
  ["/tickets/create", "tickets/create", TicketViewModels.TicketViewModel]
  ["/tickets/{ticketId}/edit", "tickets/edit", TicketViewModels.TicketViewModel]
  ["/tickets/{ticketId}", "tickets/show", TicketViewModels.TicketViewModel]
  ["/projects", "projects/list", ProjectViewModels.ProjectsViewModel]
  ["/projects/create", "projects/create", ProjectViewModels.ProjectViewModel]
  ["/projects/{projectId}/edit", "projects/edit", ProjectViewModels.ProjectViewModel]
  ["/projects/{projectId}", "projects/show", ProjectViewModels.ProjectViewModel]
  ["/projects/{projectId}/tree/{refOrSha}/{*path}", "projects/repositorybrowsing/tree", RepositoryBrowsingViewModel, { type: "tree" }]
  ["/projects/{projectId}/blob/{refOrSha}/{*path}", "projects/repositorybrowsing/blob", RepositoryBrowsingViewModel, { type: "blob" }]
  ["/throw", "throwing", ThrowingViewModel]
]
