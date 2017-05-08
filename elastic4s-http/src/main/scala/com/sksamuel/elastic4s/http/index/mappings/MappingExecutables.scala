package com.sksamuel.elastic4s.http.index.mappings

import com.sksamuel.elastic4s.IndexesAndTypes
import com.sksamuel.elastic4s.http.{HttpExecutable, ResponseHandler}
import com.sksamuel.elastic4s.indexes.PutMappingBuilder
import com.sksamuel.elastic4s.mappings.{GetMappingDefinition, PutMappingDefinition}
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.{Response, RestClient}

import scala.concurrent.Future
import scala.util.Try

case class IndexMappings(index: String, mappings: Map[String, Map[String, Any]])

trait MappingExecutables {

  implicit object GetMappingHttpExecutable extends HttpExecutable[GetMappingDefinition, Seq[IndexMappings]] {
    override def execute(client: RestClient, request: GetMappingDefinition): Future[Seq[IndexMappings]] = {
      val endpoint = request.indexesAndTypes match {
        case IndexesAndTypes(Nil, Nil) => "/_mapping"
        case IndexesAndTypes(indexes, Nil) => s"/${indexes.mkString(",")}/_mapping"
        case IndexesAndTypes(indexes, types) => s"/${indexes.mkString(",")}/_mapping/${types.mkString(",")}"
      }
      client.async("GET", endpoint, Map.empty, new ResponseHandler[Seq[IndexMappings]] {
        override def onResponse(response: Response): Try[Seq[IndexMappings]] = Try {
          val raw = ResponseHandler.fromEntity[Map[String, Map[String, Map[String, Map[String, Any]]]]](response.getEntity)
          raw.map { case (index, types) =>
            val mappings = types("mappings").map { case (tpe, properties) =>
              tpe -> properties("properties").asInstanceOf[Map[String, Any]]
            }
            IndexMappings(index, mappings)
          }.toSeq
        }
      })
    }
  }

  implicit object PutMappingHttpExecutable extends HttpExecutable[PutMappingDefinition, PutMappingResponse] {

    override def execute(client: RestClient, request: PutMappingDefinition): Future[PutMappingResponse] = {

      val endpoint = s"/${request.indexesAndType.indexes.mkString(",")}/_mapping/${request.indexesAndType.`type`}"

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.updateAllTypes.foreach(params.put("update_all_types", _))
      request.ignoreUnavailable.foreach(params.put("ignore_unavailable", _))
      request.allowNoIndices.foreach(params.put("allow_no_indices", _))
      request.expandWildcards.foreach(params.put("expand_wildcards", _))

      val body = PutMappingBuilder(request).string()
      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)

      client.async("PUT", endpoint, params.toMap, entity, ResponseHandler.default)
    }
  }
}

case class GetMapping()

case class PutMappingResponse(acknowledged: Boolean) {
  def success: Boolean = acknowledged
}
