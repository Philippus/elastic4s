package com.sksamuel.elastic4s

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.language.higherKinds

trait Executor[F[_]] {
  def exec(client: HttpClient, request: ElasticRequest): F[HttpResponse]
}

object Executor {

  def apply[F[_]: Executor](): Executor[F] = implicitly[Executor[F]]

  implicit def FutureExecutor(implicit ec: ExecutionContext = ExecutionContext.Implicits.global): Executor[Future] =
    new Executor[Future] {
      override def exec(client: HttpClient, request: ElasticRequest): Future[HttpResponse] = {
        val promise = Promise[HttpResponse]()
        val callback: Either[Throwable, HttpResponse] => Unit = {
          case Left(t)  => promise.tryFailure(t)
          case Right(r) => promise.trySuccess(r)
        }
        client.send(request, callback)
        promise.future
      }
    }
}
