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

  def create() = {
    Await.ready(Users.indexes, Inf)
    Await.ready(Projects.indexes, Inf)
    Await.ready(Tickets.indexes, Inf)
  }

  def drop() = {
    Await.ready(database(Users.collectionName).remove(BSONDocument.empty), Inf)
    Await.ready(database(Projects.collectionName).remove(BSONDocument.empty), Inf)
    Await.ready(database(Tickets.collectionName).remove(BSONDocument.empty), Inf)
  }
}

object Database {
  val driver = new MongoDriver
}

