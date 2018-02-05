package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.Show
import com.sksamuel.exts.Logging
import com.sksamuel.exts.OptionImplicits._

/**
  * @tparam T the type of the request object handled by this handler
  * @tparam U the type of the response object returned by this handler
  */
abstract class Handler[T, U: Manifest] extends Logging {
  def responseHandler: ResponseHandler[U] = ResponseHandler.default[U]
  def build(t: T): ElasticRequest
}

// models everything needed to send the request to elasticsearch
// all request types, like SearchRequest, UpdateRequest, etc ultimately are converted
// into this case class by a RequestBuilder instance.
case class ElasticRequest(method: String, endpoint: String, params: Map[String, String], entity: Option[HttpEntity])

object ElasticRequestShow extends Show[ElasticRequest] {
  override def show(t: ElasticRequest): String = {
    val header = s"${t.method}:${t.endpoint}?${t.params.map { case (k, v) => k + "=" + v }.mkString("&")}"
    t.entity.fold(header) { body => s"$header\n$body" }
  }
}

object ElasticRequest {

  def apply(method: String, endpoint: String): ElasticRequest = apply(method, endpoint, Map.empty[String, Any])

  def apply(method: String, endpoint: String, body: HttpEntity): ElasticRequest =
    apply(method, endpoint, Map.empty[String, Any], body)

  def apply(method: String, endpoint: String, params: Map[String, Any]): ElasticRequest =
    apply(method, endpoint, params.mapValues(_.toString), None)

  def apply(method: String, endpoint: String, params: Map[String, Any], body: HttpEntity): ElasticRequest =
    apply(method, endpoint, params.mapValues(_.toString), body.some)
}
