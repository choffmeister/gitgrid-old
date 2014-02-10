package com.gitgrid.webservice

import spray.routing._
import spray.routing.directives._
import com.gitgrid.webservice.directives._

trait Directives extends RouteConcatenation
  with AnyParamDirectives
  with BasicDirectives
  with ChunkingDirectives
  with CookieDirectives
  with DebuggingDirectives
  with EncodingDirectives
  with ExecutionDirectives
  with FileAndResourceDirectives
  with FormFieldDirectives
  with FutureDirectives
  with HeaderDirectives
  with HostDirectives
  with MarshallingDirectives
  with MethodDirectives
  with MiscDirectives
  with ParameterDirectives
  with PathDirectives
  with RespondWithDirectives
  with RouteDirectives
  with SchemeDirectives
  //with SecurityDirectives
  with AuthDirectives
  with ODataDirectives
