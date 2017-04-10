package com.sksamuel.elastic4s.http.search

import com.sksamuel.elastic4s.get.HitField
import com.sksamuel.elastic4s.http.{Shards, SourceAsContentBuilder}
import com.sksamuel.elastic4s.{Hit, HitReader}

case class SearchHit(private val _id: String,
                     private val _index: String,
                     private val _type: String,
                     private val _score: Float,
                     private val _source: Map[String, AnyRef],
                     fields: Map[String, AnyRef],
                     highlight: Map[String, Seq[String]],
                     private val _version: Long) extends Hit {

  def highlightFragments(name: String): Seq[String] = Option(highlight).getOrElse(Map.empty).getOrElse(name, Nil)

  override def index: String = _index
  override def id: String = _id
  override def `type`: String = _type
  override def version: Long = _version

  def storedField(fieldName: String): HitField = storedFieldOpt(fieldName).get
  def storedFieldOpt(fieldName: String): Option[HitField] = fields.get(fieldName).map { v =>
    new HitField {
      override def values: Seq[AnyRef] = v match {
        case values: Seq[AnyRef] => values
        case value: AnyRef => Seq(value)
      }
      override def value: AnyRef = values.head
      override def name: String = fieldName
      override def isMetadataField: Boolean = ???
    }
  }

  override def sourceAsMap: Map[String, AnyRef] = _source
  override def sourceAsString: String = SourceAsContentBuilder(_source).string()

  override def exists: Boolean = true
  override def score: Float = _score
}

case class SearchHits(total: Int,
                      private val max_score: Double,
                      hits: Array[SearchHit]) {
  def maxScore: Double = max_score
  def size: Int = hits.length
  def isEmpty: Boolean = hits.isEmpty
  def nonEmpty: Boolean = hits.nonEmpty
}

case class SuggestionEntry(term: String) {
  def options: Seq[String] = Nil
  def optionsText: String = ""
}

case class CompletionSuggestionResult(entries: Seq[SuggestionEntry]) {
  def entry(term: String): SuggestionEntry = entries.find(_.term == term).get
}

case class PhraseSuggestionResult(entries: Seq[SuggestionEntry]) {
  def entry(term: String): SuggestionEntry = entries.find(_.term == term).get
}

case class SuggestionOption(text: String, score: Double, freq: Int)

case class SuggestionResult(text: String,
                            offset: Int,
                            length: Int,
                            options: Seq[SuggestionOption]) {
  def toTerm: TermSuggestionResult = TermSuggestionResult(text, offset, length, options)
}

case class TermSuggestionResult(text: String,
                                offset: Int,
                                length: Int,
                                options: Seq[SuggestionOption]) {
  def optionsText: Seq[String] = options.map(_.text)
}

case class Bucket(key: String,
                  private val doc_count: Int) {
  def docCount: Int = doc_count
  @deprecated("use getDocCount", "5.2.9")
  def getDocCount: Int = docCount
}

trait AggregationResponse {

  protected def aggdata: Map[String, AnyRef]
  protected def agg(name: String): Map[String, AnyRef] = aggdata(name).asInstanceOf[Map[String, AnyRef]]

  def termsAgg(name: String): TermsAggregationResult = {
    TermsAggregationResult(
      name,
      agg(name)("buckets").asInstanceOf[Seq[Map[String, AnyRef]]].map { map => Bucket(map("key").toString, map("doc_count").toString.toInt) },
      agg(name)("doc_count_error_upper_bound").toString.toInt,
      agg(name)("sum_other_doc_count").toString.toInt
    )
  }

  def sumAgg(name: String): SumAggregationResult = SumAggregationResult(name, agg(name)("value").toString.toDouble)
  def minAgg(name: String): MinAggregationResult = MinAggregationResult(name, agg(name)("value").toString.toDouble)
  def maxAgg(name: String): MaxAggregationResult = MaxAggregationResult(name, agg(name)("value").toString.toDouble)
  def filterAgg(name: String): FilterAggregationResult = FilterAggregationResult(name, agg(name)("doc_count").toString.toInt, agg(name))
}

case class SearchResponse(took: Int,
                          private val timed_out: Boolean,
                          private val terminated_early: Option[Boolean],
                          private val suggest: Map[String, Seq[SuggestionResult]],
                          private val _shards: Shards,
                          private val _scroll_id: String,
                          aggregations: Map[String, AnyRef],
                          hits: SearchHits) extends AggregationResponse {

  protected def aggdata: Map[String, AnyRef] = aggregations

  def totalHits: Int = hits.total
  def size: Int = hits.size
  def ids: Seq[String] = hits.hits.map(_.id)
  def maxScore: Double = hits.maxScore
  def scrollId: Option[String] = Option(_scroll_id)

  def shards: Shards = _shards

  def isTimedOut: Boolean = timed_out
  def isTerminatedEarly: Option[Boolean] = terminated_early

  def isEmpty: Boolean = hits.isEmpty
  def nonEmpty: Boolean = hits.nonEmpty

  private def suggestion(name: String): Map[String, SuggestionResult] = suggest(name).map { result => result.text -> result }.toMap

  def termSuggestion(name: String): Map[String, TermSuggestionResult] = suggestion(name).mapValues(_.toTerm)
  def completionSuggestion(name: String): CompletionSuggestionResult = suggestion(name).asInstanceOf[CompletionSuggestionResult]
  def phraseSuggestion(name: String): PhraseSuggestionResult = suggestion(name).asInstanceOf[PhraseSuggestionResult]

  def to[T: HitReader]: IndexedSeq[T] = hits.hits.map(_.to[T]).toIndexedSeq
  def safeTo[T: HitReader]: IndexedSeq[Either[Throwable, T]] = hits.hits.map(_.safeTo[T]).toIndexedSeq
}

case class SumAggregationResult(name: String, value: Double)
case class MinAggregationResult(name: String, value: Double)
case class MaxAggregationResult(name: String, value: Double)

case class FilterAggregationResult(name: String,
                                   docCount: Int,
                                   aggdata: Map[String, AnyRef]) extends AggregationResponse


case class TermsAggregationResult(name: String,
                                  buckets: Seq[Bucket],
                                  docCountErrorUpperBound: Int,
                                  otherDocCount: Int) {

  @deprecated("use buckets", "5.2.9")
  def getBuckets: Seq[Bucket] = buckets

  @deprecated("use bucket", "5.2.9")
  def getBucketByKey(key: String): Bucket = bucket(key)

  def bucket(key: String): Bucket = bucketOpt(key).get
  def bucketOpt(key: String): Option[Bucket] = buckets.find(_.key == key)
}
