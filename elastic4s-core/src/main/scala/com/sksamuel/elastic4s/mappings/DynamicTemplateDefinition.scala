package com.sksamuel.elastic4s.mappings

import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

case class DynamicTemplateDefinition(name: String,
                                     mapping: FieldDefinition, // definition of the field, elasticsearch calls this the mapping
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

    builder.rawField("mapping", FieldBuilderFn(mapping).bytes)

    builder.endObject()
    builder.endObject()

    builder
  }

  def `match`(m: String): DynamicTemplateDefinition = matching(m)
  def matching(m: String): DynamicTemplateDefinition = copy(_match = Option(m))
  def matchPattern(m: String): DynamicTemplateDefinition = copy(_match_pattern = Option(m))

  def unmatch(m: String): DynamicTemplateDefinition = copy(_unmatch = Option(m))
  def pathMatch(path: String): DynamicTemplateDefinition = copy(_path_match = Option(path))
  def pathUnmatch(path: String): DynamicTemplateDefinition = copy(_path_unmatch = Option(path))

  def matchMappingType(`type`: String): DynamicTemplateDefinition = copy(_match_mapping_type = Option(`type`))
}
