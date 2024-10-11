package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.FetchSourceContext
import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.requests.searches.sort.Sort
import com.sksamuel.elastic4s.ext.OptionImplicits._
import com.sksamuel.elastic4s.requests.searches.{Highlight, HighlightField, HighlightOptions}

case class TopHitsAggregation(name: String,
                              explain: Option[Boolean] = None,
                              fetchSource: Option[FetchSourceContext] = None,
                              size: Option[Int] = None,
                              from: Option[Int] = None,
                              sorts: Seq[Sort] = Nil,
                              trackScores: Option[Boolean] = None,
                              version: Option[Boolean] = None,
                              scripts: Map[String, Script] = Map.empty,
                              storedFields: Seq[String] = Nil,
                              subaggs: Seq[AbstractAggregation] = Nil,
                              metadata: Map[String, AnyRef] = Map.empty,
                              highlight: Option[Highlight] = None,
                              docValueFields: Seq[String] = Nil)
    extends Aggregation {

  type T = TopHitsAggregation

  def explain(explain: Boolean): TopHitsAggregation = copy(explain = explain.some)

  def fetchSource(includes: Array[String], excludes: Array[String]): TopHitsAggregation =
    copy(fetchSource = FetchSourceContext(true, includes, excludes).some)

  def fetchSource(fetchSource: Boolean): TopHitsAggregation =
    copy(fetchSource = FetchSourceContext(fetchSource).some)

  def size(size: Int): TopHitsAggregation = copy(size = size.some)

  def from(from: Int): TopHitsAggregation = copy(from = from.some)

  def sortBy(first: Sort, rest: Sort*): TopHitsAggregation = sortBy(first +: rest)
  def sortBy(sorts: Iterable[Sort]): TopHitsAggregation    = copy(sorts = sorts.toSeq)

  def storedField(field: String): TopHitsAggregation                 = storedFields(field)
  def storedFields(first: String, rest: String*): TopHitsAggregation = storedFields(first +: rest)
  def storedFields(fields: Iterable[String]): TopHitsAggregation     = copy(storedFields = fields.toSeq)

  def docValueFields(docValueFields: Iterable[String]): TopHitsAggregation =
    copy(docValueFields = docValueFields.toSeq)

  def version(version: Boolean): TopHitsAggregation         = copy(version = version.some)
  def trackScores(trackScores: Boolean): TopHitsAggregation = copy(trackScores = trackScores.some)

  def script(name: String, script: Script): T = copy(scripts = scripts + (name -> script))

  def highlighting(first: HighlightField, rest: HighlightField*): TopHitsAggregation =
    highlighting(HighlightOptions(), first +: rest)

  def highlighting(fields: Iterable[HighlightField]): TopHitsAggregation =
    highlighting(HighlightOptions(), fields)

  def highlighting(options: HighlightOptions, first: HighlightField, rest: HighlightField*): TopHitsAggregation =
    highlighting(options, first +: rest)

  def highlighting(options: HighlightOptions, fields: Iterable[HighlightField]): TopHitsAggregation =
    copy(highlight = Highlight(options, fields).some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T =
    sys.error("Top Hits does not support sub aggregations")
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = map)
}
