package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.DefinitionAttributes.{DefinitionAttributeIndexesOptions, DefinitionAttributeIgnoreConflicts}
import com.sksamuel.elastic4s.mappings.TypedFieldDefinition
import org.elasticsearch.action.admin.indices.mapping.put.{PutMappingRequestBuilder, PutMappingRequest}
import org.elasticsearch.common.xcontent.XContentFactory

/** @author Stephen Samuel */
trait PutMappingDsl {
  def put = new PutExpectsMapping
  class PutExpectsMapping {
    def mapping(indexes: IndexesTypes) = new PutMappingDefinition(indexes)
  }
}

class PutMappingDefinition(indexes: IndexesTypes)
  extends DefinitionAttributeIgnoreConflicts
  with DefinitionAttributeIndexesOptions {

  def build: PutMappingRequest = _builder.request

  val _builder = new PutMappingRequestBuilder(null)
    .setIndices(indexes.index)
    .setType(indexes.typ.getOrElse("Must specify type for put mapping"))

  def fields(fields: TypedFieldDefinition*): this.type = {
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