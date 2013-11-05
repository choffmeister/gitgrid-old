package de.choffmeister.asserthub.managers

trait EntityRepository[T] {
  /**
   * Returns a list of all available entities.
   */
  def all: List[T]
  
  /**
   * Returns a specific entity by it's ID.
   */
  def find(id: Long): Option[T]
  
  /**
   * Inserts a new entity.
   */
  def insert(entity: T): T
  
  /**
   * Updates an entity.
   */
  def update(entity: T): Unit
  
  /**
   * Deletes a specific entity by it's ID.
   */
  def delete(id: Long): Unit
}