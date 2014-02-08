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

  def insert(entity: E)(implicit ec: ExecutionContext): Future[LastError]
  def update(entity: E)(implicit ec: ExecutionContext): Future[LastError]
  def delete(id: BSONObjectID)(implicit ec: ExecutionContext): Future[LastError]

  def beforeInsert(entity: E): E = entity
  def beforeUpdate(entity: E): E = entity
}

abstract class ReactiveMongoEntityRepository[E <: Entity](collectionName: String) extends EntityRepository[E] {
  implicit val reader: BSONDocumentReader[E]
  implicit val writer: BSONDocumentWriter[E]
  protected val coll = DefaultDatabase.database(collectionName)

  def all(implicit ec: ExecutionContext): Future[List[E]] = coll.find(BSONDocument.empty).cursor[E].collect[List]()
  def find(id: BSONObjectID)(implicit ec: ExecutionContext): Future[Option[E]] = coll.find(BSONDocument("_id" -> id)).one[E]

  def insert(entity: E)(implicit ec: ExecutionContext): Future[LastError] = coll.insert(beforeInsert(entity))
  def update(entity: E)(implicit ec: ExecutionContext): Future[LastError] = coll.update(BSONDocument("_id" -> entity.id.get), beforeUpdate(entity))
  def delete(id: BSONObjectID)(implicit ec: ExecutionContext): Future[LastError] = coll.remove(BSONDocument("_id" -> id))
}
