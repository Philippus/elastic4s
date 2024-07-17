package com.sksamuel.elastic4s.cats.effect

import cats.effect.{Async, IO}
import com.sksamuel.elastic4s.{ElasticRequest, Executor, HttpClient, HttpResponse}

import scala.language.higherKinds

class CatsEffectExecutor[F[_]] extends Executor[F] {
  override def exec(client: HttpClient[F], request: ElasticRequest): F[HttpResponse] =
    client.send(request)
}

@deprecated("Use CatsEffectExecutor[IO] instead")
class IOExecutor extends CatsEffectExecutor[IO]
