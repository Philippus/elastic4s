package com.sksamuel.elastic4s.searches.highlighting

import com.sksamuel.elastic4s.searches.{HighlightFieldDefinition, QueryBuilderFn}
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder
import scala.collection.JavaConversions._

object HighlightFieldBuilder {
  def apply(highlight: HighlightFieldDefinition): HighlightBuilder.Field = {
    val builder = new HighlightBuilder.Field(highlight.field)
    highlight.boundaryChars.foreach(builder.boundaryChars)
    highlight.boundaryMaxScan.map(Integer.valueOf).foreach(builder.boundaryMaxScan)
    highlight.forceSource.map(java.lang.Boolean.valueOf).foreach(builder.forceSource)
    highlight.fragmenter.foreach(builder.fragmenter)
    highlight.fragmentOffset.foreach(builder.fragmentOffset)
    highlight.fragmentSize.map(Integer.valueOf).foreach(builder.fragmentSize)
    highlight.highlighterType.foreach(builder.highlighterType)
    highlight.highlightFilter.map(java.lang.Boolean.valueOf).foreach(builder.highlightFilter)
    highlight.highlightQuery.map(QueryBuilderFn.apply).foreach(builder.highlightQuery)
    highlight.order.foreach(builder.order)
    highlight.noMatchSize.map(Integer.valueOf).foreach(builder.noMatchSize)
    highlight.numOfFragments.map(Integer.valueOf).foreach(builder.numOfFragments)
    highlight.options.foreach(options => builder.options(options))

    if (highlight.postTags.nonEmpty)
      builder.postTags(highlight.postTags: _*)
    if (highlight.preTags.nonEmpty)
      builder.preTags(highlight.preTags: _*)
    highlight.requireFieldMatch.map(java.lang.Boolean.valueOf).foreach(builder.requireFieldMatch)
    if (highlight.matchedFields.nonEmpty)
      builder.matchedFields(highlight.matchedFields: _*)
    highlight.phraseLimit.map(Integer.valueOf).foreach(builder.phraseLimit)
    builder
  }
}
