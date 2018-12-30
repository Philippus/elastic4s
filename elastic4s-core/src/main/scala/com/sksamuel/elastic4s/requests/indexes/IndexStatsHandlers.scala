package com.sksamuel.elastic4s.requests.indexes

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.{ElasticRequest, Handler}
import com.sksamuel.elastic4s.requests.admin.IndexStatsRequest

case class Docs(count: Long, deleted: Long)
case class Store(@JsonProperty("size_in_bytes") sizeInBytes: Long)
case class Indexing(
  @JsonProperty("index_total") indexTotal: Long,
  @JsonProperty("index_time_in_millis") indexTimeInMillis: Long,
  @JsonProperty("index_current") indexCurrent: Long,
  @JsonProperty("index") indexFailed: Long,
  @JsonProperty("delete_total") deleteTotal: Long,
  @JsonProperty("delete") deleteTimeInMillis: Long,
  @JsonProperty("delete") deleteCurrent: Long,
  @JsonProperty("noop_update_total") noopUpdateTotal: Long,
  @JsonProperty("is_throttled") isThrottled: Long,
  @JsonProperty("throttle_time_in_millis") throttleTimeInMillis: Long
)
case class Get(
  @JsonProperty("total") total: Long,
  @JsonProperty("time_in_millis") timeInMillis: Long,
  @JsonProperty("exists_total") existsTotal: Long,
  @JsonProperty("exists_time_in_millis") existsTimeInMillis: Long,
  @JsonProperty("missing_total") missingTotal: Long,
  @JsonProperty("missing_time_in_millis") missingTimeInMillis: Long,
  @JsonProperty("current") current: Long
)

case class Search(
  @JsonProperty("open_contexts") openContexts: Long,
  @JsonProperty("query_total") queryTotal: Long,
  @JsonProperty("query_time_in_millis") queryTimeInMillis: Long,
  @JsonProperty("query_current") queryCurrent: Long,
  @JsonProperty("fetch_total") fetchTotal: Long,
  @JsonProperty("fetch_time_in_millis") fetchTimeInMillis: Long,
  @JsonProperty("fetch_current") fetchCurrent: Long,
  @JsonProperty("scroll_total") scrollTotal: Long,
  @JsonProperty("scroll_time_in_millis") scrollTimeInMillis: Long,
  @JsonProperty("scroll_current") scrollCurrent: Long,
  @JsonProperty("suggest_total") suggestTotal: Long,
  @JsonProperty("suggest_time_in_millis") suggestTimeInMillis: Long,
  @JsonProperty("suggest_current") suggestCurrent: Long
)

case class Merges(
  @JsonProperty("current") current: Long,
  @JsonProperty("current_docs") currentDocs: Long,
  @JsonProperty("current_size_in_bytes") currentSizeInBytes: Long,
  @JsonProperty("total") total: Long,
  @JsonProperty("total_time_in_millis") totalTimeInMillis: Long,
  @JsonProperty("total_docs") totalDocs: Long,
  @JsonProperty("total_size_in_bytes") totalSizeInBytes: Long,
  @JsonProperty("total_stopped_time_in_millis") totalStoppedTimeInMillis: Long,
  @JsonProperty("total_throttled_time_in_millis") totalThrottledTimeInMillis: Long,
  @JsonProperty("total_auto_throttle_in_bytes") totalAutoThrottleInBytes: Long
)

case class Segments(
  @JsonProperty("count") count: Long,
  @JsonProperty("memory_in_bytes") memoryInBytes: Long,
  @JsonProperty("terms_memory_in_bytes") termsMemoryInBytes: Long,
  @JsonProperty("stored_fields_memory_in_bytes") storedFieldsMemoryInBytes: Long,
  @JsonProperty("term_vectors_memory_in_bytes") termVectorsMemoryInBytes: Long,
  @JsonProperty("norms_memory_in_bytes") normsMemoryInBytes: Long,
  @JsonProperty("points_memory_in_bytes") pointsMemoryInBytes: Long,
  @JsonProperty("doc_values_memory_in_bytes") docValuesMemoryInBytes: Long,
  @JsonProperty("index_writer_memory_in_bytes") indexWriterMemoryInBytes: Long,
  @JsonProperty("version_map_memory_in_bytes") versionMapMemoryInBytes: Long,
  @JsonProperty("fixed_bit_set_memory_in_bytes") fixedBitSetMemoryInBytes: Long,
  @JsonProperty("max_unsafe_auto_id_timestamp") maxUnsafeAutoIdTimestamp: Long
)

case class RequestCache(
  @JsonProperty("memory_size_in_bytes") memorySizeInBytes: Long,
  @JsonProperty("evictions") evictions: Long,
  @JsonProperty("hit_count") hitCount: Long,
  @JsonProperty("miss_count") missCount: Long
)

case class Recovery(
  @JsonProperty("current_as_source") currentAsSource: Long,
  @JsonProperty("current_as_target") currentAsTarget: Long,
  @JsonProperty("throttle_time_in_millis") throttleTimeInMillis: Long
)

case class TransLog(
  @JsonProperty("operations") operations: Long,
  @JsonProperty("size_in_bytes") sizeInBytes: Long,
  @JsonProperty("uncommitted_operations") uncommittedOperations: Long,
  @JsonProperty("uncommitted_size_in_bytes") uncommittedSizeInBytes: Long
)

case class QueryCache(
  @JsonProperty("memory_size_in_bytes") memorySizeInBytes: Long,
  @JsonProperty("total_count") totalCount: Long,
  @JsonProperty("hit_count") hitCount: Long,
  @JsonProperty("miss_count") missCount: Long,
  @JsonProperty("cache_size") cacheSize: Long,
  @JsonProperty("cache_count") cacheCount: Long,
  @JsonProperty("evictions") evictions: Long
)

case class Stats(docs: Docs,
                 store: Store,
                 get: Get,
                 search: Search,
                 merges: Merges,
                 segments: Segments,
                 @JsonProperty("request_cache") requestCache: RequestCache,
                 @JsonProperty("query_cache") queryCache: QueryCache,
                 recovery: Recovery,
                 translog: TransLog)

case class IndexStatsGroup(primaries: Stats, total: Stats)

case class IndexStatsResponse(@JsonProperty("_all") all: IndexStatsGroup, indices: Map[String, IndexStatsGroup])

trait IndexStatsHandlers {

  implicit object IndicesStatsHandler extends Handler[IndexStatsRequest, IndexStatsResponse] {

    override def build(request: IndexStatsRequest): ElasticRequest = {
      val endpoint = if (request.indices.isAll) "/_stats" else s"/${request.indices.string}/_stats"
      ElasticRequest("GET", endpoint)
    }
  }
}
