package com.sksamuel.elastic4s.mappings.dynamictemplate

import com.sksamuel.elastic4s.mappings.FieldBuilderFn
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object DynamicTemplateBodyFn {

  def build(dyn: DynamicTemplateDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder
    build(dyn, builder)
    builder
  }

  def build(dyn: DynamicTemplateDefinition, builder: XContentBuilder): Unit = {

    builder.startObject().startObject(dyn.name)

    dyn.`match`.foreach(builder.field("match", _))
    dyn.unmatch.foreach(builder.field("unmatch", _))
    dyn.pathMatch.foreach(builder.field("path_match", _))
    dyn.pathUnmatch.foreach(builder.field("path_unmatch", _))
    dyn.MatchPattern.foreach(builder.field("match_pattern", _))
    dyn.matchMappingType.foreach(builder.field("match_mapping_type", _))

    builder.rawField("mapping", FieldBuilderFn(dyn.mapping).bytes)

    builder.endObject().endObject()
  }
}
