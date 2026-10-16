package com.sksamuel.elastic4s.requests.task

import scala.concurrent.duration.FiniteDuration

case class GetTask(
    nodeId: String,
    taskId: String,
    waitForCompletion: Option[Boolean] = None,
    timeout: Option[FiniteDuration] = None
) {
  def waitForCompletion(waitForCompletion: Boolean): GetTask = copy(waitForCompletion = Some(waitForCompletion))
  def timeout(timeout: FiniteDuration): GetTask              = copy(timeout = Some(timeout))
}
