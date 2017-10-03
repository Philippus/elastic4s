package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object CommonFieldBuilder {

  def apply(field: FieldDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)

    field.analyzer.foreach(builder.field("analyzer", _))
    field.boost.foreach(builder.field("boost", _))

    if (field.copyTo.nonEmpty)
      builder.array("copy_to", field.copyTo.toArray)

    if (field.fields.nonEmpty) {
      field match {
        case _: NestedFieldDefinition => builder.startObject("properties")
        case _: ObjectFieldDefinition => builder.startObject("properties")
        case _ => builder.startObject("fields")
      }
      field.fields.foreach { subfield =>
        builder.rawField(subfield.name, FieldBuilderFn(subfield))
      }
      builder.endObject()
    }

    field.docValues.foreach(builder.field("doc_values", _))
    field.enabled.foreach(builder.field("enabled", _))
    field.index.foreach(builder.field("index", _))
    field.normalizer.foreach(builder.field("normalizer", _))
    field.norms.foreach(builder.field("norms", _))
    field.nullValue.foreach {
      case v: String => builder.field("null_value", v)
      case v: Boolean => builder.field("null_value", v)
      case v: Float => builder.field("null_value", v)
      case v: Long => builder.field("null_value", v)
      case v: Double => builder.field("null_value", v)
      case v: Int => builder.field("null_value", v)
    }
    field.searchAnalyzer.foreach(builder.field("search_analyzer", _))
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
        basic.ignoreAbove.foreach(builder.field("ignore_above", _))
        basic.ignoreMalformed.foreach(builder.field("ignore_malformed", _))
        basic.indexOptions.foreach(builder.field("index_options", _))
        basic.scalingFactor.foreach(builder.field("scaling_factor", _))
        basic.coerce.foreach(builder.field("coerce", _))
        basic.format.foreach(builder.field("format", _))
        basic.similarity.foreach(builder.field("similarity", _))

      case comp: CompletionFieldDefinition =>
        comp.preservePositionIncrements.foreach(builder.field("preserve_position_increments", _))
        comp.preserveSeparators.foreach(builder.field("preserve_separators", _))
        comp.ignores.ignoreAbove.foreach(builder.field("ignore_above", _))
        comp.ignores.ignoreMalformed.foreach(builder.field("ignore_malformed", _))
        comp.indexOptions.foreach(builder.field("index_options", _))
        comp.maxInputLength.foreach(builder.field("max_input_length", _))
        comp.coerce.foreach(builder.field("coerce", _))
        if(comp.contexts.nonEmpty) {
          builder.startArray("contexts")
          comp.contexts.foreach { context =>
            builder.startObject()
            builder.field("name", context.name)
            builder.field("type", context.`type`)
            context.path.foreach(builder.field("path", _))
            if (context.`type` == "geo") context.precision.foreach(builder.field("precision", _))
            builder.endObject()
          }
          builder.endArray()
        }

      case geo: GeoshapeFieldDefinition =>
        geo.geoFields.tree.foreach(builder.field("tree", _))
        geo.geoFields.precision.foreach(builder.field("precision", _))
        geo.geoFields.treeLevels.foreach(builder.field("tree_levels", _))
        geo.geoFields.strategy.foreach(builder.field("strategy", _))
        geo.geoFields.distanceErrorPct.foreach(builder.field("distance_error_pct", _))
        geo.geoFields.orientation.foreach(builder.field("orientation", _))
        geo.geoFields.pointsOnly.foreach(builder.field("points_only", _))
        geo.coerce.foreach(builder.field("coerce", _))
        geo.format.foreach(builder.field("format", _))
        geo.ignoreMalformed.foreach(builder.field("ignore_malformed", _))

      case join: JoinFieldDefinition =>
        builder.startObject("relations")
        join.relations.foreach { case (parent, child) =>
          builder.field(parent, child)
        }
        builder.endObject()

      case obj: ObjectFieldDefinition =>
        obj.dynamic.foreach(builder.field("dynamic", _))

      case nested: NestedFieldDefinition =>
        nested.dynamic.foreach(builder.field("dynamic", _))

      case text: TextFieldDefinition =>
        text.eagerGlobalOrdinals.foreach(builder.field("eager_global_ordinals", _))
        text.positionIncrementGap.foreach(builder.field("position_increment_gap", _))
        text.fielddata.foreach(builder.field("fielddata", _))
        text.maxInputLength.foreach(builder.field("max_input_length", _))
        text.ignoreAbove.foreach(builder.field("ignore_above", _))
        text.similarity.foreach(builder.field("similarity", _))

      case keyword: KeywordFieldDefinition =>
        keyword.eagerGlobalOrdinals.foreach(builder.field("eager_global_ordinals", _))
        keyword.ignoreAbove.foreach(builder.field("ignore_above", _))
        keyword.similarity.foreach(builder.field("similarity", _))
        keyword.indexOptions.foreach(builder.field("index_options", _))

    }
    builder.endObject()
    builder
  }
}
