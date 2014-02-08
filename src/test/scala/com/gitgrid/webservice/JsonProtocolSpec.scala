package com.gitgrid.webservice

import org.specs2.mutable._
import com.gitgrid.mongodb._
import spray.json._
import java.sql.Timestamp
import java.util.Date

class JsonProtocolSpec extends SpecificationWithJUnit {
  import JsonProtocol._

  "JsonProtocol" should {
    "properly convert User objects w/ id" in {
      val original = User(
        id = Some(Entity.generateId()),
        userName = "USERNAME",
        passwordHash = "2",
        passwordSalt = "3",
        firstName = "4",
        lastName = "5"
      )
      val serialized = original.toJson
      val deserialized = serialized.convertTo[User]

      original === deserialized
    }

    "properly convert User objects w/o id" in {
      val original = User(
        id = None,
        userName = "USERNAME",
        passwordHash = "2",
        passwordSalt = "3",
        firstName = "4",
        lastName = "5"
      )
      val serialized = original.toJson
      val deserialized = serialized.convertTo[User]

      original === deserialized
    }

    "convert Date objects" in {
      val now = System.currentTimeMillis()
      val nowround = now - now % 60000
      for (i <- 0 to 60000) {
        val ts = new Date(nowround + i)
        val str = DateFormat.write(ts)
        val ts2 = DateFormat.read(str)
        ts.getTime === ts2.getTime
      }
      ok
    }
  }
}
