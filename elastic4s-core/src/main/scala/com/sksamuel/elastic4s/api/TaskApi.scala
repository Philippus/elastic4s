package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.task.{CancelTaskByIdRequest, CancelTasksRequest, GetTask, ListTasks, PendingClusterTasksRequest}

trait TaskApi {

  def cancelTasks(): CancelTasksRequest = cancelTasks(Nil)
  def cancelTasks(first: String, rest: String*): CancelTasksRequest = cancelTasks(first +: rest)
  def cancelTasks(nodeIds: Seq[String]): CancelTasksRequest = CancelTasksRequest(nodeIds)
  def cancelTaskById(nodeId: String, taskId: String): CancelTaskByIdRequest = CancelTaskByIdRequest(nodeId, taskId)

  def pendingClusterTasks(local: Boolean): PendingClusterTasksRequest = PendingClusterTasksRequest(local)

  def getTask(nodeId: String, taskId: String): GetTask = GetTask(nodeId, taskId)

  def listTasks(): ListTasks = listTasks(Nil)
  def listTasks(first: String, rest: String*): ListTasks = listTasks(first +: rest)
  def listTasks(nodeIds: Seq[String]): ListTasks = ListTasks(nodeIds)
}
