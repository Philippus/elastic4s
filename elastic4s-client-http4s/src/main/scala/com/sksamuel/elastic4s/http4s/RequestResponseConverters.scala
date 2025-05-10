package com.sksamuel.elastic4s.http4s

import cats.effect.Sync
import cats.syntax.all._
import com.sksamuel.elastic4s
import fs2.io.file.Files
import org.http4s

trait RequestResponseConverters extends Elastic4sEntityEncoders {

  def elasticRequestToHttp4sRequest[F[_]: Sync: Files](
      endpoint: http4s.Uri,
      request: elastic4s.ElasticRequest
  ): http4s.Request[F] = {
    val uri =
      endpoint.copy(
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

  def http4sResponseToElasticResponse[F[_]: Sync](
      response: http4s.Response[F]
  ): F[elastic4s.HttpResponse] = {
    for {
      body <- response.body
                .through(fs2.text.utf8.decode)
                .compile.string
    } yield elastic4s.HttpResponse(
      statusCode = response.status.code,
      entity = Some(elastic4s.HttpEntity.StringEntity(body, None)),
      headers = response.headers.headers.map { h => h.name.toString -> h.value }.toMap
    )
  }

}
