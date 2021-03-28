package com.sksamuel.elastic4s.requests.task

case class CreateTaskResponse(nodeId: String, taskId: String)

case class ListTaskResponse(nodes: Map[String, Node])

case class GetTaskResponse(completed: Boolean, task: Task, error: Option[TaskError])
