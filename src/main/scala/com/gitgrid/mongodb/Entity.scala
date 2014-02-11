package com.gitgrid.mongodb

import reactivemongo.bson._
import reactivemongo.core.commands.LastError
import scala.util.Success
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

trait Entity {
  val id: Option[BSONObjectID]
}

object Entity {
  def generateId() = BSONObjectID.generate
}

trait EntityRepository[E <: Entity] {
  def all(implicit ec: ExecutionContext): Future[List[E]]
  def find(id: BSONObjectID)(implicit ec: ExecutionContext): Future[Option[E]]

  def insert(entity: E)(implicit ec: ExecutionContext): Future[E]
  def update(entity: E)(implicit ec: ExecutionContext): Future[E]
  def delete(id: BSONObjectID)(implicit ec: ExecutionContext): Future[Unit]

  def beforeInsert(entity: E): E = entity
  def beforeUpdate(entity: E): E = entity
}

abstract class ReactiveMongoEntityRepository[E <: Entity](collectionName: String) extends EntityRepository[E] {
  implicit val reader: BSONDocumentReader[E]
  implicit val writer: BSONDocumentWriter[E]
  protected val coll = DefaultDatabase.database(collectionName)

  def all(implicit ec: ExecutionContext): Future[List[E]] = {
    coll.find(BSONDocument.empty).cursor[E].collect[List]()
  }

  def find(id: BSONObjectID)(implicit ec: ExecutionContext): Future[Option[E]] = {
    coll.find(BSONDocument("_id" -> id)).one[E]
  }

  def insert(entity: E)(implicit ec: ExecutionContext): Future[E] = {
    val transformed = beforeInsert(entity)
    coll.insert(transformed).map(_ => transformed)
  }

  def update(entity: E)(implicit ec: ExecutionContext): Future[E] = {
    val transformed = beforeUpdate(entity)
    coll.update(BSONDocument("_id" -> entity.id.get), transformed).map(_ => transformed)
  }

  def delete(id: BSONObjectID)(implicit ec: ExecutionContext): Future[Unit] = {
    coll.remove(BSONDocument("_id" -> id)).map(_ => Unit)
  }
}
