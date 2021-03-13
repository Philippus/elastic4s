package com.sksamuel.elastic4s.cats.effect

import cats.effect.{Async, IO}
import com.sksamuel.elastic4s.{ElasticRequest, Executor, HttpClient, HttpResponse}

class CatsEffectExecutor[F[_]: Async] extends Executor[F] {
  override def exec(client: HttpClient, request: ElasticRequest): F[HttpResponse] =
    Async[F].async_[HttpResponse](k => client.send(request, k))
}

@deprecated("Use CatsEffectExecutor[IO] instead")
class IOExecutor extends CatsEffectExecutor[IO]
