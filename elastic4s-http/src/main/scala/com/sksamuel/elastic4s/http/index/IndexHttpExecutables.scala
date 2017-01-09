package com.sksamuel.elastic4s.http.index

import com.sksamuel.elastic4s.http.HttpExecutable
import com.sksamuel.elastic4s.indexes.IndexDefinition
import org.apache.http.entity.StringEntity
import org.elasticsearch.client.{ResponseListener, RestClient}
import org.elasticsearch.common.xcontent.XContentFactory

import scala.collection.JavaConverters._

trait IndexHttpExecutables {

  implicit object IndexHttpExecutable extends HttpExecutable[IndexDefinition, IndexResponse] {

    override def execute(client: RestClient, request: IndexDefinition): (ResponseListener) => Any = {

      val (method, endpoint) = request.id match {
        case Some(id) => "PUT" -> s"/${request.indexAndType.index}/${request.indexAndType.`type`}/$id"
        case None => "POST" -> s"/${request.indexAndType.index}/${request.indexAndType.`type`}"
      }

      val params = scala.collection.mutable.Map.empty[String, String]
      request.opType.foreach(params.put("op_type", _))
      request.routing.foreach(params.put("routing", _))
      request.parent.foreach(params.put("parent", _))
      request.timeout.foreach(params.put("timeout", _))
      request.refresh.map(_.toString).foreach(params.put("refresh", _))
      request.version.map(_.toString).foreach(params.put("version", _))

      val entity = request.source match {
        case Some(json) => new StringEntity(json)
        case None =>
          // todo needs to be replaced with some non-elastic builder to remove dep on main elastic
          val source = XContentFactory.jsonBuilder().startObject()
          request.fields.foreach(_.output(source))
          source.endObject()
          new StringEntity(source.string())
      }

      logger.debug(s"Endpoint=$endpoint")
      client.performRequestAsync(method, endpoint, params.asJava, entity, _: ResponseListener)
    }
  }
}
