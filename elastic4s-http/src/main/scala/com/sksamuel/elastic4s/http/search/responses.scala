package com.sksamuel.elastic4s.http.search

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.get.HitField
import com.sksamuel.elastic4s.http.{Shards, SourceAsContentBuilder}
import com.sksamuel.elastic4s.http.explain.Explanation
import com.sksamuel.elastic4s.{Hit, HitReader}

case class SearchHit(@JsonProperty("_id") id: String,
                     @JsonProperty("_index") index: String,
                     @JsonProperty("_type") `type`: String,
                     @JsonProperty("_score") score: Float,
                     @JsonProperty("_parent") parent: Option[String],
                     @JsonProperty("_explanation") explanation: Option[Explanation],
                     private val _source: Map[String, AnyRef],
                     fields: Map[String, AnyRef],
                     highlight: Map[String, Seq[String]],
                     private val inner_hits: Map[String, Map[String, Any]],
                     @JsonProperty("_version") version: Long) extends Hit {

  def highlightFragments(name: String): Seq[String] = Option(highlight).getOrElse(Map.empty).getOrElse(name, Nil)

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

  def innerHits: Map[String, InnerHits] = Option(inner_hits).getOrElse(Map.empty).mapValues { hits =>
      val v = hits("hits").asInstanceOf[Map[String, AnyRef]]
      InnerHits(
        total = v("total").asInstanceOf[Long],
        max_score = v("max_score").asInstanceOf[Double],
        hits = v("hits").asInstanceOf[Seq[Map[String, AnyRef]]].map { hits =>
          InnerHit(
            nested = hits.get("_nested").map(_.asInstanceOf[Map[String, AnyRef]]).getOrElse(Map.empty),
            score = hits("_score").asInstanceOf[Double],
            source = hits("_source").asInstanceOf[Map[String, AnyRef]],
            highlight = hits.get("highlight").map(_.asInstanceOf[Map[String, Seq[String]]]).getOrElse(Map.empty)
          )
        }
      )
  }
}

case class SearchHits(total: Long,
                      @JsonProperty("max_score") maxScore: Double,
                      hits: Array[SearchHit]) {
  def size: Int = hits.length
  def isEmpty: Boolean = hits.isEmpty
  def nonEmpty: Boolean = hits.nonEmpty
}

case class InnerHits(total: Long,
                     max_score: Double,
                     hits: Seq[InnerHit])

case class InnerHit(nested: Map[String, AnyRef],
                    score: Double,
                    source: Map[String, AnyRef],
                    highlight: Map[String, Seq[String]])

case class SuggestionResult(text: String,
                            offset: Int,
                            length: Int,
                            options: Seq[Map[String, Any]]) {
  def toCompletion: CompletionSuggestionResult = CompletionSuggestionResult(text, offset, length, options.map(CompletionSuggestionOption))
  def toTerm: TermSuggestionResult = TermSuggestionResult(text, offset, length, options.map(TermSuggestionOption))
  def toPhrase: PhraseSuggestionResult = PhraseSuggestionResult(text, offset, length, options.map(PhraseSuggestionOption))
}

case class PhraseSuggestionResult(text: String, offset: Int, length: Int, options: Seq[PhraseSuggestionOption]) {
  def optionsText: Seq[String] = options.map(_.text)
}

case class PhraseSuggestionOption(private val options: Map[String, Any]) {
  val text: String = options("text").asInstanceOf[String]
  val highlighted: String = options("highlighted").asInstanceOf[String]
  val score: Double = options("score").asInstanceOf[Double]
}

case class CompletionSuggestionResult(text: String, offset: Int, length: Int, options: Seq[CompletionSuggestionOption]) {
  def optionsText: Seq[String] = options.map(_.text)
}

case class CompletionSuggestionOption(private val options: Map[String, Any]) {
  val text: String = options("text").asInstanceOf[String]
  val score: Double = options("_score").asInstanceOf[Double]
  val source: Map[String, AnyRef] = options.get("_source").map(_.asInstanceOf[Map[String, AnyRef]]).getOrElse(Map.empty)
  val index: Option[String] = options.get("_index").map(_.asInstanceOf[String])
  val `type`: Option[String] = options.get("_type").map(_.asInstanceOf[String])
  val id: Option[Any] = options.get("_id")
}

case class TermSuggestionResult(text: String, offset: Int, length: Int, options: Seq[TermSuggestionOption]) {
  def optionsText: Seq[String] = options.map(_.text)
}

case class TermSuggestionOption(private val options: Map[String, Any]) {
  val text: String = options("text").asInstanceOf[String]
  val score: Double = options("score").asInstanceOf[Double]
  val freq: Int = options("freq").asInstanceOf[Int]
}

case class Bucket(key: String,
                  @JsonProperty("doc_count") docCount: Long,
                  aggdata: Map[String, AnyRef]) extends AggregationResponse

trait AggregationResponse {

  protected def aggdata: Map[String, AnyRef]
  protected def agg(name: String): Map[String, AnyRef] = aggdata(name).asInstanceOf[Map[String, AnyRef]]

  def termsAgg(name: String): TermsAggregationResult = {
    TermsAggregationResult(
      name,
      agg(name)("buckets").asInstanceOf[Seq[Map[String, AnyRef]]].map { map => Bucket(map("key").toString, map("doc_count").toString.toLong, map) },
      agg(name)("doc_count_error_upper_bound").toString.toLong,
      agg(name)("sum_other_doc_count").toString.toLong
    )
  }

