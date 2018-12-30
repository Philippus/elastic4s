package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.requests.mappings.dynamictemplate.{DynamicMapping, DynamicTemplateBodyFn}
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object MappingBuilderFn {

  def build(d: MappingDefinitionLike): XContentBuilder =
    d.rawSource match {
      //user raw source if provided, ignore other mapping settings
      case Some(rs) => XContentFactory.parse(rs)
      case None =>
        val builder = XContentFactory.jsonBuilder()
        build(d, builder)
        builder.endObject()
    }

  // returns the mapping json wrapped in the mapping type name, eg "mytype" : { mapping }
  def buildWithName(d: MappingDefinitionLike, tpe: String): XContentBuilder =
    d.rawSource match {
      //user raw source if provided, ignore other mapping settings
      case Some(rs) =>
        val builder = XContentFactory.jsonBuilder
        builder.rawField(tpe, XContentFactory.parse(rs))
        builder
      case None =>
        val builder = XContentFactory.jsonBuilder
        builder.startObject(tpe)
        build(d, builder)
        builder.endObject()
        builder.endObject()
    }

  def build(d: MappingDefinitionLike, builder: XContentBuilder): Unit = {

    for (all <- d.all) builder.startObject("_all").field("enabled", all).endObject()
    (d.source, d.sourceExcludes) match {
      case (_, l) if l.nonEmpty => builder.startObject("_source").array("excludes", l.toArray).endObject()
      case (Some(source), _)    => builder.startObject("_source").field("enabled", source).endObject()
      case _                    =>
    }

    if (d.dynamicDateFormats.nonEmpty)
      builder.array("dynamic_date_formats", d.dynamicDateFormats.toArray)

    for (dd <- d.dateDetection) builder.field("date_detection", dd)
    for (nd <- d.numericDetection) builder.field("numeric_detection", nd)

    d.dynamic.foreach(
      dynamic =>
        builder.field("dynamic", dynamic match {
          case DynamicMapping.Strict => "strict"
          case DynamicMapping.False  => "false"
          case _                     => "dynamic"
        })
    )

    d.boostName.foreach(
      x =>
        builder.startObject("_boost").field("name", x).field("null_value", d.boostNullValue.getOrElse(0D)).endObject()
    )
    d.analyzer.foreach(x => builder.startObject("_analyzer").field("path", x).endObject())
    d.parent.foreach(x => builder.startObject("_parent").field("type", x).endObject())
    d.size.foreach(x => builder.startObject("_size").field("enabled", x).endObject())

    if (d.fields.nonEmpty) {
      builder.startObject("properties")
      for (field <- d.fields)
        builder.rawField(field.name, FieldBuilderFn(field))
      builder.endObject() // end properties
    }

    if (d.meta.nonEmpty) {
      builder.startObject("_meta")
      for (meta <- d.meta)
        meta match {
          case (name, s: String)  => builder.field(name, s)
          case (name, s: Double)  => builder.field(name, s)
          case (name, s: Boolean) => builder.field(name, s)
          case (name, s: Long)    => builder.field(name, s)
          case (name, s: Float)   => builder.field(name, s)
          case (name, s: Int)     => builder.field(name, s)
        }
      builder.endObject()
    }

    d.routing.foreach(routing => {
      builder.startObject("_routing").field("required", routing.required)
      routing.path.foreach(path => builder.field("path", path))
      builder.endObject()
    })

    if (d.templates.nonEmpty) {
      builder.startArray("dynamic_templates")
      d.templates.foreach { template =>
        builder.rawValue(DynamicTemplateBodyFn.build(template))
      }
      builder.endArray()
    }
  }
}
