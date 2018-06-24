package com.sksamuel.elastic4s.http.search

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.get.HitField
import com.sksamuel.elastic4s.http.explain.Explanation
import com.sksamuel.elastic4s.http.get.MetaDataFields
import com.sksamuel.elastic4s.http.{Shards, SourceAsContentBuilder}
import com.sksamuel.elastic4s.{Hit, HitReader}

import scala.util.Try

case class SearchHit(@JsonProperty("_id") id: String,
                     @JsonProperty("_index") index: String,
                     @JsonProperty("_type") `type`: String,
                     @JsonProperty("_version") version: Long,
                     @JsonProperty("_score") score: Float,
                     @JsonProperty("_parent") parent: Option[String],
                     @JsonProperty("_shard") shard: Option[String],
                     @JsonProperty("_node") node: Option[String],
                     @JsonProperty("_routing") routing: Option[String],
                     @JsonProperty("_explanation") explanation: Option[Explanation],
                     @JsonProperty("sort") sort: Option[Seq[AnyRef]],
                     private val _source: Map[String, AnyRef],
                     fields: Map[String, AnyRef],
                     highlight: Map[String, Seq[String]],
                     private val inner_hits: Map[String, Map[String, Any]])
    extends Hit {

  def highlightFragments(name: String): Seq[String] = Option(highlight).getOrElse(Map.empty).getOrElse(name, Nil)

  def storedField(fieldName: String): HitField = storedFieldOpt(fieldName).get
  def storedFieldOpt(fieldName: String): Option[HitField] = fields.get(fieldName).map { v =>
    new HitField {
      override def values: Seq[AnyRef] = v match {
        case values: Seq[AnyRef] => values
        case value: AnyRef       => Seq(value)
      }
      override def value: AnyRef            = values.head
      override def name: String             = fieldName
      override def isMetadataField: Boolean = MetaDataFields.fields.contains(name)
    }
  }

  override def sourceAsMap: Map[String, AnyRef] = _source
  override def sourceAsString: String           = SourceAsContentBuilder(_source).string()

  override def exists: Boolean = true

  private def buildInnerHits(_hits: Map[String, Map[String, Any]]): Map[String, InnerHits] =
    Option(_hits).getOrElse(Map.empty).mapValues { hits =>
      val v = hits("hits").asInstanceOf[Map[String, AnyRef]]
      InnerHits(
        total = v("total").toString.toLong,
        max_score = v("max_score").asInstanceOf[Double],
        hits = v("hits").asInstanceOf[Seq[Map[String, AnyRef]]].map { hits =>
          InnerHit(
            nested = hits.get("_nested").map(_.asInstanceOf[Map[String, AnyRef]]).getOrElse(Map.empty),
            score = hits("_score").asInstanceOf[Double],
            source = hits.get("_source").map(_.asInstanceOf[Map[String, AnyRef]]).getOrElse(Map.empty),
            innerHits = buildInnerHits(hits.getOrElse("inner_hits", null).asInstanceOf[Map[String, Map[String, Any]]]),
            highlight = hits.get("highlight").map(_.asInstanceOf[Map[String, Seq[String]]]).getOrElse(Map.empty),
            sort = hits.get("sort").map(_.asInstanceOf[Seq[AnyRef]]).getOrElse(Seq.empty)
          )
        }
      )
    }

  def innerHits: Map[String, InnerHits] = buildInnerHits(inner_hits)
}

case class SearchHits(total: Long, @JsonProperty("max_score") maxScore: Double, hits: Array[SearchHit]) {
  def size: Long        = hits.length
  def isEmpty: Boolean  = hits.isEmpty
  def nonEmpty: Boolean = hits.nonEmpty
}

case class InnerHits(total: Long, max_score: Double, hits: Seq[InnerHit])

case class InnerHit(nested: Map[String, AnyRef],
                    score: Double,
                    source: Map[String, AnyRef],
                    innerHits: Map[String, InnerHits],
                    highlight: Map[String, Seq[String]],
                    sort: Seq[AnyRef])

case class SearchResponse(took: Long,
                          @JsonProperty("timed_out") isTimedOut: Boolean,
                          @JsonProperty("terminated_early") isTerminatedEarly: Boolean,
                          private val suggest: Map[String, Seq[SuggestionResult]],
                          @JsonProperty("_shards") private val _shards: Shards,
                          @JsonProperty("_scroll_id") scrollId: Option[String],
                          @JsonProperty("aggregations") private val _aggregationsAsMap: Map[String, Any],
                          hits: SearchHits) {

  def aggregationsAsMap: Map[String, Any] = Option(_aggregationsAsMap).getOrElse(Map.empty)
  def totalHits: Long                     = hits.total
  def size: Long                          = hits.size
  def ids: Seq[String]                    = hits.hits.map(_.id)
  def maxScore: Double                    = hits.maxScore

  def shards: Shards = Option(_shards).getOrElse(Shards(-1, -1, -1))

  def isEmpty: Boolean  = hits.isEmpty
  def nonEmpty: Boolean = hits.nonEmpty

  lazy val aggsAsContentBuilder         = SourceAsContentBuilder(aggregationsAsMap)
  lazy val aggregationsAsString: String = aggsAsContentBuilder.string()
  def aggs: Aggregations                = aggregations
  def aggregations: Aggregations        = Aggregations(aggregationsAsMap)

  private def suggestion(name: String): Map[String, SuggestionResult] =
    suggest
      .getOrElse(name, Nil)
      .map { result =>
        result.text -> result
      }
      .toMap

  def termSuggestion(name: String): Map[String, TermSuggestionResult] = suggestion(name).mapValues(_.toTerm)
  def completionSuggestion(name: String): Map[String, CompletionSuggestionResult] =
    suggestion(name).mapValues(_.toCompletion)
  def phraseSuggestion(name: String): Map[String, PhraseSuggestionResult] = suggestion(name).mapValues(_.toPhrase)

  def to[T: HitReader]: IndexedSeq[T]                        = hits.hits.map(_.to[T]).toIndexedSeq
  def safeTo[T: HitReader]: IndexedSeq[Try[T]] = hits.hits.map(_.safeTo[T]).toIndexedSeq
}
