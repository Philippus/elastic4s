package com.sksamuel.elastic4s.requests.task

import scala.concurrent.duration.FiniteDuration
import com.sksamuel.exts.OptionImplicits._

trait TaskApi {

  def cancelTasks(): CancelTasksRequest                             = cancelTasks(Nil)
  def cancelTasks(first: String, rest: String*): CancelTasksRequest = cancelTasks(first +: rest)
  def cancelTasks(nodeIds: Seq[String]): CancelTasksRequest         = CancelTasksRequest(nodeIds)

  def pendingClusterTasks(local: Boolean): PendingClusterTasksRequest = PendingClusterTasksRequest(local)

  def getTask(nodeId: String, taskId: String): GetTask = GetTask(nodeId, taskId)

  def listTasks(): ListTasks                             = listTasks(Nil)
  def listTasks(first: String, rest: String*): ListTasks = listTasks(first +: rest)
  def listTasks(nodeIds: Seq[String]): ListTasks         = ListTasks(nodeIds)
}

case class CancelTasksRequest(nodeIds: Seq[String],
                              timeout: Option[FiniteDuration] = None,
                              actions: Seq[String] = Nil) {
  def actions(first: String, rest: String*): CancelTasksRequest = actions(first +: rest)
  def actions(actions: Iterable[String]): CancelTasksRequest    = copy(actions = actions.toSeq)
}

case class PendingClusterTasksRequest(local: Boolean, masterNodeTimeout: Option[FiniteDuration] = None)

case class GetTask(nodeId: String, taskId: String)

case class ListTasks(nodeIds: Seq[String],
                     detailed: Option[Boolean] = None,
                     actions: Seq[String] = Nil,
                     groupBy: Option[String] = None,
                     waitForCompletion: Option[Boolean] = None) {
  def actions(first: String, rest: String*): ListTasks = actions(first +: rest)
  def actions(actions: Iterable[String]): ListTasks    = copy(actions = actions.toSeq)
  def groupBy(group: String): ListTasks                = copy(groupBy = group.some)
  def groupByParents(): ListTasks                      = groupBy("parents")
  def waitForCompletion(w: Boolean): ListTasks         = copy(waitForCompletion = w.some)
}
