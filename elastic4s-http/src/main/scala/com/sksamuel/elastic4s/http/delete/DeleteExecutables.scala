package com.sksamuel.elastic4s.http.delete

import com.sksamuel.elastic4s.delete.DeleteByIdDefinition
import com.sksamuel.elastic4s.http.{HttpExecutable, RefreshPolicyHttpValue, Shards}
import org.elasticsearch.client.{ResponseListener, RestClient}

import scala.collection.JavaConverters._

case class DeleteResponse(_shards: Shards,
                          found: Boolean,
                          _index: String,
                          _type: String,
                          _id: String,
                          _version: Long,
                          result: String)

trait DeleteExecutables {

  implicit object DeleteExecutable extends HttpExecutable[DeleteByIdDefinition, DeleteResponse] {

    override def execute(client: RestClient, request: DeleteByIdDefinition): (ResponseListener) => Any = {
      val method = "DELETE"
      val url = s"/${request.indexType.index}/${request.indexType.`type`}/${request.id}"

      val params = scala.collection.mutable.Map.empty[String, String]
      request.parent.foreach(params.put("parent", _))
      request.routing.foreach(params.put("routing", _))
      request.refresh.map(RefreshPolicyHttpValue.apply).foreach(params.put("refresh", _))
      request.version.map(_.toString).foreach(params.put("version", _))
      request.versionType.map(_.name).foreach(params.put("versionType", _))
      request.waitForActiveShards.map(_.toString).foreach(params.put("wait_for_active_shards", _))

      client.performRequestAsync(method, url, params.asJava, _)
    }
  }
}
