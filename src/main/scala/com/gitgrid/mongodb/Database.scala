package com.gitgrid.mongodb

import com.gitgrid.Config
import scala.concurrent._
import scala.concurrent.duration.Duration.Inf
import reactivemongo.api._
import reactivemongo.bson._

class Database(overrideServer: Option[List[String]] = None, overrideDatabase: Option[String] = None)(implicit val executor: ExecutionContext) {
  import UserBSONFormat._
  import ProjectBSONFormat._
  import TicketBSONFormat._

  val connection = Database.driver.connection(overrideServer getOrElse Config.mongoDbServers)
  val database = connection(overrideDatabase getOrElse Config.mongoDbDatabase)

  def drop() = {
    Await.result(database("users").remove(BSONDocument.empty), Inf)
  }
}

object Database {
  val driver = new MongoDriver
}

