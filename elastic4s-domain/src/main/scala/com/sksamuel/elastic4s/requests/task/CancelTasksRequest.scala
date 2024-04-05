package com.sksamuel.elastic4s.requests.task

import scala.concurrent.duration.FiniteDuration

case class CancelTasksRequest(nodeIds: Seq[String],
                              timeout: Option[FiniteDuration] = None,
                              actions: Seq[String] = Nil) {
  def actions(first: String, rest: String*): CancelTasksRequest = actions(first +: rest)
  def actions(actions: Iterable[String]): CancelTasksRequest = copy(actions = actions.toSeq)
}

case class CancelTaskByIdRequest(nodeId: String, taskId: String)
