package com.sksamuel.elastic4s.fields.builders

import com.sksamuel.elastic4s.fields.TextField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object TextFieldBuilderFn {

  def build(field: TextField): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)

    field.analyzer.foreach(builder.field("analyzer", _))
    field.boost.foreach(builder.field("boost", _))

    if (field.copyTo.nonEmpty)
      builder.array("copy_to", field.copyTo.toArray)

    field.index.foreach(builder.field("index", _))

    if (field.fields.nonEmpty) {
      builder.startObject("fields")
      field.fields.foreach { field =>
        builder.rawField(field.name, ElasticFieldBuilderFn(field))
      }
      builder.endObject()
    }

    field.norms.foreach(builder.field("norms", _))
    field.store.foreach(builder.field("store", _))
    field.indexPrefixes.foreach { prefix =>
      builder.startObject("index_prefixes")
      builder.field("min_chars", prefix.minChars)
      builder.field("min_chars", prefix.maxChars)
      builder.endObject()
    }
    field.indexPhrases.foreach(builder.field("index_phrases", _))
    field.fielddata.foreach(builder.field("fielddata", _))

    field.fielddataFrequencyFilter.foreach { filter =>
      builder.startObject("fielddata_frequency_filter")
      builder.field("min", filter.min)
      builder.field("max", filter.max)
      builder.field("min_segment_size", filter.minSegmentSize)
      builder.endObject()
    }

    field.positionIncrementGap.foreach(builder.field("position_increment_gap", _))
    field.fielddata.foreach(builder.field("fielddata", _))
    field.eagerGlobalOrdinals.foreach(builder.field("eager_global_ordinals", _))
    field.ignoreAbove.foreach(builder.field("ignore_above", _))
    field.indexOptions.foreach(builder.field("index_options", _))
    field.searchAnalyzer.foreach(builder.field("search_analyzer", _))
    field.searchQuoteAnalyzer.foreach(builder.field("search_quote_analyzer", _))
    field.similarity.foreach(builder.field("similarity", _))
    field.termVector.foreach(builder.field("term_vector", _))

    builder.endObject()
  }
}
