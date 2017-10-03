package com.sksamuel.elastic4s.http.settings

import com.sksamuel.elastic4s.Index
import com.sksamuel.elastic4s.http.update.RequestFailure
import com.sksamuel.elastic4s.http.{HttpExecutable, HttpRequestClient, HttpResponse, ResponseHandler}
import com.sksamuel.elastic4s.json.JacksonSupport
import com.sksamuel.elastic4s.settings.{GetSettingsDefinition, UpdateSettingsDefinition}
import com.sksamuel.exts.collection.Maps

import scala.collection.JavaConverters._
import scala.concurrent.Future

case class IndexSettingsResponse(settings: Map[Index, Map[String, String]]) {
  def settingsForIndex(index: Index) = settings(index)
}

trait SettingsImplicits {

  implicit object GetSettingsHttpExecutable extends HttpExecutable[GetSettingsDefinition, Either[RequestFailure, IndexSettingsResponse]] {

    override def responseHandler = new ResponseHandler[Either[RequestFailure, IndexSettingsResponse]] {
      override def doit(response: HttpResponse): Either[RequestFailure, IndexSettingsResponse] = response.statusCode match {
        case 200 =>
          val root = JacksonSupport.mapper.readTree(response.entity.get.content)
          val settings = root.fields.asScala.map { entry =>
            val indexSettings = JacksonSupport.mapper.readValue[Map[String, Any]](JacksonSupport.mapper.writeValueAsBytes(entry.getValue))
            // we don't want the 'settings.' prefix on the settings map, that's just in the json for tidyness, but the actual settings names are not settings.index.uuid but index.uuid
            Index(entry.getKey) -> Maps.flatten(indexSettings).map { case (key, value) => key.stripPrefix("settings.") -> value.toString }
          }.toMap
          Right(IndexSettingsResponse(settings))
        case _ => Left(ResponseHandler.fromEntity[RequestFailure](response.entity.get))
      }
    }

    override def execute(client: HttpRequestClient, request: GetSettingsDefinition): Future[HttpResponse] = {
      val endpoint = "/" + request.indexes.string + "/_settings"
      client.async("GET", endpoint, Map.empty)
    }
  }

  implicit object UpdateSettingsHttpExecutable extends HttpExecutable[UpdateSettingsDefinition, Either[RequestFailure, IndexSettingsResponse]] {

    override def responseHandler = new ResponseHandler[Either[RequestFailure, IndexSettingsResponse]] {
      override def doit(response: HttpResponse): Either[RequestFailure, IndexSettingsResponse] = response.statusCode match {
        case 200 => Right(ResponseHandler.fromEntity[IndexSettingsResponse](response.entity.get))
        case _ => Left(ResponseHandler.fromEntity[RequestFailure](response.entity.get))
      }
    }

    override def execute(client: HttpRequestClient, request: UpdateSettingsDefinition): Future[HttpResponse] = {
     // val endpoint = "/" + request.indexes.string + "/_settings"
      client.async("POST", "", Map.empty)
    }
  }
}
