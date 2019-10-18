package com.sksamuel.elastic4s.zio

import com.sksamuel.elastic4s.{ElasticRequest, Executor, HttpClient, HttpResponse}
import zio.Task

class TaskExecutor extends Executor[Task] {
  override def exec(client: HttpClient, request: ElasticRequest): Task[HttpResponse] =
    Task.effectAsync { k =>
      client.send(request, { r =>
        k(Task.fromEither(r))
      })
    }
}
