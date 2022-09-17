package com.sksamuel.elastic4s.requests.update

import com.sksamuel.elastic4s.requests.task.GetTask

case class UpdateByQueryAsyncResponse(task : String)

case class UpdateByQueryTask(task: GetTask)
