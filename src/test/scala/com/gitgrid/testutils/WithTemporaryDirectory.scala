package com.gitgrid.testutils

import java.io.File
import java.util.UUID
import org.specs2.specification.Scope

/**
 * Test scope with a value directory of type java.io.File pointing
 * to a new directory within the temp directory.
 */
class WithTemporaryDirectory(create: Boolean) extends Scope {
  val directory = {
    val dir = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID.toString)
    if (create) dir.mkdirs()
    dir
  }
}
