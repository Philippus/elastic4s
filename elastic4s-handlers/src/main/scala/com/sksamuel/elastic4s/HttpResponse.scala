package com.sksamuel.elastic4s

/**
  * The response passed to the callabck of a [[HttpClient]].
  */
case class HttpResponse(statusCode: Int, entity: Option[HttpEntity.StringEntity], headers: Map[String, String])
