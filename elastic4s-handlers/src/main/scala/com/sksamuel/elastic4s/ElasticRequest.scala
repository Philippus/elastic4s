package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.HttpEntity.StringEntity

/** An [[ElasticRequest]] models all the required fields for a request to be sent to Elasticsearch.
  *
  * Request types such as [[SearchRequest]], etc, are ultimately converted into this class by means of a [[Handler]]
  * typeclass instance.
  */
case class ElasticRequest(
    method: String,
    endpoint: String,
    params: Map[String, String],
    entity: Option[HttpEntity],
    headers: Map[String, String]
) {
  def addParameter(name: String, value: String): ElasticRequest = copy(params = params + (name -> value))

  def addHeader(name: String, value: String): ElasticRequest = copy(headers = headers + (name -> value))

  def addHeaders(headers: Map[String, String]): ElasticRequest = copy(headers = this.headers ++ headers)
}

object ElasticRequest {

  def apply(method: String, endpoint: String): ElasticRequest = apply(method, endpoint, Map.empty[String, Any])

  def apply(method: String, endpoint: String, body: HttpEntity): ElasticRequest =
    apply(method, endpoint, Map.empty[String, Any], body)

  def apply(method: String, endpoint: String, params: Map[String, Any]): ElasticRequest =
    apply(method, endpoint, params.view.mapValues(_.toString).toMap, None, Map.empty)

  def apply(method: String, endpoint: String, params: Map[String, Any], body: HttpEntity): ElasticRequest =
    apply(method, endpoint, params.view.mapValues(_.toString).toMap, Some(body), Map.empty)

  implicit val ElasticRequestShow: Show[ElasticRequest] = new Show[ElasticRequest] {
    override def show(t: ElasticRequest): String = {
      val queryParams = t.params.map { case (k, v) => k + "=" + v }.mkString("&")
      val headers     = t.headers.map { case (k, v) => k + ": " + v }.mkString("\n")
      val header      = s"${t.method} ${t.endpoint}?$queryParams".stripSuffix("?")
      t.entity.fold(header) { body =>
        val content = body match {
          case StringEntity(content, _) => content
          case _                        => body
        }
        s"$headers\n$header\n$content"
      }
    }
  }
}
