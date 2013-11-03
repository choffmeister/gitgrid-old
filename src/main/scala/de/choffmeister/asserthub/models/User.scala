package de.choffmeister.asserthub.models

class User(
  val id: Long,
  val userName: String,
  val email: String,
  val passwordHash: String,
  val passwordSalt: String = "",
  val passwordHashAlgorithm: String = "plain",
  val firstName: String = "",
  val lastName: String = ""
)
{
  def this() = this(0, "", "", "", "", "", "", "")

  override def toString(): String = s"${userName}#${id}"
}
