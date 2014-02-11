package com.gitgrid

import scala.concurrent._
import reactivemongo.api._
import reactivemongo.bson._

package object mongodb {
  val DefaultDatabase = new Database(None, None)(ExecutionContext.Implicits.global)

  import UserBSONFormat._
  import ProjectBSONFormat._
  import TicketBSONFormat._
}

