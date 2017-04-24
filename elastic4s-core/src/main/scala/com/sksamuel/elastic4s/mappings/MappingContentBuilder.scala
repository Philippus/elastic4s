package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.mappings.dynamictemplate.{DynamicMapping, DynamicTemplateBodyFn}
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

import scala.collection.JavaConverters._

object MappingContentBuilder {

  def build(d: MappingDefinitionLike): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject()
    build(d, builder)
    builder.endObject()
  }

  // returns the mapping json wrapped in the mapping type name, eg "mytype" : { mapping }
  def buildWithName(d: MappingDefinitionLike, tpe: String): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject()
    builder.startObject(tpe)
    build(d, builder)
    builder.endObject()
    builder.endObject()
  }

  def build(d: MappingDefinitionLike, builder: XContentBuilder): Unit = {

    for (all <- d.all) builder.startObject("_all").field("enabled", all).endObject()
    (d.source, d.sourceExcludes) match {
      case (_, l) if l.nonEmpty => builder.startObject("_source").field("excludes", l.asJava).endObject()
      case (Some(source), _) => builder.startObject("_source").field("enabled", source).endObject()
      case _ =>
    }

    if (d.dynamicDateFormats.nonEmpty)
      builder.field("dynamic_date_formats", d.dynamicDateFormats.asJava)

    for (dd <- d.dateDetection) builder.field("date_detection", dd)
    for (nd <- d.numericDetection) builder.field("numeric_detection", nd)

    d.dynamic.foreach(dynamic => {
      builder.field("dynamic", dynamic match {
        case DynamicMapping.Strict => "strict"
        case DynamicMapping.False => "false"
        case _ => "dynamic"
      })
    })

    d.boostName.foreach(x => builder.startObject("_boost").field("name", x).field("null_value", d.boostNullValue.getOrElse(0D)).endObject())
    d.analyzer.foreach(x => builder.startObject("_analyzer").field("path", x).endObject())
    d.parent.foreach(x => builder.startObject("_parent").field("type", x).endObject())
    d.size.foreach(x => builder.startObject("_size").field("enabled", x).endObject())

    d.timestamp.foreach(_.build(builder))

    if (d.fields.nonEmpty) {
      builder.startObject("properties")
      for (field <- d.fields) {
        builder.rawField(field.name, FieldBuilderFn(field).bytes)
      }
      builder.endObject() // end properties
    }

    if (d.meta.nonEmpty) {
      builder.startObject("_meta")
      for (meta <- d.meta) {
        builder.field(meta._1, meta._2)
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
      d.templates.foreach(DynamicTemplateBodyFn.build(_, builder))
      builder.endArray()
    }
  }
}
