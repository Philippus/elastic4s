package com.sksamuel.elastic4s.requests.task

import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration.FiniteDuration



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
