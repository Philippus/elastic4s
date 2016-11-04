package com.sksamuel.elastic4s.mappings

import java.util

import com.sksamuel.elastic4s.{Executable, IndexAndType, Indexes, IndexesAndType, IndexesAndTypes, ProxyClients}
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import org.elasticsearch.action.admin.indices.mapping.put.{PutMappingAction, PutMappingRequest, PutMappingRequestBuilder, PutMappingResponse}
import org.elasticsearch.client.Client
import org.elasticsearch.cluster.metadata.MappingMetaData
import scala.concurrent.Future
import scala.language.implicitConversions
import scala.collection.JavaConverters._
import com.sksamuel.exts.OptionImplicits._

trait MappingDsl {

  val NotAnalyzed: String = "not_analyzed"
  def id: FieldDefinition = FieldDefinition("_id")

  def getMapping(str: String): GetMappingDefinition =
    if (str.contains("/")) getMapping(IndexesAndTypes(str)) else getMapping(Indexes(str))

  def getMapping(indexes: Indexes): GetMappingDefinition = getMapping(indexes.toIndexesAndTypes)
  def getMapping(indexesAndTypes: IndexesAndTypes): GetMappingDefinition = GetMappingDefinition(indexesAndTypes)

  def putMapping(indexesAndType: IndexesAndType): PutMappingDefinition = new PutMappingDefinition(indexesAndType)

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
      injectFuture(c.admin.indices.putMapping(t.request, _))
    }
  }
}

case class GetMappingDefinition(indexesAndTypes: IndexesAndTypes,
                                local: Option[Boolean] = None) {

  def types(first: String, rest: String*): GetMappingDefinition = types(first +: rest)

  def types(types: Seq[String]): GetMappingDefinition = {
    copy(indexesAndTypes = IndexesAndTypes(indexesAndTypes.indexes, types))
  }

  def local(local: Boolean) = copy(local = local.some)
}

case class GetMappingsResult(original: GetMappingsResponse) {

  @deprecated("use .mappings to use scala maps, or use original.getMappings to use the java client", "2.0")
  def getMappings = original.getMappings

  def mappingsFor(index: String): Map[String, MappingMetaData] = mappings.getOrElse(index, Map.empty)

  def mappingFor(indexAndType: IndexAndType): MappingMetaData = mappings(indexAndType.index)(indexAndType.`type`)

  def propertiesFor(indexAndType: IndexAndType): Map[String, Any] =
    mappingFor(indexAndType).sourceAsMap().get("properties").asInstanceOf[util.Map[String, _]].asScala.toMap

  def fieldFor(indexAndType: IndexAndType, field: String): Map[String, Any] =
    propertiesFor(indexAndType).get("field").asInstanceOf[util.Map[String, _]].asScala.toMap

  // returns mappings of index name to a map of types to mapping data
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
