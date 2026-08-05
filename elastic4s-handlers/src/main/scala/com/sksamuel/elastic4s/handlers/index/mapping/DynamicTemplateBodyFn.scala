package com.sksamuel.elastic4s.handlers.index.mapping

import com.sksamuel.elastic4s.handlers.fields.ElasticFieldBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.mappings.dynamictemplate.DynamicTemplateRequest

object DynamicTemplateBodyFn {

  def build(dyn: DynamicTemplateRequest): XContentBuilder = {

    val builder = XContentFactory.obj()
    builder.startObject(dyn.name)

    dyn.`match` match {
      case Nil      =>
      case Seq(str) => builder.field("match", str)
      case s        => builder.array("match", s.toArray)
    }
    dyn.unmatch match {
      case Nil      =>
      case Seq(str) => builder.field("unmatch", str)
      case s        => builder.array("unmatch", s.toArray)
    }
    dyn.pathMatch match {
      case Nil      =>
      case Seq(str) => builder.field("path_match", str)
      case s        => builder.array("path_match", s.toArray)
    }
    dyn.pathUnmatch match {
      case Nil      =>
      case Seq(str) => builder.field("path_unmatch", str)
      case s        => builder.array("path_unmatch", s.toArray)
    }
    dyn.matchMappingType match {
      case Nil      =>
      case Seq(str) => builder.field("match_mapping_type", str)
      case s        => builder.array("match_mapping_type", s.toArray)
    }
    dyn.unmatchMappingType match {
      case Nil      =>
      case Seq(str) => builder.field("unmatch_mapping_type", str)
      case s        => builder.array("unmatch_mapping_type", s.toArray)
    }
    dyn.matchPattern.foreach(builder.field("match_pattern", _))

    builder.rawField("mapping", ElasticFieldBuilderFn(dyn.mapping))

    builder.endObject()
    builder.endObject()
  }
}
