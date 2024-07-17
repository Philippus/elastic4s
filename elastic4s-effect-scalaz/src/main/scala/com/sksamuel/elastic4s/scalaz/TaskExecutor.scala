package com.sksamuel.elastic4s.scalaz

import com.sksamuel.elastic4s.{ElasticRequest, Executor, HttpClient, HttpResponse}
import scalaz.concurrent.Task

class TaskExecutor extends Executor[Task] {
  override def exec(client: HttpClient[Task], request: ElasticRequest): Task[HttpResponse] =
    client.send(request)
}
