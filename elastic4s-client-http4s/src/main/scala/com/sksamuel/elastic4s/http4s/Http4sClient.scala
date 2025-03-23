package com.sksamuel.elastic4s.http4s

import cats.effect.Sync
import com.sksamuel.elastic4s
import com.sksamuel.elastic4s.ElasticNodeEndpoint
import fs2.io.file.Files
import org.http4s

import scala.language.higherKinds

object Http4sClient {

  def apply[F[_]: Sync: Files](
      client: http4s.client.Client[F],
      endpoint: ElasticNodeEndpoint
  ): Http4sClient[F] = {
    val uri = http4s.Uri(
      scheme = Some(http4s.Uri.Scheme.http),
      authority = Some(http4s.Uri.Authority(host = http4s.Uri.RegName(endpoint.host), port = Some(endpoint.port)))
    )
    new Http4sClient(
      client = client,
      endpoint = uri
    )
  }

}

class Http4sClient[F[_]: Sync: Files](
    client: http4s.client.Client[F],
    endpoint: http4s.Uri
) extends elastic4s.HttpClient[F] with RequestResponseConverters {

  override def send(
      request: elastic4s.ElasticRequest
  ): F[elastic4s.HttpResponse] = {
    val http4sRequest = elasticRequestToHttp4sRequest[F](endpoint, request)

    client.run(http4sRequest).use(http4sResponseToElasticResponse[F])
  }

  // Instantiation of the http4s client happens by the Resource monad, so closing should be managed by it as well
  override def close(): F[Unit] = Sync[F].unit
}
