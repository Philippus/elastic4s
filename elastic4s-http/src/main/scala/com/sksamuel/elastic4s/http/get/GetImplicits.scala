package com.sksamuel.elastic4s.http.get

import cats.Show
import com.sksamuel.elastic4s.HitReader
import com.sksamuel.elastic4s.get.{GetDefinition, MultiGetDefinition}
import com.sksamuel.elastic4s.http.{HttpExecutable, ResponseHandler}
import com.sksamuel.exts.Logging
import org.apache.http.entity.{ContentType, StringEntity}
import org.elasticsearch.client.RestClient

import scala.concurrent.Future

case class MultiGetResponse(docs: Seq[GetResponse]) {
  def items: Seq[GetResponse] = docs
  def size: Int = docs.size

  def to[T: HitReader]: IndexedSeq[T] = docs.map(_.to[T]).toIndexedSeq
  def safeTo[T: HitReader]: IndexedSeq[Either[Throwable, T]] = docs.map(_.safeTo[T]).toIndexedSeq
}

trait GetImplicits {

  implicit object MultiGetShow extends Show[MultiGetDefinition] {
    override def show(f: MultiGetDefinition): String = MultiGetBodyBuilder(f).string()
  }

  implicit object MultiGetHttpExecutable extends HttpExecutable[MultiGetDefinition, MultiGetResponse] with Logging {

    import scala.concurrent.ExecutionContext.Implicits._

    private val endpoint = "/_mget"

    override def execute(client: RestClient, request: MultiGetDefinition): Future[MultiGetResponse] = {

      val body = MultiGetBodyBuilder(request).string()
      logger.debug(s"Executing multiget $body")
      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)

      client.async("POST", endpoint, Map.empty, entity, ResponseHandler.failure404).map { response =>
        response.copy(docs = response.docs.map { doc =>
          doc.copy(fields = Option(doc.fields).getOrElse(Map.empty))
        })
      }
    }
  }

  implicit object GetHttpExecutable extends HttpExecutable[GetDefinition, GetResponse] with Logging {

    import scala.concurrent.ExecutionContext.Implicits._

    override def execute(client: RestClient, request: GetDefinition): Future[GetResponse] = {

      val endpoint = s"/${request.indexAndType.index}/${request.indexAndType.`type`}/${request.id}"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.fetchSource.foreach { context =>
        if (!context.fetchSource)
          params.put("_source", "false")
        else {
          if (context.includes().nonEmpty) {
            params.put("_source_include", context.includes.mkString(","))
          }
          if (context.excludes.nonEmpty) {
            params.put("_source_exclude", context.excludes.mkString(","))
          }
        }
      }
      if (request.storedFields.nonEmpty) {
        params.put("stored_fields", request.storedFields.mkString(","))
      }
      request.parent.foreach(params.put("parent", _))
      request.routing.foreach(params.put("routing", _))
      request.preference.foreach(params.put("preference", _))
      request.refresh.map(_.toString).foreach(params.put("refresh", _))
      request.realtime.map(_.toString).foreach(params.put("realtime", _))
      request.version.map(_.toString).foreach(params.put("version", _))
      request.versionType.foreach(params.put("versionType", _))

      client.async("GET", endpoint, params.toMap, ResponseHandler.failure404).map { response =>
        response.copy(fields = Option(response.fields).getOrElse(Map.empty))
      }
    }
  }
}
