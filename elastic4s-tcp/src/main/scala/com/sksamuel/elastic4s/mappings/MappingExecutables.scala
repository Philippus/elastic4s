package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.Executable
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait MappingExecutables {

  implicit object GetMappingDefinitionExecutable
    extends Executable[GetMappingDefinition, GetMappingsResponse, GetMappingsResult] {
    override def apply(c: Client, t: GetMappingDefinition): Future[GetMappingsResult] = {
      val req = c.admin.indices.prepareGetMappings(t.indexesAndTypes.indexes: _*)
      req.setTypes(t.indexesAndTypes.types: _*)
      t.local.foreach(req.setLocal)
      injectFutureAndMap(req.execute)(GetMappingsResult.apply)
    }
  }

  implicit object PutMappingDefinitionExecutable
    extends Executable[PutMappingDefinition, PutMappingResponse, PutMappingResponse] {
    override def apply(c: Client, t: PutMappingDefinition): Future[PutMappingResponse] = {
      val listener = c.admin().indices().preparePutMapping(t.indexesAndType.indexes: _*)
        .setType(t.indexesAndType.`type`)
        .setSource(MappingContentBuilder.build(t))
        .execute()
      injectFuture(listener)
    }
  }
}
