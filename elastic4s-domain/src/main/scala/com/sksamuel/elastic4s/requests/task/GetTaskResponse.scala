package com.sksamuel.elastic4s.requests.task

case class GetTaskResponse(completed: Boolean, task: Task, error: Option[TaskError])
