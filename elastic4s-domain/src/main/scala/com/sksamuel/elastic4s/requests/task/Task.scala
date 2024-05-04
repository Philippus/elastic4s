package com.sksamuel.elastic4s.requests.task

import com.fasterxml.jackson.annotation.JsonProperty

import scala.concurrent.duration.{DurationLong, FiniteDuration}

case class Task(node: String,
                id: String,
                `type`: String,
                action: String,
                status: TaskStatus,
                description: String,
                @JsonProperty("start_time_in_millis") private val start_time_in_millis: Long,
                @JsonProperty("running_time_in_nanos") private val running_time_in_nanos: Long,
                cancellable: Boolean,
                cancelled: Option[Boolean]) {
  def startTimeInMillis: Long = start_time_in_millis
  def runningTime: FiniteDuration = running_time_in_nanos.nanos
}
