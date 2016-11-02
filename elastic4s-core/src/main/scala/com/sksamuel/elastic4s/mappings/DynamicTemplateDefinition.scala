package com.sksamuel.elastic4s.mappings

import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

trait DynamicTemplateDsl {

  def dynamicTemplate(name: String) = new {
    def mapping(mapping: TypedFieldDefinition) = DynamicTemplateDefinition(name, mapping)
  }

  def dynamicTemplate(name: String, mapping: TypedFieldDefinition): DynamicTemplateDefinition = {
    DynamicTemplateDefinition(name, mapping)
  }
}

case class DynamicTemplateDefinition(name: String,
                                     mapping: TypedFieldDefinition,
                                     _match: Option[String] = None,
                                     _unmatch: Option[String] = None,
                                     _path_match: Option[String] = None,
                                     _path_unmatch: Option[String] = None,
                                     _match_pattern: Option[String] = None,
                                     _match_mapping_type: Option[String] = None) {

  def build: XContentBuilder = build(XContentFactory.jsonBuilder())
  def build(builder: XContentBuilder): XContentBuilder = {

    builder.startObject()
    builder.startObject(name)

    _match.foreach(builder.field("match", _))
    _unmatch.foreach(builder.field("unmatch", _))
    _path_match.foreach(builder.field("path_match", _))
    _path_unmatch.foreach(builder.field("path_unmatch", _))
    _match_pattern.foreach(builder.field("match_pattern", _))
    _match_mapping_type.foreach(builder.field("match_mapping_type", _))

    builder.startObject("mapping")
    mapping.build(builder, false)
    builder.endObject()

    builder.endObject()
    builder.endObject()

    builder
  }

  def `match`(m: String) = matching(m)
  def matching(m: String) = copy(_match = Option(m))
  def matchPattern(m: String) = copy(_match_pattern = Option(m))

  def unmatch(m: String) = copy(_unmatch = Option(m))
  def pathMatch(path: String) = copy(_path_match = Option(path))
  def pathUnmatch(path: String) = copy(_path_unmatch = Option(path))

  def matchMappingType(`type`: String) = copy(_match_mapping_type = Option(`type`))
}