  def avgAgg(name: String): AvgAggregationResult = AvgAggregationResult(name, agg(name)("value").toString.toDouble)
  def bucketScriptAgg(name: String): BucketScriptAggregationResult = BucketScriptAggregationResult(name, agg(name)("value"))
  def childrenAgg(name: String): ChildrenAggregationResult = ChildrenAggregationResult(name, agg(name)("doc_count").toString.toLong, agg(name))
  def cardinalityAgg(name: String): CardinalityAggregationResult = CardinalityAggregationResult(name, agg(name)("value").toString.toLong)
  def sumAgg(name: String): SumAggregationResult = SumAggregationResult(name, agg(name)("value").toString.toDouble)
  def scriptedMetricAgg(name: String): ScriptedMetricAggregationResult = ScriptedMetricAggregationResult(name, agg(name)("value"))
  def minAgg(name: String): MinAggregationResult = MinAggregationResult(name, agg(name)("value").toString.toDouble)
  def maxAgg(name: String): MaxAggregationResult = MaxAggregationResult(name, agg(name)("value").toString.toDouble)
  def filterAgg(name: String): FilterAggregationResult = FilterAggregationResult(name, agg(name)("doc_count").toString.toLong, agg(name))
  def reverseNestedAgg(name: String): ReverseNestedAggregationResult = ReverseNestedAggregationResult(name, agg(name)("doc_count").toString.toLong, agg(name))

  def filtersAgg(name: String): FiltersAggregationResult = {
    FiltersAggregationResult(
      name,
      agg(name)("buckets").asInstanceOf[Map[String, Map[String, AnyRef]]].map(bucket => Bucket(bucket._1, bucket._2("doc_count").toString.toLong, bucket._2)).toSeq
    )
  }
  def histogramAgg(name: String): HistogramAggregationResult = {
    HistogramAggregationResult(
      name,
      agg(name)("buckets").asInstanceOf[Map[String, Map[String, AnyRef]]].map(bucket => Bucket(bucket._1, bucket._2("doc_count").toString.toLong, bucket._2)).toSeq
    )
  }
}

case class SearchResponse(took: Int,
                          @JsonProperty("timed_out") isTimedOut: Boolean,
                          @JsonProperty("terminated_early") isTerminatedEarly: Boolean,
                          private val suggest: Map[String, Seq[SuggestionResult]],
                          @JsonProperty("_shards") shards: Shards,
                          @JsonProperty("_scroll_id") scrollId: Option[String],
                          aggregations: Map[String, AnyRef],
                          hits: SearchHits) extends AggregationResponse {

  protected def aggdata: Map[String, AnyRef] = aggregations

  def totalHits: Long = hits.total
  def size: Int = hits.size
  def ids: Seq[String] = hits.hits.map(_.id)
  def maxScore: Double = hits.maxScore

  def isEmpty: Boolean = hits.isEmpty
  def nonEmpty: Boolean = hits.nonEmpty

  def aggregationsAsString: String = SourceAsContentBuilder(aggregations).string()
  def aggregationsAsMap: Map[String, AnyRef] = aggregations

  private def suggestion(name: String): Map[String, SuggestionResult] = suggest(name).map { result => result.text -> result }.toMap

  def termSuggestion(name: String): Map[String, TermSuggestionResult] = suggestion(name).mapValues(_.toTerm)
  def completionSuggestion(name: String): Map[String, CompletionSuggestionResult] = suggestion(name).mapValues(_.toCompletion)
  def phraseSuggestion(name: String): Map[String, PhraseSuggestionResult] = suggestion(name).mapValues(_.toPhrase)

  def to[T: HitReader]: IndexedSeq[T] = hits.hits.map(_.to[T]).toIndexedSeq
  def safeTo[T: HitReader]: IndexedSeq[Either[Throwable, T]] = hits.hits.map(_.safeTo[T]).toIndexedSeq
}
case class AvgAggregationResult(name: String, value: Double)
case class BucketScriptAggregationResult(name: String, value: AnyRef)
case class CardinalityAggregationResult(name: String, value: Long)
case class MinAggregationResult(name: String, value: Double)
case class MaxAggregationResult(name: String, value: Double)
case class ScriptedMetricAggregationResult(name: String, value: AnyRef)
case class SumAggregationResult(name: String, value: Double)

case class ChildrenAggregationResult(name: String,
                                     docCount: Long,
                                     aggdata: Map[String, AnyRef]) extends AggregationResponse

case class FilterAggregationResult(name: String,
                                   docCount: Long,
                                   aggdata: Map[String, AnyRef]) extends AggregationResponse

case class ReverseNestedAggregationResult(name: String,
  docCount: Long,
  aggdata: Map[String, AnyRef]) extends AggregationResponse
case class HistogramAggregationResult(name: String, buckets: Seq[Bucket])
case class FiltersAggregationResult(name: String, buckets: Seq[Bucket])

case class TermsAggregationResult(name: String,
                                  buckets: Seq[Bucket],
                                  docCountErrorUpperBound: Long,
                                  otherDocCount: Long) {

  @deprecated("use buckets", "5.2.9")
  def getBuckets: Seq[Bucket] = buckets

  @deprecated("use bucket", "5.2.9")
  def getBucketByKey(key: String): Bucket = bucket(key)

  def bucket(key: String): Bucket = bucketOpt(key).get
  def bucketOpt(key: String): Option[Bucket] = buckets.find(_.key == key)
}
