package com.sksamuel.elastic4s.monix

import com.sksamuel.elastic4s.{ElasticRequest, Executor, HttpClient, HttpResponse}
import monix.eval.Task
import monix.execution.Cancelable

class TaskExecutor extends Executor[Task] {
  override def exec(client: HttpClient, request: ElasticRequest): Task[HttpResponse] =
    Task.async {
      case (_, callback) =>
        client.send(request, {
          case Left(t)  => callback.onError(t)
          case Right(v) => callback.onSuccess(v)
        })
        Cancelable.empty
    }
}
