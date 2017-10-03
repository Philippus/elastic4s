package com.sksamuel.elastic4s.http.settings

import com.sksamuel.elastic4s.http.update.RequestFailure
import com.sksamuel.elastic4s.http.{HttpExecutable, HttpRequestClient, HttpResponse}
import com.sksamuel.elastic4s.settings.{GetSettingsDefinition, UpdateSettingsDefinition}

import scala.concurrent.Future

case class IndexSettingsResponse(settings: Map[String, String])

trait SettingsImplicits {

  implicit object GetSettingsHttpExecutable extends HttpExecutable[GetSettingsDefinition, Either[RequestFailure, IndexSettingsResponse]] {
    override def execute(client: HttpRequestClient, request: GetSettingsDefinition): Future[HttpResponse] = {
      val endpoint = "/" + request.indexes.string + "/_settings"
      client.async("GET", endpoint, Map.empty)
    }
  }

  implicit object UpdateSettingsHttpExecutable extends HttpExecutable[UpdateSettingsDefinition, Either[RequestFailure, IndexSettingsResponse]] {
    override def execute(client: HttpRequestClient, request: UpdateSettingsDefinition): Future[HttpResponse] = {
     // val endpoint = "/" + request.indexes.string + "/_settings"
      client.async("POST", "", Map.empty)
    }
  }
}
