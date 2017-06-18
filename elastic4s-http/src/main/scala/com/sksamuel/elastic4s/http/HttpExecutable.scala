package com.sksamuel.elastic4s.http

import java.nio.charset.Charset

import com.sksamuel.exts.Logging
import org.apache.http.HttpEntity
import org.elasticsearch.client.{Response, ResponseListener, RestClient}

import scala.concurrent.{Future, Promise}
import scala.io.{Codec, Source}
import scala.util.{Failure, Success}

/**
  * @tparam T the type of the request object handled by this handler
  * @tparam U the type of the response object returned by this handler
  */
abstract class HttpExecutable[T, U: Manifest] extends Logging {

  import scala.concurrent.ExecutionContext.Implicits.global

  def responseHandler: ResponseHandler[U] = ResponseHandler.default[U]

  def execute(client: RestClient, request: T): Future[Response]

  def json(client: RestClient, request: T): Future[String] = execute(client, request).map { response =>
    val charset = Option(response.getEntity.getContentEncoding).map(_.getValue).getOrElse("UTF-8")
    implicit val codec = Codec(Charset.forName(charset))
    Source.fromInputStream(response.getEntity.getContent).mkString
  }

  def response(client: RestClient, request: T): Future[U] = {
    val p = Promise[U]()
    val f = execute(client, request)
    f.onComplete {
      case Success(r) => p.tryComplete(responseHandler.onResponse(r))
      case Failure(e) => p.tryComplete(responseHandler.onError(e))
    }
    p.future
  }

  // convenience methods to allow implementations of HttpExecutable to execute
  // a HTTP request and receive a Scala Future.
  implicit class RichRestClient(client: RestClient) {

    import scala.collection.JavaConverters._

    private def future(callback: ResponseListener => Unit): Future[Response] = {
      val p = Promise[Response]()
      callback(new ResponseListener {
        override def onSuccess(r: Response): Unit = p.trySuccess(r)
        override def onFailure(e: Exception): Unit = p.tryFailure(e)
      })
      p.future
    }

    def async(method: String,
              endpoint: String,
              params: Map[String, Any]): Future[Response] = {
      logger.debug(s"Executing elastic request $method:$endpoint?${params.map { case (k, v) => k + "=" + v }.mkString("&")}")
      val callback = client.performRequestAsync(method, endpoint, params.mapValues(_.toString).asJava, _: ResponseListener)
      future(callback)
    }

    def async(method: String,
              endpoint: String,
              params: Map[String, Any],
              entity: HttpEntity): Future[Response] = {
      logger.debug(s"Executing elastic request $method:$endpoint?${params.map { case (k, v) => k + "=" + v }.mkString("&")}")
      logger.debug(Source.fromInputStream(entity.getContent).getLines().mkString("\n"))
      val callback = client.performRequestAsync(method, endpoint, params.mapValues(_.toString).asJava, entity, _: ResponseListener)
      future(callback)
    }
  }
}
