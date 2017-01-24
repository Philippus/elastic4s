package com.sksamuel.elastic4s.http.index

import com.sksamuel.elastic4s.admin.RefreshIndexDefinition
import com.sksamuel.elastic4s.http.HttpExecutable
import org.elasticsearch.client.{ResponseListener, RestClient}

case class RefreshIndexResponse()

trait RefreshIndexExecutables {

  implicit object RefreshIndexExecutable extends HttpExecutable[RefreshIndexDefinition, RefreshIndexResponse] {
    override def execute(client: RestClient, request: RefreshIndexDefinition): (ResponseListener) => Any = {
      val url = "/" + request.indexes.mkString(",") + "/_refresh"
      val method = "POST"
      client.performRequestAsync(method, url, _)
    }
  }

}
