package com.sksamuel.elastic4s.http

import com.sksamuel.exts.Logging
import org.apache.http.HttpEntity
import org.elasticsearch.client.{Response, ResponseListener, RestClient}

import scala.concurrent.{Future, Promise}

/**
  * @tparam T the type of the request object handled by this handler
  * @tparam U the type of the response object returned by this handler
  */
trait HttpExecutable[T, U] extends Logging {

  def execute(client: RestClient, request: T): Future[U]

  // convenience methods to allow implementations of HttpExec2 to execute
  // a HTTP request and receive a Scala Future.
  implicit class RichRestClient(client: RestClient) {

    import scala.collection.JavaConverters._

    private def future(callback: ResponseListener => Unit, handler: ResponseHandler[U]): Future[U] = {
      val p = Promise[U]()
      callback(new ResponseListener {
        override def onSuccess(r: Response): Unit = p.tryComplete(handler.onResponse(r))
        override def onFailure(e: Exception): Unit = p.tryComplete(handler.onError(e))
      })
      p.future
    }

    def future(method: String,
               endpoint: String,
               params: Map[String, Any],
               handler: ResponseHandler[U]): Future[U] = {
      val callback = client.performRequestAsync(method, endpoint, params.mapValues(_.toString).asJava, _: ResponseListener)
      future(callback, handler)
    }

    def future(method: String,
               endpoint: String,
               params: Map[String, Any],
               entity: HttpEntity,
               handler: ResponseHandler[U]): Future[U] = {
      val callback = client.performRequestAsync(method, endpoint, params.mapValues(_.toString).asJava, entity, _: ResponseListener)
      future(callback, handler)
    }
  }
}
