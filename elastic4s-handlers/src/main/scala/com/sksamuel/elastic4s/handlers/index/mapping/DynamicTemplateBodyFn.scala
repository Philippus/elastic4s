package com.sksamuel.elastic4s.handlers.index.mapping

import com.sksamuel.elastic4s.handlers.fields.ElasticFieldBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.mappings.dynamictemplate.DynamicTemplateRequest

object DynamicTemplateBodyFn {

  def build(dyn: DynamicTemplateRequest): XContentBuilder = {

    val builder = XContentFactory.obj()
    builder.startObject(dyn.name)

    dyn.`match`.foreach(builder.field("match", _))
    dyn.unmatch.foreach(builder.field("unmatch", _))
    dyn.pathMatch.foreach(builder.field("path_match", _))
    dyn.pathUnmatch.foreach(builder.field("path_unmatch", _))
    dyn.MatchPattern.foreach(builder.field("match_pattern", _))
    dyn.matchMappingType.foreach(builder.field("match_mapping_type", _))

    builder.rawField("mapping", ElasticFieldBuilderFn(dyn.mapping))

    builder.endObject()
  }
}
