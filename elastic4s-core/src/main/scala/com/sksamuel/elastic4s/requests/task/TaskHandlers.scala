package com.sksamuel.elastic4s.requests.task

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.{ElasticRequest, Handler, HttpResponse, ResponseHandler}

import scala.concurrent.duration._

case class CreateTaskResponse(nodeId: String, taskId: String)

case class ListTaskResponse(nodes: Map[String, Node])

case class GetTaskResponse(completed: Boolean, task: Task)

case class Node(name: String,
                @JsonProperty("transport_address") transportAddress: String,
                host: String,
                ip: String,
                roles: Seq[String],
                tasks: Map[String, Task])

case class Task(node: String,
                id: String,
                `type`: String,
                action: String,
                status: TaskStatus,
                description: String,
                @JsonProperty("start_time_in_millis") private val start_time_in_millis: Long,
                @JsonProperty("running_time_in_nanos") private val running_time_in_nanos: Long,
                cancellable: Boolean) {
  def startTimeInMillis: Long = start_time_in_millis
  def runningTime: FiniteDuration = running_time_in_nanos.nanos
}

case class TaskStatus(total: Long, updated: Long, Created: Long, deleted: Long, batches: Long)

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
