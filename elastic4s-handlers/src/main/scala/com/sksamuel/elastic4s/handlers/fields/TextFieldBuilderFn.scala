package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.{IndexPrefixes, TextField}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.mappings.FielddataFrequencyFilter

object TextFieldBuilderFn {
  private def getFieldDataFrequencyFilter(values: Map[String, Any]) = FielddataFrequencyFilter(
    values("min").asInstanceOf,
    values("max").asInstanceOf,
    values("min_segment_size").asInstanceOf
  )

  private def getIndexPrefixes(values: Map[String, Any]) = IndexPrefixes(values("min_chars").asInstanceOf, values("max_chars").asInstanceOf)

  def toField(name: String, values: Map[String, Any]): TextField = TextField(
    name,
    values.get("analyzer").map(_.asInstanceOf[String]),
    values.get("boost").map(_.asInstanceOf[Double]),
    values.get("copy_to").map(_.asInstanceOf[Seq[String]]).getOrElse(Seq.empty),
    values.get("eager_global_ordinals").map(_.asInstanceOf[Boolean]),
    values
      .get("fields")
      .map(_.asInstanceOf[Map[String, Map[String, Any]]].map { case (key, value) =>
        ElasticFieldBuilderFn.construct(key, value)
      }.toList)
      .getOrElse(List.empty),
    values.get("fielddata").map(_.asInstanceOf[Boolean]),
    values.get("fielddata_frequency_filter").map(_.asInstanceOf[Map[String, Any]]).map(getFieldDataFrequencyFilter),
    values.get("index").map(_.asInstanceOf[Boolean]),
    values.get("index_prefixes").map(_.asInstanceOf[Map[String, Any]]).map(getIndexPrefixes),
    values.get("index_phrases").map(_.asInstanceOf[Boolean]),
    values.get("index_options").map(_.asInstanceOf[String]),
    values.get("norms").map(_.asInstanceOf[Boolean]),
    values.get("position_increment_gap").map(_.asInstanceOf[Int]),
    values.get("search_analyzer").map(_.asInstanceOf[String]),
    values.get("search_quote_analyzer").map(_.asInstanceOf[String]),
    values.get("similarity").map(_.asInstanceOf[String]),
    values.get("store").map(_.asInstanceOf[Boolean]),
    values.get("term_vector").map(_.asInstanceOf[String])
  )


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
    field.eagerGlobalOrdinals.foreach(builder.field("eager_global_ordinals", _))
    field.indexOptions.foreach(builder.field("index_options", _))
    field.searchAnalyzer.foreach(builder.field("search_analyzer", _))
    field.searchQuoteAnalyzer.foreach(builder.field("search_quote_analyzer", _))
    field.similarity.foreach(builder.field("similarity", _))
    field.termVector.foreach(builder.field("term_vector", _))

    builder.endObject()
  }
}
