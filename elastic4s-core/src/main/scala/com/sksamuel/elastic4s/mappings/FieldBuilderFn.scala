package com.sksamuel.elastic4s.mappings

import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}
import scala.collection.JavaConverters._

object CommonFieldBuilder {
  def apply(field: FieldDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.field("type", field.`type`)

    field.analyzer.foreach(builder.field("analyzer", _))
    field.boost.foreach(builder.field("boost", _))
    field.coerce.foreach(builder.field("coerce", _))

    if (field.copyTo.nonEmpty)
      builder.field("copy_to", field.copyTo.asJavaCollection)

    if (field.fields.nonEmpty) {
      builder.startObject("fields")
      field.fields.foreach { subfield =>
        builder.rawField(subfield.name, FieldBuilderFn(subfield).bytes)
      }
      builder.endObject()
    }

    field.docValues.foreach(builder.field("doc_values", _))
    field.enabled.foreach(builder.field("enabled", _))
    field.fielddata.foreach(builder.field("fielddata", _))
    field.format.foreach(builder.field("format", _))
    field.ignoreMalformed.foreach(builder.field("ignore_malformed", _))
    field.includeInAll.foreach(builder.field("include_in_all", _))
    field.index.foreach(builder.field("index", _))
    field.normalizer.foreach(builder.field("normalizer", _))
    field.norms.foreach(builder.field("norms", _))
    field.nullValue.foreach(builder.field("null_value", _))
    field.searchAnalyzer.foreach(builder.field("search_analyzer", _))
    field.similarity.foreach(builder.field("similarity", _))
    field.store.foreach(builder.field("store", _))
    field.termVector.foreach(builder.field("term_vector", _))

    builder
  }
}

object FieldBuilderFn {

  def apply(field: FieldDefinition): XContentBuilder = {
    val builder = CommonFieldBuilder(field)
    field match {
      case basic: BasicFieldDefinition =>
        basic.positionIncrementGap.foreach(builder.field("position_increment_gap", _))
        basic.preservePositionIncrements.foreach(builder.field("preserve_position_increments", _))
        basic.preserveSeparators.foreach(builder.field("preserve_separators", _))
        basic.ignoreAbove.foreach(builder.field("ignore_above", _))
        basic.indexOptions.foreach(builder.field("index_options", _))
        basic.maxInputLength.foreach(builder.field("max_input_length", _))
        basic.scalingFactor.foreach(builder.field("scaling_factor", _))
      case geo: GeoshapeFieldDefinition =>
        geo.tree.foreach(builder.field("tree", _))
        geo.precision.foreach(builder.field("precision", _))
        geo.treeLevels.foreach(builder.field("tree_levels", _))
        geo.strategy.foreach(builder.field("strategy", _))
        geo.distanceErrorPct.foreach(builder.field("distance_error_pct", _))
        geo.orientation.foreach(builder.field("orientation", _))
        geo.pointsOnly.foreach(builder.field("points_only", _))
    }
    builder.endObject()
    builder
  }
}
