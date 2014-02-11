package com.gitgrid.testutils

import scala.concurrent._
import scala.concurrent.duration.Duration.Inf

trait AsyncUtils {
  implicit val executor = scala.concurrent.ExecutionContext.Implicits.global

  def await[T](future: Future[T]): T = Await.result(future, Inf)
  def await[T](futures: Seq[Future[T]]): Seq[T] = futures.map(f => Await.result(f, Inf))
}
