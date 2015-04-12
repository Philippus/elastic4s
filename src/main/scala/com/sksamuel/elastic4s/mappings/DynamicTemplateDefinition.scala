package com.sksamuel.elastic4s.mappings

import org.elasticsearch.common.xcontent.{XContentFactory, XContentBuilder}

class DynamicTemplateDefinition(name: String) {

  var _match: Option[String] = None
  var _unmatch: Option[String] = None
  var _path_match: Option[String] = None
  var _path_unmatch: Option[String] = None
  var _matchMappingType: Option[String] = None
  var _field: Option[TypedFieldDefinition] = None

  def build: XContentBuilder = build(XContentFactory.jsonBuilder())
  def build(builder: XContentBuilder): XContentBuilder = {
    builder.startObject()
    builder.startObject(name)
    _match.foreach(builder.field("match", _))
    _unmatch.foreach(builder.field("unmatch", _))
    _path_match.foreach(builder.field("path_match", _))
    _path_unmatch.foreach(builder.field("path_unmatch", _))
    _matchMappingType.foreach(builder.field("match_mapping_type", _))
    builder.startObject("mapping")
    _field.foreach(_.build(builder, false))
    builder.endObject()
    builder.endObject()
    builder.endObject()
    builder
  }

  def `match`(m: String): this.type = matching(m)
  def matching(m: String): this.type = {
    _match = Option(m)
    this
  }

  def unmatch(m: String): this.type = {
    _unmatch = Option(m)
    this
  }

  def pathMatch(path: String): this.type = {
    _path_match = Option(path)
    this
  }

  def pathUnmatch(path: String): this.type = {
    _path_unmatch = Option(path)
    this
  }

  def matchMappingType(`type`: String): this.type = {
    _matchMappingType = Option(`type`)
    this
  }

  def mapping(field: TypedFieldDefinition): this.type = {
    _field = Option(field)
    this
  }
}
