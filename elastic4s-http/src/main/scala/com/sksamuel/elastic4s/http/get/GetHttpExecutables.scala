package com.sksamuel.elastic4s.http.get

import com.sksamuel.elastic4s.get.GetDefinition
import com.sksamuel.elastic4s.http.HttpExecutable
import com.sksamuel.exts.Logging
import org.elasticsearch.client.{ResponseListener, RestClient}

import scala.collection.JavaConverters._

trait GetHttpExecutables {

  implicit object GetHttpExecutable extends HttpExecutable[GetDefinition, GetResponse] with Logging {

    override def execute(client: RestClient, request: GetDefinition): ResponseListener => Any = {

      val endpoint = s"/${request.indexAndType.index}/${request.indexAndType.`type`}/${request.id}"
      logger.debug(s"Endpoint=$endpoint")

      val params = scala.collection.mutable.Map.empty[String, String]
      request.fetchSource.foreach { context =>
        if (!context.enabled) params.put("_source", "false")
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

      client.performRequestAsync("GET", endpoint, params.asJava, _: ResponseListener)
    }
  }
}
