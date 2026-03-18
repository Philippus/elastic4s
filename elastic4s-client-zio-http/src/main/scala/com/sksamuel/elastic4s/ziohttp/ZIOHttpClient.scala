package com.sksamuel.elastic4s.ziohttp

import com.sksamuel.elastic4s
import com.sksamuel.elastic4s.HttpEntity.{ByteArrayEntity, FileEntity, StringEntity}
import com.sksamuel.elastic4s.{ElasticRequest, HttpEntity, HttpResponse}
import zio.http.Header.ContentType
import zio.http.codec.TextBinaryCodec.fromSchema
import zio.http.{Body, Client, Header, Headers, Path, QueryParams, Request, Scheme, URL}
import zio.{Task, UIO, ZIO}

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8

object ZIOHttpClient {
  def apply(client: Client, endpoint: elastic4s.ElasticNodeEndpoint): elastic4s.HttpClient[Task] =
    new ZIOHttpClient(
      client = client,
      baseUrl = URL(
        kind =
          URL.Location.Absolute(
            Scheme.decode(endpoint.protocol).getOrElse(Scheme.HTTP),
            endpoint.host,
            Some(endpoint.port)
          ),
        path = endpoint.prefix.map(Path(_)).getOrElse(Path.empty)
      )
    )
}

class ZIOHttpClient(client: Client, baseUrl: URL) extends elastic4s.HttpClient[Task] {
  private def charsetFromString(s: String): Charset =
    Charset.availableCharsets().getOrDefault(s, UTF_8)

  private def makeBody(e: elastic4s.HttpEntity): UIO[Body] = e match {
    case StringEntity(content, None)                 => ZIO.succeed(Body.fromString(content, UTF_8))
    case StringEntity(content, Some(contentCharset)) =>
      ZIO.succeed(Body.fromString(content, charsetFromString(contentCharset)))
    case HttpEntity.InputStreamEntity(content, _)    =>
      ZIO.succeed(Body.fromStream(zio.stream.ZStream.fromInputStream(content)))
    case FileEntity(content, _)                      => Body.fromFile(content)
    case ByteArrayEntity(content, _)                 => ZIO.succeed(Body.fromArray(content))
  }

  private def getContentType(entity: elastic4s.HttpEntity) =
    entity.contentCharset.flatMap(v =>
      Header.ContentType.parse(v).map(h => Headers(h)).toOption
    )

  override def send(esRequest: elastic4s.ElasticRequest): Task[HttpResponse] = {
    for {
      body  <- esRequest.entity.map(entity => makeBody(entity)).getOrElse(ZIO.succeed(Body.empty))
      req    = makeRequest(esRequest, body)
      resp  <- client.batched.request(req)
      bytes <- resp.body.asChunk
    } yield elastic4s.HttpResponse(
      resp.status.code,
      if (bytes.isEmpty) {
        None
      } else {
        val contentCharsetOpt = resp.headers.get(ContentType).flatMap(_.charset)
        Some(elastic4s.HttpEntity.StringEntity(
          bytes.asString(contentCharsetOpt.getOrElse(UTF_8)),
          contentCharsetOpt.map(_.name())
        ))
      },
      resp.headers.toSeq.map(h => h.headerName -> h.renderedValue).toMap
    )
  }

  private def makeRequest(request: ElasticRequest, body: Body) = {
    val contentType = request.entity.flatMap(getContentType).getOrElse(Headers.empty)
    val path        = request.endpoint.stripPrefix("/").split('/').foldLeft(Path.empty)((p, s) =>
      p / java.net.URLDecoder.decode(s, UTF_8)
    )
    Request(
      method = zio.http.Method.fromString(request.method),
      headers = if (request.headers.nonEmpty) {
        val seq = request.headers.toSeq
        Headers(seq.head, seq.tail: _*) ++ contentType
      } else {
        contentType
      },
      url = baseUrl ++ URL(
        path = path,
        queryParams = if (request.params.isEmpty) {
          QueryParams.empty
        } else {
          val paramsSeq = request.params.toSeq
          QueryParams(paramsSeq.head, paramsSeq.tail: _*)
        }
      ),
      body = body
    )
  }

  override def close(): Task[Unit] = ZIO.unit
}
