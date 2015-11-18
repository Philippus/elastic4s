package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.{Executable, IndexesAndTypes, ProxyClients}
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import org.elasticsearch.action.admin.indices.mapping.put.{PutMappingAction, PutMappingRequest, PutMappingRequestBuilder, PutMappingResponse}
import org.elasticsearch.client.Client

import scala.concurrent.Future
import scala.language.implicitConversions

/** @author Stephen Samuel */
trait MappingDsl {

  val NotAnalyzed: String = "not_analyzed"
  def id: FieldDefinition = "_id"

  @deprecated("use field(x)", "2.0.0")
  implicit def stringToField(name: String): FieldDefinition = new FieldDefinition(name)
  @deprecated("use mapping(x)", "2.0.0")
  implicit def stringToMap(`type`: String): MappingDefinition = new MappingDefinition(`type`)

  case class GetMappingDefinition(indexesAndTypes: IndexesAndTypes) {
    def types(first: String, rest: String*): GetMappingDefinition = types(first +: rest)
    def types(types: Seq[String]): GetMappingDefinition = {
      copy(indexesAndTypes = IndexesAndTypes(indexesAndTypes.indexes, types))
    }
  }

  implicit object GetMappingDefinitionExecutable
    extends Executable[GetMappingDefinition, GetMappingsResponse, GetMappingsResponse] {
    override def apply(c: Client, t: GetMappingDefinition): Future[GetMappingsResponse] = {
      injectFuture(
        c.admin.indices.prepareGetMappings(t.indexesAndTypes.indexes: _*).setTypes(t.indexesAndTypes.types: _*).execute
      )
    }
  }

  implicit object PutMappingDefinitionExecutable
    extends Executable[PutMappingDefinition, PutMappingResponse, PutMappingResponse] {
    override def apply(c: Client, t: PutMappingDefinition): Future[PutMappingResponse] = {
      injectFuture(c.admin.indices.putMapping(t.request, _))
    }
  }
}

class PutMappingDefinition(indexesType: IndexesAndTypes) extends MappingDefinition(indexesType.types.head) {

  def request: PutMappingRequest = {
    val req = new PutMappingRequestBuilder(ProxyClients.indices, PutMappingAction.INSTANCE)
      .setIndices(indexesType.indexes: _*)
      .setType(indexesType.types.head)
      .setSource(super.build)
    req.request()
  }
}
