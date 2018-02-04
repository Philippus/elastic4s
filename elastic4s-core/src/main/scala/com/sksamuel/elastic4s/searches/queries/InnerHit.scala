package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.FetchSourceContext
import com.sksamuel.elastic4s.searches.HighlightField
import com.sksamuel.elastic4s.searches.sort.Sort
import com.sksamuel.exts.OptionImplicits._

case class InnerHit(name: String,
                    size: Option[Int] = None,
                    fetchSource: Option[FetchSourceContext] = None,
                    version: Option[Boolean] = None,
                    trackScores: Option[Boolean] = None,
                    explain: Option[Boolean] = None,
                    storedFieldNames: Seq[String] = Nil,
                    docValueFields: Seq[String] = Nil,
                    sorts: Seq[Sort] = Nil,
                    from: Option[Int] = None,
                    highlights: Seq[HighlightField] = Nil) {

  def sortBy(sorts: Sort*): InnerHit          = sortBy(sorts)
  def sortBy(sorts: Iterable[Sort]): InnerHit = copy(sorts = sorts.toSeq)

  def highlighting(highlights: HighlightField*): InnerHit = highlighting(highlights)
  def highlighting(highlights: Iterable[HighlightField]): InnerHit =
    copy(highlights = highlights.toSeq)

  def trackScores(trackScores: Boolean): InnerHit            = copy(trackScores = trackScores.some)
  def version(version: Boolean): InnerHit                    = copy(version = version.some)
  def from(from: Int): InnerHit                              = copy(from = from.some)
  def fetchSource(fetchSource: FetchSourceContext): InnerHit = copy(fetchSource = fetchSource.some)
  def size(size: Int): InnerHit                              = copy(size = size.some)

  def docValueFields(docValueFields: Iterable[String]): InnerHit =
    copy(docValueFields = docValueFields.toSeq)

  def storedFieldNames(storedFieldNames: Iterable[String]): InnerHit =
    copy(storedFieldNames = storedFieldNames.toSeq)

  def explain(explain: Boolean): InnerHit = copy(explain = explain.some)
}
