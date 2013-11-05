package de.choffmeister.asserthub

import org.junit.Assert._
import org.junit._
import spray.json._
import de.choffmeister.asserthub.JsonProtocol._
import de.choffmeister.asserthub.models.User

class JsonProtocolTest {
  @Test def testUserConversion() {
    val original = User(1L, "USERNAME", "2", "3", "4", "5", "6", "7")
    val serialized = original.toJson
    val deserialized = serialized.convertTo[User]
    
    assertTrue(serialized.toString.contains("USERNAME"))
    assertEquals(original.id, deserialized.id)
    assertEquals(original.userName, deserialized.userName)
    assertNotSame(original, deserialized)
  }
}
