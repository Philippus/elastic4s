package com.sksamuel.elastic4s.monix

import com.sksamuel.elastic4s.http.{ElasticRequest, Executor, HttpClient, HttpResponse}
import monix.eval.Task
import monix.execution.Cancelable

class TaskExecutor extends Executor[Task] {
  override def exec(client: HttpClient, request: ElasticRequest): Task[HttpResponse] =
    Task.async(k => client.send(request,k))
}
