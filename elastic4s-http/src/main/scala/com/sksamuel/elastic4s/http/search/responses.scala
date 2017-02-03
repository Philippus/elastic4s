package com.sksamuel.elastic4s.http.search

import cats.syntax.either._
import com.sksamuel.elastic4s.http.Shards
import com.sksamuel.elastic4s.{Hit, HitReader}
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

case class SearchHit(private val _id: String,
                     private val _index: String,
                     private val _type: String,
                     private val _score: Double,
                     private val _source: Map[String, AnyRef],
                     private val _version: Long) extends Hit {

  override def index: String = _index
  override def id: String = _id
  override def `type`: String = _type
  override def version: Long = _version

  override def sourceAsMap: Map[String, AnyRef] = _source
  override def sourceAsBytes: Array[Byte] = sourceAsString.getBytes("UTF8")
  override def sourceAsString: String = sourceAsXContentBuilder.string

  def sourceAsXContentBuilder: XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    def addMap(map: Map[String, AnyRef]): Unit = {
      builder.startObject()
      map.foreach {
        case (key, value: Map[String, AnyRef]) => addMap(value)
        case (key, value) => builder.field(key, value)
      }
      builder.endObject()
    }
    addMap(_source)
    builder
  }

  override def exists: Boolean = true
}

case class SearchHits(total: Int,
                      private val max_score: Double,
                      hits: Array[SearchHit]) {
  def maxScore: Double = max_score
  def size: Int = hits.length
  def isEmpty: Boolean = hits.isEmpty
  def nonEmpty: Boolean = hits.nonEmpty
}

case class SearchResponse(took: Int,
                          private val timed_out: Boolean,
                          private val terminated_early: Boolean,
                          _shards: Shards,
                          hits: SearchHits) {

  def totalHits: Int = hits.total
  def size: Int = hits.size
  def ids: Seq[String] = hits.hits.map(_.id)
  def maxScore: Double = hits.maxScore

  def isTimedOut: Boolean = timed_out
  def isTerminatedEarly: Boolean = terminated_early

  def isEmpty: Boolean = hits.isEmpty
  def nonEmpty: Boolean = hits.nonEmpty

  def to[T: HitReader]: IndexedSeq[T] = safeTo.flatMap(_.toOption)
  def safeTo[T: HitReader]: IndexedSeq[Either[Throwable, T]] = hits.hits.map(_.safeTo[T]).toIndexedSeq
}

case class MultiSearchResponse(responses: Seq[SearchResponse]) {
  def size = responses.size
}
