package de.choffmeister.asserthub.models

case class Ticket(
  id: Long,
  title: String,
  creatorId: Long
) extends Entity
