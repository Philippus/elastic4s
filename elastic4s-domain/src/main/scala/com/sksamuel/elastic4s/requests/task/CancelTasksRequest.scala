package com.sksamuel.elastic4s.requests.task

import scala.concurrent.duration.FiniteDuration

case class CancelTasksRequest(
    nodeIds: Seq[String],
    timeout: Option[FiniteDuration] = None,
    actions: Seq[String] = Nil,
    parentTaskId: Option[String] = None,
    waitForCompletion: Option[Boolean] = None
) {
  def actions(first: String, rest: String*): CancelTasksRequest = actions(first +: rest)
  def actions(actions: Iterable[String]): CancelTasksRequest    = copy(actions = actions.toSeq)
  def timeout(t: FiniteDuration): CancelTasksRequest            = copy(timeout = Some(t))
  def parentTaskId(id: String): CancelTasksRequest              = copy(parentTaskId = Some(id))
  def waitForCompletion(w: Boolean): CancelTasksRequest         = copy(waitForCompletion = Some(w))
}

case class CancelTaskByIdRequest(nodeId: String, taskId: String, waitForCompletion: Option[Boolean] = None) {
  def waitForCompletion(w: Boolean): CancelTaskByIdRequest = copy(waitForCompletion = Some(w))
}
