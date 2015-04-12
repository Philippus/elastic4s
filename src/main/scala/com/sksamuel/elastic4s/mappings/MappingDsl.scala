package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.{ PutMappingDefinition, Executable }
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingResponse
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future
import scala.language.implicitConversions

/** @author Stephen Samuel */
trait MappingDsl {

  val NotAnalyzed: String = "not_analyzed"
  def id: FieldDefinition = "_id"

  implicit def field(name: String): FieldDefinition = new FieldDefinition(name)
  implicit def map(`type`: String): MappingDefinition = new MappingDefinition(`type`)

  implicit object GetMappingDefinitionExecutable extends Executable[GetMappingDefinition, GetMappingsResponse] {
    override def apply(c: Client, t: GetMappingDefinition): Future[GetMappingsResponse] = {
      injectFuture(c.admin.indices.prepareGetMappings(t.indexes.toSeq: _*).setTypes(t.types.toSeq: _*).execute)
    }
  }

  implicit object asPutMappingDefinitionExecutable extends Executable[PutMappingDefinition, PutMappingResponse] {
    override def apply(c: Client, t: PutMappingDefinition): Future[PutMappingResponse] = {
      injectFuture(c.admin.indices.putMapping(t.build, _))
    }
  }

  implicit object DeleteMappingDefinitionExecutable extends Executable[DeleteMappingDefinition, DeleteMappingResponse] {
    override def apply(c: Client, t: DeleteMappingDefinition): Future[DeleteMappingResponse] = {
      injectFuture(c.admin.indices.prepareDeleteMapping(t.indexes.toSeq: _*).setType(t.types.toSeq: _*).execute)
    }
  }
}
