package com.sksamuel.elastic4s.http4s

import cats.effect.unsafe.IORuntime
import cats.effect.{Async, IO}
import com.sksamuel.elastic4s
import com.sksamuel.elastic4s.ElasticNodeEndpoint
import fs2.io.file.Files
import org.http4s

import scala.language.higherKinds

// class to support callback interface
trait CallbackRunner[F[_]] {
  def run[A](fa: F[A], cb: Either[Throwable, A] => Unit): Unit
}

object Http4sClient {

  def usingIO(
    client: http4s.client.Client[IO],
    endpoint: ElasticNodeEndpoint,
    authorization: Authorization = Authorization.NoAuth
  )(implicit runtime: IORuntime): Http4sClient[IO] = {
    val ioRunner = new CallbackRunner[IO] {
      override def run[A](fa: IO[A], cb: Either[Throwable, A] => Unit): Unit = fa.unsafeRunAsync(cb)
    }

    new Http4sClient(
      client = client,
      endpoint = endpoint,
      authorization = authorization,
      runner = ioRunner
    )
  }

}

class Http4sClient[F[_] : Async : Files](
  client: http4s.client.Client[F],
  endpoint: ElasticNodeEndpoint,
  authorization: Authorization,
  runner: CallbackRunner[F],
) extends elastic4s.HttpClient with RequestResponseConverters {

  override def send(
    request: elastic4s.ElasticRequest,
    callback: Either[Throwable, elastic4s.HttpResponse] => Unit
  ): Unit = {
    val http4sRequest = authorize(
      elasticRequestToHttp4sRequest[F](endpoint, request),
      authorization
    )

    val response = client.run(http4sRequest).use(http4sResponseToElasticResponse[F])

    runner.run(response, callback)
  }

  // Instantiation of the http4s client happens by the Resource monad, so closing should be managed by it as well
  override def close(): Unit = ()
}
