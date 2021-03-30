package com.sksamuel.elastic4s.requests.task

case class TaskStatus(total: Long, updated: Long, created: Long, deleted: Long, batches: Long)
