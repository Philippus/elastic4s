package com.sksamuel.elastic4s.http.search

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.get.HitField
import com.sksamuel.elastic4s.http.SourceAsContentBuilder
import com.sksamuel.elastic4s.http.values.Shards
import com.sksamuel.elastic4s.{Hit, HitReader}

case class SearchHit(@JsonProperty("_id") id: String,
                     @JsonProperty("_index") index: String,
                     @JsonProperty("_type") `type`: String,
                     @JsonProperty("_score") score: Float,
                     @JsonProperty("_parent") parent: Option[String],
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
        total = v("total").asInstanceOf[Int],
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

case class SearchHits(total: Int,
                      @JsonProperty("max_score") maxScore: Double,
                      hits: Array[SearchHit]) {
  def size: Int = hits.length
  def isEmpty: Boolean = hits.isEmpty
  def nonEmpty: Boolean = hits.nonEmpty
}

case class InnerHits(total: Int,
                     max_score: Double,
                     hits: Seq[InnerHit])

case class InnerHit(nested: Map[String, AnyRef],
                    score: Double,
                    source: Map[String, AnyRef],
                    highlight: Map[String, Seq[String]])

case class SearchResponse(took: Int,
                          @JsonProperty("timed_out") isTimedOut: Boolean,
                          @JsonProperty("terminated_early") isTerminatedEarly: Boolean,
                          private val suggest: Map[String, Seq[SuggestionResult]],
                          @JsonProperty("_shards") shards: Shards,
                          @JsonProperty("_scroll_id") scrollId: Option[String],
                          @JsonProperty("aggregations") aggregationsAsMap: Map[String, Any],
                          hits: SearchHits) {

  def totalHits: Int = hits.total
  def size: Int = hits.size
  def ids: Seq[String] = hits.hits.map(_.id)
  def maxScore: Double = hits.maxScore

  def isEmpty: Boolean = hits.isEmpty
  def nonEmpty: Boolean = hits.nonEmpty

  def aggregationsAsString: String = SourceAsContentBuilder(aggregationsAsMap).string()
  def aggs: Aggregations = aggregations
  def aggregations: Aggregations = Aggregations(aggregationsAsMap)

  private def suggestion(name: String): Map[String, SuggestionResult] = suggest(name).map { result => result.text -> result }.toMap

  def termSuggestion(name: String): Map[String, TermSuggestionResult] = suggestion(name).mapValues(_.toTerm)
  def completionSuggestion(name: String): Map[String, CompletionSuggestionResult] = suggestion(name).mapValues(_.toCompletion)
  def phraseSuggestion(name: String): Map[String, PhraseSuggestionResult] = suggestion(name).mapValues(_.toPhrase)

  def to[T: HitReader]: IndexedSeq[T] = hits.hits.map(_.to[T]).toIndexedSeq
  def safeTo[T: HitReader]: IndexedSeq[Either[Throwable, T]] = hits.hits.map(_.safeTo[T]).toIndexedSeq
}
