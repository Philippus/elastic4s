package com.sksamuel.elastic4s.http

import java.nio.charset.Charset

import org.elasticsearch.client.{ResponseException, ResponseListener}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.io.{Codec, Source}

// We'd probably want a better name for this, and it could live elsewhere. Other effect types (twitter future, cats IO, scalaz Task, etc)
// could live in their own separate packages.
object ScalaFutureFromListener {
  implicit def scalaFutureFromListenerInstance(
      implicit ec: ExecutionContext): FromListener[Future] = new FromListener[Future] {

    override def map[A, B](fa: Future[A])(f: A => B): Future[B] = fa.map(f)

    override def fromListener(callback: ResponseListener => Unit): Future[HttpResponse] = {

      val p = Promise[HttpResponse]()
      callback(new ResponseListener {

        def fromResponse(r: org.elasticsearch.client.Response): HttpResponse = {
          val entity = Option(r.getEntity).map { entity =>
            val contentEncoding =
              Option(entity.getContentEncoding).map(_.getValue).getOrElse("UTF-8")
            implicit val codec = Codec(Charset.forName(contentEncoding))
            val body           = Source.fromInputStream(entity.getContent).mkString
            HttpEntity.StringEntity(body, Some(contentEncoding))
          }
          val headers = r.getHeaders.map { header =>
            header.getName -> header.getValue
          }.toMap
          HttpResponse(r.getStatusLine.getStatusCode, entity, headers)
        }

        override def onSuccess(r: org.elasticsearch.client.Response): Unit =
          p.trySuccess(fromResponse(r))
        override def onFailure(e: Exception): Unit = e match {
          case re: ResponseException => p.trySuccess(fromResponse(re.getResponse))
          case t                     => p.tryFailure(JavaClientExceptionWrapper(t))
        }
      })
      p.future
    }
  }
}
