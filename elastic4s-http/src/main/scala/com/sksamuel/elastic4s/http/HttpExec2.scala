package com.sksamuel.elastic4s.http

import org.apache.http.HttpEntity
import org.elasticsearch.client.{Response, ResponseListener, RestClient}

import scala.concurrent.{Future, Promise}
import scala.collection.JavaConverters._

trait HttpExec2[T, U] {

  def execute(client: RestClient, request: T): Future[U]

  // convenience methods to allow implementations of HttpExec2 to execute
  // a HTTP request and receive a Scala Future.
  implicit class RichRestClient(client: RestClient) {

    def future(method: String,
               endpoint: String,
               params: Map[String, Any],
               entity: HttpEntity,
               handler: ResponseHandler[U]): Future[U] = {
      val p = Promise[U]()
      val callback = client.performRequestAsync(method, endpoint, params.mapValues(_.toString).asJava, entity, _: ResponseListener)
      callback(new ResponseListener {
        override def onSuccess(r: Response): Unit = p.trySuccess(handler.handle(r))
        override def onFailure(e: Exception): Unit = p.tryFailure(e)
      })
      p.future
    }
  }
}
