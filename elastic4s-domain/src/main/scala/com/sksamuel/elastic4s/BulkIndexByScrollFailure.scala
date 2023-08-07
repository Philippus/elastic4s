package com.sksamuel.elastic4s

// https://github.com/elastic/elasticsearch-specification/blob/b8b9d95dd6f94dc4e415d37da97095278f9a3a90/specification/_types/Errors.ts#L58
case class BulkIndexByScrollFailure(cause: ErrorCause,
                                    id: String,
                                    index: String,
                                    status: Int,
                                    `type`: String)
