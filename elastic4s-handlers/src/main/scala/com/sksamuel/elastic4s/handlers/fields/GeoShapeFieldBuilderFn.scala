package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.GeoShapeField
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object GeoShapeFieldBuilderFn {
  def build(field: GeoShapeField): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("type", field.`type`)
    field.boost.foreach(builder.field("boost", _))
    if (field.copyTo.nonEmpty) builder.array("copy_to", field.copyTo.toArray)
    field.docValues.foreach(builder.field("doc_values", _))
    field.ignoreMalformed.foreach(builder.field("ignore_malformed", _))
    field.ignoreZValue.foreach(builder.field("ignore_z_value", _))
    field.index.foreach(builder.field("index", _))
    field.norms.foreach(builder.field("norms", _))
    field.nullValue.foreach(builder.field("null_value", _))
    field.tree.foreach(builder.field("tree", _))
    field.precision.foreach(builder.field("precision", _))
    field.treeLevels.foreach(builder.field("tree_levels", _))
    field.strategy.foreach(builder.field("strategy", _))
    field.distanceErrorPct.foreach(builder.field("distance_error_pct", _))
    field.orientation.foreach(builder.field("orientation", _))
    field.pointsOnly.foreach(builder.field("points_only", _))
    field.store.foreach(builder.field("store", _))

    builder.endObject()
  }
}
