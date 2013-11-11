package de.choffmeister.asserthub

import org.specs2.mutable._
import de.choffmeister.asserthub.JsonProtocol._
import de.choffmeister.asserthub.models._
import spray.json._
import java.sql.Timestamp
import java.util.Date

class JsonProtocolSpec extends SpecificationWithJUnit { 
  "JsonProtocol" should {
    "properly convert User objects" in {
      val original = User(1L, "USERNAME", "2", "3", "4", "5", "6", "7")
      val serialized = original.toJson
      val deserialized = serialized.convertTo[User]
    
      original.id === deserialized.id
      original.userName === deserialized.userName
      original !== deserialized
    }
    
    "convert Timestamp objects" in {
      val ts = new Timestamp(System.currentTimeMillis())
      val str = TimestampFormat.write(ts)
      val ts2 = TimestampFormat.read(str)

      ts === ts2
    }
  } 
}