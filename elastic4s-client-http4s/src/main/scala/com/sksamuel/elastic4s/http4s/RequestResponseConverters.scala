package com.sksamuel.elastic4s.http4s

import cats.effect.Async
import cats.syntax.all._
import com.sksamuel.elastic4s
import com.sksamuel.elastic4s.ElasticNodeEndpoint
import fs2.io.file.Files
import org.http4s

import scala.language.higherKinds

trait RequestResponseConverters extends Elastic4sEntityEncoders {

  def elasticRequestToHttp4sRequest[F[_] : Async : Files](
    endpoint: ElasticNodeEndpoint,
    request: elastic4s.ElasticRequest,
  ): http4s.Request[F] = {
    val uri = http4s.Uri(
      scheme = Some(http4s.Uri.Scheme.unsafeFromString(endpoint.protocol)),
      authority = Some(http4s.Uri.Authority(
        host = http4s.Uri.RegName(endpoint.host),
        port = Some(endpoint.port)
      )),
      path = http4s.Uri.Path(request.endpoint.stripPrefix("/").split('/').map(http4s.Uri.Path.Segment(_)).toVector),
      query = http4s.Query.fromPairs(request.params.toList: _*)
    )

    http4s.Request[F]()
      .withUri(uri)
      .withMethod(http4s.Method.fromString(request.method).valueOr(throw _))
      .withHeaders(http4s.Headers(request.headers.toList).put())
      .withEntity(request.entity)
      .withContentTypeOption(
        request.entity.flatMap(_.contentCharset).map(http4s.headers.`Content-Type`.parse(_).valueOr(throw _))
      )
  }

  def authorize[F[_]](request: http4s.Request[F], authorization: Authorization): http4s.Request[F] = {
    authorization match {
      case Authorization.UsernamePassword(username, password) =>
        request.putHeaders(http4s.headers.Authorization(http4s.BasicCredentials(username, password)))
      case Authorization.NoAuth =>
        request
    }
  }

  def http4sResponseToElasticResponse[F[_] : Async](
    response: http4s.Response[F]
  ): F[elastic4s.HttpResponse] = {
    for {
      body <- response.body
        .through(fs2.text.utf8.decode)
        .compile.string
    } yield elastic4s.HttpResponse(
      statusCode = response.status.code,
      entity = Some(elastic4s.HttpEntity.StringEntity(body, None)),
      headers = response.headers.headers.map { h => h.name.toString -> h.value }.toMap,
    )
  }

}
