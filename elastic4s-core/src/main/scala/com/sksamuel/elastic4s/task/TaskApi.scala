package com.sksamuel.elastic4s.task

import scala.concurrent.duration.FiniteDuration
import com.sksamuel.exts.OptionImplicits._

trait TaskApi {

  def cancelTasks(): CancelTasksDefinition = cancelTasks(Nil)
  def cancelTasks(first: String, rest: String*): CancelTasksDefinition = cancelTasks(first +: rest)
  def cancelTasks(nodeIds: Seq[String]): CancelTasksDefinition = CancelTasksDefinition(nodeIds)

  def pendingClusterTasks(local: Boolean): PendingClusterTasksDefinition = PendingClusterTasksDefinition(local)

  def listTasks(): ListTasksDefinition = listTasks(Nil)
  def listTasks(first: String, rest: String*): ListTasksDefinition = listTasks(first +: rest)
  def listTasks(nodeIds: Seq[String]): ListTasksDefinition = ListTasksDefinition(nodeIds)
}

case class CancelTasksDefinition(nodeIds: Seq[String],
                                 timeout: Option[FiniteDuration] = None,
                                 actions: Seq[String] = Nil) {
  def actions(first: String, rest: String*): CancelTasksDefinition = actions(first +: rest)
  def actions(actions: Iterable[String]): CancelTasksDefinition = copy(actions = actions.toSeq)
}

case class PendingClusterTasksDefinition(local: Boolean,
                                         masterNodeTimeout: Option[FiniteDuration] = None)

case class ListTasksDefinition(nodeIds: Seq[String],
                               detailed: Option[Boolean] = None,
                               actions: Seq[String] = Nil,
                               groupBy: Option[String] = None,
                               waitForCompletion: Option[Boolean] = None) {
  def actions(first: String, rest: String*): ListTasksDefinition = actions(first +: rest)
  def actions(actions: Iterable[String]): ListTasksDefinition = copy(actions = actions.toSeq)
  def groupBy(group: String): ListTasksDefinition = copy(groupBy = group.some)
  def groupByParents(): ListTasksDefinition = groupBy("parents")
}
