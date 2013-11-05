package de.choffmeister.asserthub.models

case class User(
  id: Long,
  userName: String,
  email: String,
  passwordHash: String,
  passwordSalt: String = "",
  passwordHashAlgorithm: String = "plain",
  firstName: String = "",
  lastName: String = ""
)
