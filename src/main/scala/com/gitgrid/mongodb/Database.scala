package com.gitgrid.mongodb

import com.gitgrid.Config
import scala.concurrent._
import reactivemongo.api._
import reactivemongo.bson._

class Database(overrideServer: Option[List[String]] = None, overrideDatabase: Option[String] = None)(implicit val executor: ExecutionContext) {
  val connection = Database.driver.connection(overrideServer getOrElse Config.mongoDbServers)
  val database = connection(overrideDatabase getOrElse Config.mongoDbDatabase)

  import UserBSONFormat._
  import ProjectBSONFormat._
  import TicketBSONFormat._
}

object Database {
  val driver = new MongoDriver
}

