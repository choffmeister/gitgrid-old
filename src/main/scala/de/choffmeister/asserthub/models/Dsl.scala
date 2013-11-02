package de.choffmeister.asserthub.models

import org.squeryl.KeyedEntityDef
import org.squeryl.PrimitiveTypeMode

object Dsl extends PrimitiveTypeMode
{
  implicit object userKED extends KeyedEntityDef[User, Long] {
    def getId(u: User) = u.id
    def isPersisted(u: User) = u.id > 0
    def idPropertyName = "id"
  }
}