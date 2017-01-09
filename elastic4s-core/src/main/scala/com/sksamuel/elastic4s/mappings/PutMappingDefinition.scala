package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.{IndexesAndType, ProxyClients}
import org.elasticsearch.action.admin.indices.mapping.put.{PutMappingAction, PutMappingRequest, PutMappingRequestBuilder}

class PutMappingDefinition(indexesAndType: IndexesAndType) extends MappingDefinition(indexesAndType.`type`) {

  def request: PutMappingRequest = {
    val req = new PutMappingRequestBuilder(ProxyClients.indices, PutMappingAction.INSTANCE)
      .setIndices(indexesAndType.indexes: _*)
      .setType(indexesAndType.`type`)
      .setSource(super.build)
    req.request()
  }
}
