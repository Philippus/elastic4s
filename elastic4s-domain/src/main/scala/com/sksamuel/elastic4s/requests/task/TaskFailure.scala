package com.sksamuel.elastic4s.requests.task

import com.sksamuel.elastic4s.ErrorCause

// https://github.com/elastic/elasticsearch-specification/blob/main/specification/_types/Errors.ts
case class TaskFailure(index: String, id: String, cause: ErrorCause, status: Int)
