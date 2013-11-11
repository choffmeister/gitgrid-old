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
  
  implicit object projectKED extends KeyedEntityDef[Project, Long] {
    def getId(p: Project) = p.id
    def isPersisted(p: Project) = p.id > 0
    def idPropertyName = "id"
  }
  
  implicit object ticketKED extends KeyedEntityDef[Ticket, Long] {
    def getId(t: Ticket) = t.id
    def isPersisted(t: Ticket) = t.id > 0
    def idPropertyName = "id"
  }
}