package de.choffmeister.asserthub.models

import org.squeryl.Table
import org.squeryl.KeyedEntityDef
import de.choffmeister.asserthub.models.Dsl._
import java.sql.Timestamp
import java.util.Calendar

trait Entity {
  val id: Long
}

trait TimestampedEntity extends Entity {
  val createdAt: Timestamp
}

trait OwnedEntity extends Entity {
  val creatorId: Long
}

trait EntityRepository[T <: Entity] {
  implicit val keyDef: KeyedEntityDef[T, Long]
  val table: Table[T]

  /**
   * Returns a list of all available entities.
   */
  def all: List[T] = inTransaction(from(table)(e => select(e) orderBy(e.id asc)).toList)

  /**
   * Returns a specific entity by it's ID.
   */
  def find(id: Long): Option[T] = inTransaction(table.lookup(id))

  /**
   * Inserts a new entity.
   */
  def insert(entity: T): T = inTransaction(table.insert(entity))

  /**
   * Updates an entity.
   */
  def update(entity: T): T = inTransaction {
    table.update(entity)
    entity
  }

  /**
   * Deletes a specific entity by it's ID.
   */
  def delete(id: Long): Option[T] = inTransaction {
    find(id) match {
      case Some(entity) =>
        table.deleteWhere(e => e.id === entity.id)
        Some(entity)
      case _ =>
        None
    }
  }

  def now(): Timestamp = {
    return new Timestamp(System.currentTimeMillis)
  }
}
