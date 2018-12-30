package com.sksamuel.elastic4s.scalaz

import com.sksamuel.elastic4s.{ElasticRequest, Executor, HttpClient, HttpResponse}
import scalaz.\/
import scalaz.concurrent.Task

class TaskExecutor extends Executor[Task] {
  override def exec(client: HttpClient, request: ElasticRequest): Task[HttpResponse] =
    Task.async { k =>
      client.send(request, { j =>
        k(\/.fromEither(j))
      })
    }
}
