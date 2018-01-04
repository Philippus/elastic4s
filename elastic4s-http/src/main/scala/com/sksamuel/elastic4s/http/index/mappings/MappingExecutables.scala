package com.sksamuel.elastic4s.http.index.mappings

import com.sksamuel.elastic4s.IndexesAndTypes
import com.sksamuel.elastic4s.http._
import com.sksamuel.elastic4s.indexes.PutMappingBuilderFn
import com.sksamuel.elastic4s.mappings.{GetMappingDefinition, PutMappingDefinition}
import org.apache.http.entity.ContentType

case class IndexMappings(index: String, mappings: Map[String, Map[String, Any]])

trait MappingExecutables {

  implicit object GetMappingHttpExecutable extends HttpExecutable[GetMappingDefinition, Seq[IndexMappings]] {

    override def responseHandler: ResponseHandler[Seq[IndexMappings]] = new ResponseHandler[Seq[IndexMappings]] {
      override def handle(response: HttpResponse) = {
        val raw = ResponseHandler.fromResponse[Map[String, Map[String, Map[String, Map[String, Any]]]]](response)
        val raw2 = raw.map { case (index, types) =>
          val mappings = types("mappings").map { case (tpe, properties) =>
            tpe -> properties("properties").asInstanceOf[Map[String, Any]]
          }
          IndexMappings(index, mappings)
        }.toSeq
        Right(raw2)
      }
    }

    override def execute[F[_]: AsyncExecutor](client: HttpRequestClient, request: GetMappingDefinition): F[HttpResponse] = {
      val endpoint = request.indexesAndTypes match {
        case IndexesAndTypes(Nil, Nil) => "/_mapping"
        case IndexesAndTypes(indexes, Nil) => s"/${indexes.mkString(",")}/_mapping"
        case IndexesAndTypes(indexes, types) => s"/${indexes.mkString(",")}/_mapping/${types.mkString(",")}"
      }
      client.async("GET", endpoint, Map.empty)
    }
  }

  implicit object PutMappingHttpExecutable extends HttpExecutable[PutMappingDefinition, PutMappingResponse] {

    override def execute[F[_]: AsyncExecutor](client: HttpRequestClient, request: PutMappingDefinition): F[HttpResponse] = {

      val endpoint = s"/${request.indexesAndType.indexes.mkString(",")}/_mapping/${request.indexesAndType.`type`}"

      val params = scala.collection.mutable.Map.empty[String, Any]
      request.updateAllTypes.foreach(params.put("update_all_types", _))
      request.ignoreUnavailable.foreach(params.put("ignore_unavailable", _))
      request.allowNoIndices.foreach(params.put("allow_no_indices", _))
      request.expandWildcards.foreach(params.put("expand_wildcards", _))

      val body = PutMappingBuilderFn(request).string()
      val entity = HttpEntity(body, ContentType.APPLICATION_JSON.getMimeType)

      client.async("PUT", endpoint, params.toMap, entity)
    }
  }
}


case class PutMappingResponse(acknowledged: Boolean) {
  def success: Boolean = acknowledged
}
