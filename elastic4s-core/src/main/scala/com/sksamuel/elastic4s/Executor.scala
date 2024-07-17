package com.sksamuel.elastic4s

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

trait Executor[F[_]] {
  def exec(client: HttpClient[F], request: ElasticRequest): F[HttpResponse]
}

object Executor {

  def apply[F[_]: Executor]: Executor[F] = implicitly[Executor[F]]

  implicit def FutureExecutor(implicit ec: ExecutionContext = ExecutionContext.Implicits.global): Executor[Future] =
    new Executor[Future] {
      override def exec(client: HttpClient[Future], request: ElasticRequest): Future[HttpResponse] = {
        client.send(request)
      }
    }
}
