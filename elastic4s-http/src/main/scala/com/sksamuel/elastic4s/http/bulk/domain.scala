package com.sksamuel.elastic4s.http.bulk

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.http.Shards

case class BulkResponseItem(itemId: Int,
                            @JsonProperty("_id") id: String,
                            @JsonProperty("_index") index: String,
                            @JsonProperty("_type") `type`: String,
                            @JsonProperty("_version") version: Long,
                            @JsonProperty("forced_refresh") forcedRefresh: Boolean,
                            found: Boolean,
                            created: Boolean,
                            result: String,
                            status: Int,
                            error: Option[BulkError],
                            @JsonProperty("_shards") shards: Option[Shards])

case class BulkError(`type`: String,
                     reason: String,
                     index_uuid: String,
                     shard: Int,
                     index: String)

case class BulkResponseItems(index: Option[BulkResponseItem],
                             delete: Option[BulkResponseItem],
                             update: Option[BulkResponseItem])

case class BulkResponse(took: Long,
                        errors: Boolean,
                        @JsonProperty("items") private val _items: Seq[BulkResponseItems]) {

  def items: Seq[BulkResponseItem] = _items.flatMap { item =>
    item.index.orElse(item.update).orElse(item.delete)
  }.zipWithIndex.map { case (item, index) =>
    item.copy(itemId = index)
  }

  def failures: Seq[BulkResponseItem] = items.filter(_.status >= 300)
  def successes: Seq[BulkResponseItem] = items.filter(_.status < 300)
  def hasSuccesses: Boolean = items.exists(_.status < 300)
  def hasFailures: Boolean = items.exists(_.status >= 300)
}
