package com.sksamuel.elastic4s.requests.update

case class UpdateByQueryResponse(took: Long,
                                 timedOut: Boolean,
                                 total: Long,
                                 updated: Long,
                                 deleted: Long,
                                 batches: Long,
                                 versionConflicts: Long,
                                 noops: Long,
                                 throttledMillis: Long,
                                 requestsPerSecond: Long,
                                 throttledUntilMillis: Long)
