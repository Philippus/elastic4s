package com.sksamuel.elastic4s.requests.task

import scala.concurrent.duration.FiniteDuration

case class PendingClusterTasksRequest(local: Boolean, masterNodeTimeout: Option[FiniteDuration] = None)
