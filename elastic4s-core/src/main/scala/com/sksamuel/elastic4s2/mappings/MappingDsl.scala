package com.sksamuel.elastic4s2.mappings

import com.sksamuel.elastic4s2.{IndexesAndType, Executable, IndexesAndTypes, ProxyClients}
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import org.elasticsearch.action.admin.indices.mapping.put.{PutMappingAction, PutMappingRequest, PutMappingRequestBuilder, PutMappingResponse}
import org.elasticsearch.client.Client
import org.elasticsearch.cluster.metadata.MappingMetaData

import scala.concurrent.Future
import scala.language.implicitConversions

/** @author Stephen Samuel */
trait MappingDsl {

  val NotAnalyzed: String = "not_analyzed"
  def id: FieldDefinition = "_id"

  @deprecated("use field(x)", "2.0.0")
  implicit def stringToField(name: String): FieldDefinition = FieldDefinition(name)
  @deprecated("use mapping(x)", "2.0.0")
  implicit def stringToMap(`type`: String): MappingDefinition = new MappingDefinition(`type`)

  def putMapping(indexesAndType: IndexesAndType): PutMappingDefinition = new PutMappingDefinition(indexesAndType)

  implicit object GetMappingDefinitionExecutable
    extends Executable[GetMappingDefinition, GetMappingsResponse, GetMappingsResult] {
    override def apply(c: Client, t: GetMappingDefinition): Future[GetMappingsResult] = {
      injectFutureAndMap(
        c.admin.indices
          .prepareGetMappings(t.indexesAndTypes.indexes: _*)
          .setTypes(t.indexesAndTypes.types: _*)
          .execute
      )(GetMappingsResult.apply)
    }
  }

  implicit object PutMappingDefinitionExecutable
    extends Executable[PutMappingDefinition, PutMappingResponse, PutMappingResponse] {
    override def apply(c: Client, t: PutMappingDefinition): Future[PutMappingResponse] = {
      injectFuture(c.admin.indices.putMapping(t.request, _))
    }
  }
}

case class GetMappingDefinition(indexesAndTypes: IndexesAndTypes) {
  def types(first: String, rest: String*): GetMappingDefinition = types(first +: rest)
  def types(types: Seq[String]): GetMappingDefinition = {
    copy(indexesAndTypes = IndexesAndTypes(indexesAndTypes.indexes, types))
  }
}

case class GetMappingsResult(original: GetMappingsResponse) {

  import scala.collection.JavaConverters._

  @deprecated("use .mappings to use scala maps, or use original.getMappings to use the java client", "2.0")
  def getMappings = original.getMappings

  def mappings: Map[String, Map[String, MappingMetaData]] = {
    original.mappings.iterator.asScala.map { x =>
      x.key -> x.value.iterator.asScala.map { y => y.key -> y.value }.toMap
    }.toMap
  }
}

class PutMappingDefinition(indexesAndType: IndexesAndType) extends MappingDefinition(indexesAndType.`type`) {

  def request: PutMappingRequest = {
    val req = new PutMappingRequestBuilder(ProxyClients.indices, PutMappingAction.INSTANCE)
      .setIndices(indexesAndType.indexes: _*)
      .setType(indexesAndType.`type`)
      .setSource(super.build)
    req.request()
  }
}
