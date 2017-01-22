package com.sksamuel.elastic4s.searches.highlighting

import com.sksamuel.elastic4s.searches.{HighlightFieldDefinition, HighlightOptionsDefinition, QueryBuilderFn}
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder

object HighlightBuilderFn {
  def apply(options: HighlightOptionsDefinition, fields: Seq[HighlightFieldDefinition]): HighlightBuilder = {
    val builder = new HighlightBuilder()
    options.encoder.foreach(builder.encoder)
    options.tagsSchema.foreach(builder.tagsSchema)
    options.forceSource.foreach(bool => builder.forceSource(bool))
    options.useExplicitFieldOrder.foreach(builder.useExplicitFieldOrder)
    options.boundaryChars.map(_.toCharArray).foreach(builder.boundaryChars)
    options.boundaryMaxScan.foreach(int => builder.boundaryMaxScan(int))
    options.fragmenter.foreach(builder.fragmenter)
    options.fragmentSize.foreach(int => builder.fragmentSize(int))
    options.highlighterType.foreach(builder.highlighterType)
    options.highlightFilter.foreach(bool => builder.highlightFilter(bool))
    options.highlightQuery.map(QueryBuilderFn.apply).foreach(builder.highlightQuery)
    options.noMatchSize.foreach(int => builder.noMatchSize(int))
    options.numOfFragments.foreach(int => builder.numOfFragments(int))
    options.order.foreach(builder.order)
    options.phraseLimit.foreach(int => builder.phraseLimit(int))
    if (options.postTags.nonEmpty)
      builder.postTags(options.postTags: _*)
    if (options.preTags.nonEmpty)
      builder.preTags(options.preTags: _*)
    options.requireFieldMatch.foreach(bool => builder.requireFieldMatch(bool))

    fields.map { field =>
      builder.field(HighlightFieldBuilder(field))
    }

    builder
  }
}
