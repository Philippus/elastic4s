package com.sksamuel.elastic4s.http.get

import cats.Show
import cats.syntax.either._
import com.sksamuel.elastic4s.{HitReader, JsonFormat}
import com.sksamuel.elastic4s.get.{GetDefinition, MultiGetDefinition}
import com.sksamuel.elastic4s.http.HttpExecutable
import com.sksamuel.exts.Logging
import org.apache.http.entity.StringEntity
import org.elasticsearch.client.RestClient

import scala.collection.JavaConverters._
import scala.concurrent.Future

case class MultiGetResponse(docs: Seq[GetResponse]) {
  def items: Seq[GetResponse] = docs
  def size: Int = docs.size

  def to[T: HitReader]: IndexedSeq[T] = safeTo.flatMap(_.toOption)
  def safeTo[T: HitReader]: IndexedSeq[Either[Throwable, T]] = docs.map(_.safeTo[T]).toIndexedSeq
}

trait GetImplicits {

  implicit object MultiGetShow extends Show[MultiGetDefinition] {
    override def show(f: MultiGetDefinition): String = MultiGetBodyBuilder(f).string()
  }

  implicit object MultiGetHttpExecutable extends HttpExecutable[MultiGetDefinition, MultiGetResponse] with Logging {
    override def execute(client: RestClient,
                         request: MultiGetDefinition,
                         format: JsonFormat[MultiGetResponse]): Future[MultiGetResponse] = {

      val body = MultiGetBodyBuilder(request).string()
      logger.debug(s"Executing multiget $body")
      val entity = new StringEntity(body)

      val params = scala.collection.mutable.Map.empty[String, String]

      executeAsyncAndMapResponse(client.performRequestAsync("POST", "/_mget", params.asJava, entity, _), format)
    }
  }

  implicit object GetHttpExecutable extends HttpExecutable[GetDefinition, GetResponse] with Logging {

    override def execute(client: RestClient,
                         request: GetDefinition,
                         format: JsonFormat[GetResponse]): Future[GetResponse] = {

      val endpoint = s"/${request.indexAndType.index}/${request.indexAndType.`type`}/${request.id}"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.fetchSource.foreach { context =>
        if (!context.fetchSource)
          params.put("_source", "false")
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

      executeAsyncAndMapResponse(client.performRequestAsync("GET", endpoint, params.asJava, _), format)
    }
  }
}
