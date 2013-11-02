package de.choffmeister.asserthub.models

class User(
  val id: Long,
  val userName: String,
  val email: String,
  val passwordHash: String,
  val passwordSalt: String,
  val passwordHashAlgorithm: String,
  val firstName: String,
  val lastName: String
)
{
  def this() = this(0, "", "", "", "", "", "", "")
  def this(userName: String, email: String, firstName: String, lastName: String) = this(0, userName, email, "", "", "", firstName, lastName)

  override def toString(): String = userName + "#" + id
}
