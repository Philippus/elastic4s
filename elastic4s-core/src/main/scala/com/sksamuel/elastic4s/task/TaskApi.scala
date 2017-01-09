package com.sksamuel.elastic4s.task

import scala.concurrent.duration.FiniteDuration

trait TaskApi {

  def cancelTasks(first: String, rest: String*): CancelTasksDefinition = cancelTasks(first +: rest)
  def cancelTasks(nodeIds: Seq[String]): CancelTasksDefinition = CancelTasksDefinition(nodeIds)

  def pendingClusterTasks(local: Boolean): PendingClusterTasksDefinition = PendingClusterTasksDefinition(local)

  def listTasks(first: String, rest: String*): ListTasksDefinition = listTasks(first +: rest)
  def listTasks(nodeIds: Seq[String]): ListTasksDefinition = ListTasksDefinition(nodeIds)
}

case class CancelTasksDefinition(nodeIds: Seq[String],
                                 timeout: Option[FiniteDuration] = None,
                                 actions: Seq[String] = Nil)

case class PendingClusterTasksDefinition(local: Boolean,
                                         masterNodeTimeout: Option[FiniteDuration] = None)

case class ListTasksDefinition(nodeIds: Seq[String],
                               detailed: Option[Boolean] = None,
                               waitForCompletion: Option[Boolean] = None)
