package com.gitgrid.webservice

import spray.routing._
import spray.routing.Directives._

case class ODataParameters(top: Option[Int], skip: Option[Int])

object ODataDirective {
  lazy val odata: Directive1[ODataParameters] = {
    def parseInt(str: Option[String]): Option[Int] = str match {
      case Some(s) => Some(s.toInt)
      case _ => None
    }
    Directives.parameterMap.flatMap {
      case m: Map[String, String] => provide(ODataParameters(
        parseInt(m.get("$top")),
        parseInt(m.get("$skip"))
      ))
      case _ => provide(ODataParameters(None, None))
    }
  }
}
