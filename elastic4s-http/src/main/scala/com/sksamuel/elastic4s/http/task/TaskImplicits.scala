package com.sksamuel.elastic4s.http.task

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.http.{HttpExecutable, HttpClient, HttpResponse, ResponseHandler}
import com.sksamuel.elastic4s.task.{CancelTasksRequest, ListTasksRequest}

import scala.concurrent.Future
import scala.concurrent.duration._

case class ListTaskResponse(nodes: Map[String, Node])

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
                private val start_time_in_millis: Long,
                private val running_time_in_nanos: Long,
                cancellable: Boolean) {
  def startTime: FiniteDuration   = start_time_in_millis.millis
  def runningTime: FiniteDuration = running_time_in_nanos.nanos
}

trait TaskImplicits {

  implicit object ListTaskHttpExecutable extends HttpExecutable[ListTasksRequest, ListTaskResponse] {

    override def execute(client: HttpClient, request: ListTasksRequest): Future[HttpResponse] = {

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

      client.async("GET", s"/_tasks", params.toMap)
    }
  }

  implicit object CancelTaskHttpExecutable extends HttpExecutable[CancelTasksRequest, Boolean] {

    override def responseHandler: ResponseHandler[Boolean] = new ResponseHandler[Boolean] {
      override def handle(response: HttpResponse) = Right(response.statusCode >= 200 && response.statusCode < 300)
    }

    override def execute(client: HttpClient, request: CancelTasksRequest): Future[HttpResponse] = {

      val endpoint =
        if (request.nodeIds.isEmpty) s"/_tasks/cancel"
        else s"/_tasks/task_id:${request.nodeIds.mkString(",")}/_cancel"

      val params = scala.collection.mutable.Map.empty[String, String]
      if (request.nodeIds.nonEmpty)
        params.put("nodes", request.nodeIds.mkString(","))
      if (request.actions.nonEmpty)
        params.put("actions", request.actions.mkString(","))

      client.async("POST", endpoint, params.toMap)
    }
  }
}
