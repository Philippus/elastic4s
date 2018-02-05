package com.sksamuel.elastic4s.task

import scala.concurrent.duration.FiniteDuration
import com.sksamuel.exts.OptionImplicits._

trait TaskApi {

  def cancelTasks(): CancelTasksRequest = cancelTasks(Nil)
  def cancelTasks(first: String, rest: String*): CancelTasksRequest = cancelTasks(first +: rest)
  def cancelTasks(nodeIds: Seq[String]): CancelTasksRequest = CancelTasksRequest(nodeIds)

  def pendingClusterTasks(local: Boolean): PendingClusterTasksRequest = PendingClusterTasksRequest(local)

  def listTasks(): ListTasksRequest = listTasks(Nil)
  def listTasks(first: String, rest: String*): ListTasksRequest = listTasks(first +: rest)
  def listTasks(nodeIds: Seq[String]): ListTasksRequest = ListTasksRequest(nodeIds)
}

case class CancelTasksRequest(nodeIds: Seq[String],
                              timeout: Option[FiniteDuration] = None,
                              actions: Seq[String] = Nil) {
  def actions(first: String, rest: String*): CancelTasksRequest = actions(first +: rest)
  def actions(actions: Iterable[String]): CancelTasksRequest = copy(actions = actions.toSeq)
}

case class PendingClusterTasksRequest(local: Boolean, masterNodeTimeout: Option[FiniteDuration] = None)

case class ListTasksRequest(nodeIds: Seq[String],
                            detailed: Option[Boolean] = None,
                            actions: Seq[String] = Nil,
                            groupBy: Option[String] = None,
                            waitForCompletion: Option[Boolean] = None) {
  def actions(first: String, rest: String*): ListTasksRequest = actions(first +: rest)
  def actions(actions: Iterable[String]): ListTasksRequest = copy(actions = actions.toSeq)
  def groupBy(group: String): ListTasksRequest = copy(groupBy = group.some)
  def groupByParents(): ListTasksRequest = groupBy("parents")
  def waitForCompletion(w: Boolean): ListTasksRequest = copy(waitForCompletion = w.some)
}
