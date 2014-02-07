package com.gitgrid.webapi

import org.specs2.mutable._
import com.gitgrid.models._
import spray.json._
import java.sql.Timestamp
import java.util.Date

class JsonProtocolSpec extends SpecificationWithJUnit {
  import JsonProtocol._

  "JsonProtocol" should {
    "properly convert User objects" in {
      val original = User(1L, "USERNAME", "2", "3", "4", "5", "6", "7")
      val serialized = original.toJson
      val deserialized = serialized.convertTo[User]

      original.id === deserialized.id
      original.userName === deserialized.userName
      original !== deserialized
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

    "convert Timestamp objects" in {
      val now = System.currentTimeMillis()
      val nowround = now - now % 1000
      for (i <- 0 to 60000) {
        val ts = new Timestamp(nowround + i)
        val str = TimestampFormat.write(ts)
        val ts2 = TimestampFormat.read(str)
        ts.getTime === ts2.getTime
      }
      ok
    }
  }
}
