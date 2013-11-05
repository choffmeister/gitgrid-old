package de.choffmeister.asserthub

import junit.framework.AssertionFailedError
import spray.testkit.RouteTest
import spray.testkit.TestFrameworkInterface

trait JUnitRouteTest extends RouteTest with TestFrameworkInterface {
  override def failTest(msg: String): Nothing = throw new AssertionFailedError(msg)
}