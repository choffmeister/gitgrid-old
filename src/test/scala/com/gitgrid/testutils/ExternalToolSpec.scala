package com.gitgrid.testutils

import java.io._
import scala.sys.process._
import org.specs2.mutable._

trait ExternalToolSpec extends Specification {
  def checkExecute(cmd: Seq[String]): Boolean = {
    try {
      execute(cmd)
      true
    } catch {
      case ex: IOException => false
    }
  }

  def execute(cmd: Seq[String], wd: Option[File] = None): (Int, String) = {
    val buffer = new StringBuffer()
    val proc = Process(cmd, wd)
    val exitCode = proc ! ProcessLogger((line: String) => buffer.append(line + "\n"))
    (exitCode, buffer.toString)
  }
}
