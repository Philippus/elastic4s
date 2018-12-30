package com.sksamuel.elastic4s

/**
  * An [[ElasticRequest]] models all the required fields for a request to be
  * sent to Elasticsearch. Request types such as [[com.sksamuel.elastic4s.requests.searches.SearchRequest]],
  * etc, are ultimately converted into this class by means of a [[Handler]] typeclass instance.
  */
case class ElasticRequest(method: String, endpoint: String, params: Map[String, String], entity: Option[HttpEntity])
object ElasticRequest {

  def apply(method: String, endpoint: String): ElasticRequest = apply(method, endpoint, Map.empty[String, Any])

  def apply(method: String, endpoint: String, body: HttpEntity): ElasticRequest =
    apply(method, endpoint, Map.empty[String, Any], body)

  def apply(method: String, endpoint: String, params: Map[String, Any]): ElasticRequest =
    apply(method, endpoint, params.mapValues(_.toString), None)

  def apply(method: String, endpoint: String, params: Map[String, Any], body: HttpEntity): ElasticRequest =
    apply(method, endpoint, params.mapValues(_.toString), Some(body))

  implicit val ElasticRequestShow: Show[ElasticRequest] = new Show[ElasticRequest] {
    override def show(t: ElasticRequest): String = {
      val header = s"${t.method}:${t.endpoint}?${t.params.map { case (k, v) => k + "=" + v }.mkString("&")}"
      t.entity.fold(header) { body =>
        s"$header\n$body"
      }
    }
  }
}
