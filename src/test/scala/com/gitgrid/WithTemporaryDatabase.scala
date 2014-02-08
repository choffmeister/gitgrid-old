package com.gitgrid

import java.io.File
import java.util.UUID
import org.specs2.specification.Scope
import com.gitgrid.mongodb._

class WithTemporaryDatabase() extends Scope {
  val db = new Database(overrideServer = Some(List("localhost:27017")), overrideDatabase = Some(UUID.randomUUID.toString))(scala.concurrent.ExecutionContext.Implicits.global)
}
