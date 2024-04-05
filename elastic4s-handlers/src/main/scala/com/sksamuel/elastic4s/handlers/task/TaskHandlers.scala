package com.sksamuel.elastic4s.handlers.task

import com.sksamuel.elastic4s.ext.OptionImplicits.RichOption
import com.sksamuel.elastic4s.handlers.ElasticErrorParser
import com.sksamuel.elastic4s.requests.task.{CancelTaskByIdRequest, CancelTasksRequest, GetTask, GetTaskResponse, ListTaskResponse, ListTasks, Node, Task}
import com.sksamuel.elastic4s.{ElasticError, ElasticRequest, Handler, HttpResponse, ResponseHandler}

import scala.util.Try

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

  abstract class AbstractCancelTaskHandler[U] extends Handler[U, Boolean] {

    override def responseHandler: ResponseHandler[Boolean] = new ResponseHandler[Boolean] {
      override def handle(response: HttpResponse): Either[ElasticError, Boolean] = response.statusCode match {
        case 200 | 201 | 202 | 203 | 204 => {
          val entity = response.entity.getOrError("No entity defined")
          // It can fail on a 200 by returning a response containing node_failures
          if (entity.content.contains("node_failures")) Right[ElasticError, Boolean](false)
          else {
            Try(ResponseHandler.fromEntity[ListTaskResponse](entity)).map { (list: ListTaskResponse) =>
              // Check that all the tasks on all the nodes are cancelled
              list.nodes.forall { case (_, node: Node) =>
                node.tasks.forall { case (_, task: Task) =>
                  task.cancelled.getOrElse(false)
                }
              }
            }.toEither.fold((_: Throwable) => Right[ElasticError, Boolean](false), b => Right[ElasticError, Boolean](b))
          }
        }
        case _ =>
          Left[ElasticError, Boolean](ElasticErrorParser.parse(response))
      }
    }

  }

  implicit object CancelTaskHandler extends AbstractCancelTaskHandler[CancelTasksRequest] {

    override def build(request: CancelTasksRequest): ElasticRequest = {

      val params = scala.collection.mutable.Map.empty[String, String]
      if (request.nodeIds.nonEmpty)
        params.put("nodes", request.nodeIds.mkString(","))
      if (request.actions.nonEmpty)
        params.put("actions", request.actions.mkString(","))

      ElasticRequest("POST", "/_tasks/_cancel", params.toMap)
    }
  }

  implicit object CancelTaskByIdHandler extends AbstractCancelTaskHandler[CancelTaskByIdRequest] {

    override def build(request: CancelTaskByIdRequest): ElasticRequest =
      ElasticRequest("POST", s"/_tasks/${request.nodeId}:${request.taskId}/_cancel")
  }
}
