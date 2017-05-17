package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.script.SortBuilderFn
import com.sksamuel.elastic4s.searches.HighlightOptionsDefinition
import com.sksamuel.elastic4s.searches.highlighting.HighlightBuilderFn
import com.sksamuel.elastic4s.searches.sort.SortDefinition
import org.elasticsearch.index.query.InnerHitBuilder
import org.elasticsearch.search.fetch.subphase.FetchSourceContext
import org.elasticsearch.search.sort.SortBuilder

import scala.collection.JavaConverters._

object InnerHitBuilder {

  def sort(sortdef: SortDefinition): SortBuilder[_ <: SortBuilder[_]] = SortBuilderFn(sortdef)

  def apply(d: InnerHitDefinition): InnerHitBuilder = {
    val builder = new InnerHitBuilder().setName(d.name)
    d.from.foreach(builder.setFrom)
    d.explain.foreach(builder.setExplain)

    d.fetchSource.foreach { fetch =>
      val context = if (fetch.fetchSource) {
        val inc = if (fetch.includes.isEmpty) null else fetch.includes
        val exc = if (fetch.excludes.isEmpty) null else fetch.excludes
        new FetchSourceContext(true, inc, exc)
      } else {
        new FetchSourceContext(false)
      }
      builder.setFetchSourceContext(context)
    }

    d.trackScores.foreach(builder.setTrackScores)
    d.version.foreach(builder.setVersion)
    d.size.foreach(builder.setSize)
    d.docValueFields.foreach(builder.addDocValueField)
    d.sorts.map(sort).foreach(builder.addSort)
    if (d.storedFieldNames.nonEmpty)
      builder.setStoredFieldNames(d.storedFieldNames.asJava)
    if (d.highlights.nonEmpty) {
      val h = HighlightBuilderFn(HighlightOptionsDefinition(), d.highlights)
      builder.setHighlightBuilder(h)
    }
    builder
  }
}
