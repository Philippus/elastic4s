package com.sksamuel.elastic4s.http.delete

case class DeleteByQueryResponse(took: Long,
                                 timed_out: Boolean,
                                 total: Long,
                                 deleted: Long,
                                 batches: Long,
                                 version_conflicts: Long,
                                 noops: Long,
                                 throttled_millis: Long,
                                 requests_per_second: Long,
                                 throttled_until_millis: Long
                                )
