package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.script.SortBuilderFn
import com.sksamuel.elastic4s.searches.{HighlightFieldDefinition, HighlightOptionsDefinition}
import com.sksamuel.elastic4s.searches.highlighting.HighlightBuilderFn
import com.sksamuel.elastic4s.searches.sort.SortDefinition
import org.elasticsearch.index.query.InnerHitBuilder
import org.elasticsearch.search.fetch.subphase.FetchSourceContext
import com.sksamuel.exts.OptionImplicits._

import scala.collection.JavaConverters._

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

  def builder = {
    val builder = new InnerHitBuilder().setName(name)
    from.foreach(builder.setFrom)
    explain.foreach(builder.setExplain)
    fetchSource.foreach(builder.setFetchSourceContext)
    trackScores.foreach(builder.setTrackScores)
    version.foreach(builder.setVersion)
    size.foreach(builder.setSize)
    docValueFields.foreach(builder.addDocValueField)
    sorts.map(SortBuilderFn.apply).foreach(builder.addSort)
    if (storedFieldNames.nonEmpty)
      builder.setStoredFieldNames(storedFieldNames.asJava)
    if (highlights.nonEmpty) {
      val h = HighlightBuilderFn(HighlightOptionsDefinition(), highlights)
      builder.setHighlightBuilder(h)
    }
    builder
  }

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
