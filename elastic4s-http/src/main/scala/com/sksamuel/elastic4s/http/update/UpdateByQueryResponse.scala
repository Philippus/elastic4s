package com.sksamuel.elastic4s.http.update

case class UpdateByQueryResponse(took: Long,
                                 timed_out: Boolean,
                                 total: Long,
                                 updated: Long,
                                 deleted: Long,
                                 batches: Long,
                                 versionConflicts: Long,
                                 noops: Long,
                                 throttledMillis: Long,
                                 requestsPerSecond: Long,
                                 throttledUntilMillis: Long
                                )
