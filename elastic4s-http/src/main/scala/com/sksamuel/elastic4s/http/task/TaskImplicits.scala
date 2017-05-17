package com.sksamuel.elastic4s.http.task

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.http.{HttpExecutable, ResponseHandler}
import com.sksamuel.elastic4s.task.{CancelTasksDefinition, ListTasksDefinition}
import org.elasticsearch.client.RestClient

import scala.collection.JavaConverters._
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
  def startTime: FiniteDuration = start_time_in_millis.millis
  def runningTime: FiniteDuration = running_time_in_nanos.nanos
}

trait TaskImplicits {

  implicit object ListTaskHttpExecutable extends HttpExecutable[ListTasksDefinition, ListTaskResponse] {

    override def execute(client: RestClient, request: ListTasksDefinition): Future[ListTaskResponse] = {

      val params = scala.collection.mutable.Map.empty[String, String]
      if (request.nodeIds.nonEmpty)
        params.put("nodes", request.nodeIds.mkString(","))
      if (request.actions.nonEmpty)
        params.put("actions", request.actions.mkString(","))
      if (request.detailed.contains(true))
        params.put("detailed", "true")
      if (request.waitForCompletion.contains(true))
        params.put("wait_for_completion", "true")
      request.groupBy.foreach(params.put("group_by", _))

      client.async("GET", s"/_tasks", params.toMap, ResponseHandler.default)
    }
  }

  implicit object CancelTaskHttpExecutable extends HttpExecutable[CancelTasksDefinition, Boolean] {

    override def execute(client: RestClient,
                         request: CancelTasksDefinition): Future[Boolean] = {

      val endpoint = if (request.nodeIds.isEmpty) s"/_tasks/cancel"
      else s"/_tasks/task_id:${request.nodeIds.mkString(",")}/_cancel"

      val params = scala.collection.mutable.Map.empty[String, String]
      if (request.nodeIds.nonEmpty)
        params.put("nodes", request.nodeIds.mkString(","))
      if (request.actions.nonEmpty)
        params.put("actions", request.actions.mkString(","))

      Future.successful {
        val code = client.performRequest("POST", endpoint, params.asJava).getStatusLine.getStatusCode
        code >= 200 && code < 300
      }
    }
  }
}
