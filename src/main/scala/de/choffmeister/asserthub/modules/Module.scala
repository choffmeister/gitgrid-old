package de.choffmeister.asserthub.modules

import spray.routing.Route

trait Module {
  val name: String
  val routePrefix: String
  val route: Route
}