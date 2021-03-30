package com.sksamuel.elastic4s.requests.bulk

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.common.Shards
import com.sksamuel.elastic4s.requests.searches.aggs.responses.JacksonSupport
import com.sksamuel.elastic4s.requests.update.UpdateGet

sealed trait BulkResponseItem {
  type Self <: BulkResponseItem

  def withItemId(itemId: Int): Self

  def itemId: Int

  def id: String

  def index: String

  def `type`: String

  def version: Long

  def forcedRefresh: Boolean

  def seqNo: Long

  def primaryTerm: Long

  def found: Boolean

  def created: Boolean

  def result: String

  def status: Int

  def error: Option[BulkError]

  def shards: Option[Shards]

}

case class IndexBulkResponseItem(itemId: Int,
                                 @JsonProperty("_id") id: String,
                                 @JsonProperty("_index") index: String,
                                 @JsonProperty("_type") `type`: String,
                                 @JsonProperty("_version") version: Long,
                                 @JsonProperty("forced_refresh") forcedRefresh: Boolean,
                                 @JsonProperty("_seq_no") seqNo: Long,
                                 @JsonProperty("_primary_term") primaryTerm: Long,
                                 found: Boolean,
                                 created: Boolean,
                                 result: String,
                                 status: Int,
                                 error: Option[BulkError],
                                 @JsonProperty("_shards") shards: Option[Shards]) extends BulkResponseItem {
  override type Self = IndexBulkResponseItem

  override def withItemId(itemId: Int): IndexBulkResponseItem = this.copy(itemId)
}

case class DeleteBulkResponseItem(itemId: Int,
                                  @JsonProperty("_id") id: String,
                                  @JsonProperty("_index") index: String,
                                  @JsonProperty("_type") `type`: String,
                                  @JsonProperty("_version") version: Long,
                                  @JsonProperty("forced_refresh") forcedRefresh: Boolean,
                                  @JsonProperty("_seq_no") seqNo: Long,
                                  @JsonProperty("_primary_term") primaryTerm: Long,
                                  found: Boolean,
                                  created: Boolean,
                                  result: String,
                                  status: Int,
                                  error: Option[BulkError],
                                  @JsonProperty("_shards") shards: Option[Shards]) extends BulkResponseItem {
  override type Self = DeleteBulkResponseItem

  override def withItemId(itemId: Int): DeleteBulkResponseItem = this.copy(itemId)
}

case class UpdateBulkResponseItem(itemId: Int,
                                  @JsonProperty("_id") id: String,
                                  @JsonProperty("_index") index: String,
                                  @JsonProperty("_type") `type`: String,
                                  @JsonProperty("_version") version: Long,
                                  @JsonProperty("forced_refresh") forcedRefresh: Boolean,
                                  @JsonProperty("_seq_no") seqNo: Long,
                                  @JsonProperty("_primary_term") primaryTerm: Long,
                                  found: Boolean,
                                  created: Boolean,
                                  result: String,
                                  status: Int,
                                  error: Option[BulkError],
                                  @JsonProperty("_shards") shards: Option[Shards],
                                  private val get: Option[UpdateGet] = None) extends BulkResponseItem {
  override type Self = UpdateBulkResponseItem

  override def withItemId(itemId: Int): UpdateBulkResponseItem = this.copy(itemId)

  def source: Option[Map[String, Any]] = get.map(_._source)

  def sourceAsString: Option[String] = source.map(JacksonSupport.mapper.writeValueAsString)
}

case class CreateBulkResponseItem(itemId: Int,
                                  @JsonProperty("_id") id: String,
                                  @JsonProperty("_index") index: String,
                                  @JsonProperty("_type") `type`: String,
                                  @JsonProperty("_version") version: Long,
                                  @JsonProperty("forced_refresh") forcedRefresh: Boolean,
                                  @JsonProperty("_seq_no") seqNo: Long,
                                  @JsonProperty("_primary_term") primaryTerm: Long,
                                  found: Boolean,
                                  created: Boolean,
                                  result: String,
                                  status: Int,
                                  error: Option[BulkError],
                                  @JsonProperty("_shards") shards: Option[Shards]) extends BulkResponseItem {
  override type Self = CreateBulkResponseItem

  override def withItemId(itemId: Int): CreateBulkResponseItem = this.copy(itemId)
}

case class CausedBy(`type`: String, reason: String)

case class BulkError(`type`: String, reason: String, index_uuid: String, shard: Int, index: String, caused_by: Option[CausedBy])

case class BulkResponseItems(index: Option[IndexBulkResponseItem],
                             delete: Option[DeleteBulkResponseItem],
                             update: Option[UpdateBulkResponseItem],
                             create: Option[CreateBulkResponseItem])

case class BulkResponse(took: Long,
                        errors: Boolean,
                        @JsonProperty("items") private val _items: Seq[BulkResponseItems]) {

  def items: Seq[BulkResponseItem] =
    _items
      .flatMap { item => item.index.orElse(item.update).orElse(item.delete).orElse(item.create) }
      .zipWithIndex
      .map { case (item, index) => item.withItemId(index) }

  def failures: Seq[BulkResponseItem] = items.filter(_.status >= 300)

  def successes: Seq[BulkResponseItem] = items.filter(_.status < 300)

  def hasSuccesses: Boolean = items.exists(_.status < 300)

  def hasFailures: Boolean = items.exists(_.status >= 300)
}
