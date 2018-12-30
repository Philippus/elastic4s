package com.sksamuel.elastic4s.cats.effect

import cats.effect.IO
import com.sksamuel.elastic4s.{ElasticRequest, Executor, HttpClient, HttpResponse}

class IOExecutor extends Executor[IO] {
  override def exec(client: HttpClient, request: ElasticRequest): IO[HttpResponse] =
    IO.async[HttpResponse] { k =>
      client.send(request, k)
    }
}
