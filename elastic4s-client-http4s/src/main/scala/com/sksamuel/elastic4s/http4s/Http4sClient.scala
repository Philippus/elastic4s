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
    endpoint: http4s.Uri,
  )(implicit runtime: IORuntime): Http4sClient[IO] = {
    val ioRunner = new CallbackRunner[IO] {
      override def run[A](fa: IO[A], cb: Either[Throwable, A] => Unit): Unit = fa.unsafeRunAsync(cb)
    }

    new Http4sClient(
      client = client,
      endpoint = endpoint,
      runner = ioRunner
    )
  }

  @deprecated("Use usingIO with http4s.Uri", "8.16.0")
  def usingIO(
    client: http4s.client.Client[IO],
    endpoint: ElasticNodeEndpoint,
  )(implicit runtime: IORuntime): Http4sClient[IO] = {
    val ioRunner = new CallbackRunner[IO] {
      override def run[A](fa: IO[A], cb: Either[Throwable, A] => Unit): Unit = fa.unsafeRunAsync(cb)
    }

    Http4sClient(
      client = client,
      endpoint = endpoint,
      runner = ioRunner
    )
  }

  def apply[F[_] : Async : Files](
    client: http4s.client.Client[F],
    endpoint: ElasticNodeEndpoint,
    runner: CallbackRunner[F],
  ): Http4sClient[F] = {
    val uri = http4s.Uri(
      scheme = Some(http4s.Uri.Scheme.http),
      authority = Some(http4s.Uri.Authority(host = http4s.Uri.RegName(endpoint.host), port = Some(endpoint.port))),
      )
    new Http4sClient(
      client = client,
      endpoint = uri,
      runner = runner
    )
  }

}

class Http4sClient[F[_] : Async : Files](
  client: http4s.client.Client[F],
  endpoint: http4s.Uri,
  runner: CallbackRunner[F],
) extends elastic4s.HttpClient with RequestResponseConverters {

  override def send(
    request: elastic4s.ElasticRequest,
    callback: Either[Throwable, elastic4s.HttpResponse] => Unit
  ): Unit = {
    val http4sRequest = elasticRequestToHttp4sRequest[F](endpoint, request)

    val response = client.run(http4sRequest).use(http4sResponseToElasticResponse[F])

    runner.run(response, callback)
  }

  // Instantiation of the http4s client happens by the Resource monad, so closing should be managed by it as well
  override def close(): Unit = ()
}
