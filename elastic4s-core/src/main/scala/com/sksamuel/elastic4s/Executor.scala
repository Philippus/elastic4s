package com.sksamuel.elastic4s

import scala.language.higherKinds

trait Executor[F[_]] {
  def exec(client: HttpClient[F], request: ElasticRequest): F[HttpResponse]
}

object Executor {
  def apply[F[_]](implicit ev: Executor[F]): Executor[F] = ev

  implicit def defaultExecutor[F[_]]: Executor[F] =
    (client: HttpClient[F], request: ElasticRequest) => client.send(request)
}
