package com.gitgrid

import reactivemongo.api._
import reactivemongo.bson._
import scala.concurrent._

package object mongodb {
  val DefaultDatabase = new Database(None, None)(ExecutionContext.Implicits.global)

  import UserBSONFormat._
  import ProjectBSONFormat._
  import TicketBSONFormat._
}

