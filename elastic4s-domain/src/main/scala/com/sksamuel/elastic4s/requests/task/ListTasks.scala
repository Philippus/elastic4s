package com.sksamuel.elastic4s.requests.task

import com.sksamuel.elastic4s.ext.OptionImplicits.*

import scala.concurrent.duration.FiniteDuration

case class ListTasks(
    nodeIds: Seq[String],
    detailed: Option[Boolean] = None,
    actions: Seq[String] = Nil,
    groupBy: Option[String] = None,
    waitForCompletion: Option[Boolean] = None,
    parentTaskId: Option[String] = None,
    timeout: Option[FiniteDuration] = None,
    masterTimeout: Option[FiniteDuration] = None
) {
  def actions(first: String, rest: String*): ListTasks = actions(first +: rest)
  def actions(actions: Iterable[String]): ListTasks    = copy(actions = actions.toSeq)
  def groupBy(group: String): ListTasks                = copy(groupBy = group.some)
  def groupByParents(): ListTasks                      = groupBy("parents")
  def waitForCompletion(w: Boolean): ListTasks         = copy(waitForCompletion = w.some)
  def parentTaskId(id: String): ListTasks              = copy(parentTaskId = id.some)
  def timeout(t: FiniteDuration): ListTasks            = copy(timeout = t.some)
  def masterTimeout(t: FiniteDuration): ListTasks      = copy(masterTimeout = t.some)
}
