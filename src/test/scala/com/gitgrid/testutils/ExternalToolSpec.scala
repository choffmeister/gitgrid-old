package com.gitgrid.testutils

import java.io._
import scala.sys.process._
import org.specs2.mutable._

case class ExternalToolException(cmd: Seq[String], exitCode: Int, stdOut: String, stdErr: String) extends Exception(
  s"Executing ${cmd.mkString(" ")} failed with exit code ${exitCode}:\n${stdErr}"
)

trait ExternalToolSpec extends Specification {
  def checkExecute(cmd: Seq[String]): Boolean = {
    try {
      execute(cmd)
      true
    } catch {
      case ex: IOException => false
    }
  }

  def execute(cmd: Seq[String], wd: Option[File] = None): (Int, String, String) = {
    val stdOut = new StringBuffer()
    val stdErr = new StringBuffer()
    val proc = Process(cmd, wd)
    val exitCode = proc ! ProcessLogger(line => stdOut.append(line + "\n"), line => stdErr.append(line + "\n"))

    if (exitCode != 0) throw new ExternalToolException(cmd, exitCode, stdOut.toString, stdErr.toString)

    (exitCode, stdOut.toString, stdErr.toString)
  }
}
