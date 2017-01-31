package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.HighlightFieldDefinition
import com.sksamuel.elastic4s.searches.sort.SortDefinition
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.search.fetch.subphase.FetchSourceContext

case class InnerHitDefinition(name: String,
                              size: Option[Int] = None,
                              fetchSource: Option[FetchSourceContext] = None,
                              version: Option[Boolean] = None,
                              trackScores: Option[Boolean] = None,
                              explain: Option[Boolean] = None,
                              storedFieldNames: Seq[String] = Nil,
                              docValueFields: Seq[String] = Nil,
                              sorts: Seq[SortDefinition] = Nil,
                              from: Option[Int] = None,
                              highlights: Seq[HighlightFieldDefinition] = Nil) {

  def sortBy(sorts: SortDefinition*): InnerHitDefinition = sortBy(sorts)
  def sortBy(sorts: Iterable[SortDefinition]): InnerHitDefinition = copy(sorts = sorts.toSeq)

  def highlighting(highlights: HighlightFieldDefinition*): InnerHitDefinition = highlighting(highlights)
  def highlighting(highlights: Iterable[HighlightFieldDefinition]): InnerHitDefinition =
    copy(highlights = highlights.toSeq)

  def trackScores(trackScores: Boolean): InnerHitDefinition = copy(trackScores = trackScores.some)
  def version(version: Boolean): InnerHitDefinition = copy(version = version.some)
  def from(from: Int): InnerHitDefinition = copy(from = from.some)
  def fetchSource(fetchSource: FetchSourceContext): InnerHitDefinition = copy(fetchSource = fetchSource.some)
  def size(size: Int): InnerHitDefinition = copy(size = size.some)

  def docValueFields(docValueFields: Iterable[String]): InnerHitDefinition =
    copy(docValueFields = docValueFields.toSeq)

  def storedFieldNames(storedFieldNames: Iterable[String]): InnerHitDefinition =
    copy(storedFieldNames = storedFieldNames.toSeq)

  def explain(explain: Boolean): InnerHitDefinition = copy(explain = explain.some)
}
