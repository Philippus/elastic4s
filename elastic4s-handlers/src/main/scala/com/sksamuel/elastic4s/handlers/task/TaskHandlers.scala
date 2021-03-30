package com.sksamuel.elastic4s.handlers.task

import com.sksamuel.elastic4s.requests.task.{CancelTasksRequest, GetTask, GetTaskResponse, ListTaskResponse, ListTasks}
import com.sksamuel.elastic4s.{ElasticRequest, Handler, HttpResponse, ResponseHandler}

trait TaskHandlers {

  implicit object GetTaskHandler extends Handler[GetTask, GetTaskResponse] {
    override def build(request: GetTask): ElasticRequest = {
      ElasticRequest("GET", s"/_tasks/${request.nodeId}:${request.taskId}")
    }
  }

  implicit object ListTaskHandler extends Handler[ListTasks, ListTaskResponse] {

    override def build(request: ListTasks): ElasticRequest = {

      val params = scala.collection.mutable.Map.empty[String, String]
      if (request.nodeIds.nonEmpty)
        params.put("nodes", request.nodeIds.mkString(","))
      if (request.actions.nonEmpty)
        params.put("actions", request.actions.mkString(","))
      if (request.detailed.getOrElse(false))
        params.put("detailed", "true")
      if (request.waitForCompletion.getOrElse(false))
        params.put("wait_for_completion", "true")
      request.groupBy.foreach(params.put("group_by", _))

      ElasticRequest("GET", s"/_tasks", params.toMap)
    }
  }

  implicit object CancelTaskHandler extends Handler[CancelTasksRequest, Boolean] {

    override def responseHandler: ResponseHandler[Boolean] = new ResponseHandler[Boolean] {
      override def handle(response: HttpResponse) = Right(response.statusCode >= 200 && response.statusCode < 300)
    }

    override def build(request: CancelTasksRequest): ElasticRequest = {

      val endpoint =
        if (request.nodeIds.isEmpty) s"/_tasks/cancel"
        else s"/_tasks/task_id:${request.nodeIds.mkString(",")}/_cancel"

      val params = scala.collection.mutable.Map.empty[String, String]
      if (request.nodeIds.nonEmpty)
        params.put("nodes", request.nodeIds.mkString(","))
      if (request.actions.nonEmpty)
        params.put("actions", request.actions.mkString(","))

      ElasticRequest("POST", endpoint, params.toMap)
    }
  }
}
