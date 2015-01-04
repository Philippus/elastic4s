package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.DefinitionAttributes.{DefinitionAttributeIndexesOptions, DefinitionAttributeIgnoreConflicts}
import com.sksamuel.elastic4s.mappings.TypedFieldDefinition
import org.elasticsearch.action.admin.indices.mapping.put.{PutMappingRequestBuilder, PutMappingRequest}
import org.elasticsearch.common.xcontent.XContentFactory

/** @author Stephen Samuel */
class PutMappingDefinition(indexType: IndexType)
  extends DefinitionAttributeIgnoreConflicts
  with DefinitionAttributeIndexesOptions {

  def build: PutMappingRequest = _builder.request

  val _builder = new PutMappingRequestBuilder(ProxyClients.indices)
    .setIndices(indexType.index)
    .setType(indexType.`type`)

  @deprecated("use as instead of add", "1.4.4")
  def add(fields: TypedFieldDefinition*): this.type = as(fields)

  @deprecated("use as instead of add", "1.4.4")
  def add(fields: Iterable[TypedFieldDefinition]): this.type = as(fields)

  def as(fields: TypedFieldDefinition*): this.type = add(fields)
  def as(fields: Iterable[TypedFieldDefinition]): this.type = {
    val xcontent = XContentFactory.jsonBuilder().startObject()
    xcontent.startObject("properties")
    for ( field <- fields ) {
      field.build(xcontent)
    }
    xcontent.endObject()
    _builder.setSource(xcontent)
    this
  }
}
