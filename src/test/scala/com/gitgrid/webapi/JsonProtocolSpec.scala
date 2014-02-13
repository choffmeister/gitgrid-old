package com.gitgrid.webapi

import com.gitgrid.mongodb._
import java.util.Date
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import spray.json._

@RunWith(classOf[JUnitRunner])
class JsonProtocolSpec extends Specification {
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
