package com.sksamuel.elastic4s.http

import cats.Show
import com.sksamuel.elastic4s.{ElasticsearchClientUri, JsonFormat}
import com.sksamuel.exts.Logging
import org.apache.http.HttpHost
import org.elasticsearch.client.{Response, ResponseException, ResponseListener, RestClient}

import scala.concurrent.{Future, Promise}
import scala.io.Source
import scala.util.{Failure, Try}

trait HttpClient extends Logging {

  // returns the underlying java rest client
  def rest: RestClient

  // returns a String containing the Json of the request
  def show[T](request: T)(implicit show: Show[T]): String = show.show(request)

  /**
    * Executes the given request type T, and returns a Future of the response type U.
    *
    * In order to use this method an implicit `JsonFormat[U]` must be in scope. The easiest
    * way is to include one of the json modules, eg `elastic4s-jackson` or `elastic4s-circe` and
    * then import the implicit. Eg,
    *
    * `import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._`
    */
  def execute[T, U](request: T)
                   (implicit exec: HttpExecutable[T, U],
                    format: JsonFormat[U]): Future[U] = exec.execute(rest, request, format)

  def close(): Unit
}

object HttpClient extends Logging {

  /**
    * Creates a new HttpClient from an existing java RestClient.
    * Use this method if you wish to customize the way the rest client is created.
    *
    * @param client the Java client to wrap
    * @return newly created Scala client
    */
  def fromRestClient(client: RestClient): HttpClient = new HttpClient {
    override def close(): Unit = rest.close()
    // returns the underlying java rest client
    override def rest: RestClient = client
  }

  def apply(uri: ElasticsearchClientUri): HttpClient = {
    val hosts = uri.hosts.map { case (host, port) => new HttpHost(host, port, "http") }
    logger.info(s"Creating HTTP client on ${hosts.mkString(",")}")
    val client = RestClient.builder(hosts: _*).build()
    HttpClient.fromRestClient(client)
  }
}

/**
  * @tparam T the type of the request object handled by this handler
  * @tparam U the type of the response object returned by this handler
  */
trait HttpExecutable[T, U] extends Logging {

  def execute(client: RestClient, request: T, format: JsonFormat[U]): Future[U]

  protected def executeAsyncAndMapResponse(listener: ResponseListener => Any,
                                           format: JsonFormat[U]): Future[U] = {
    executeAsyncAndMapResponse(listener, format, defaultFailureHandler(format))
  }

  // convenience method that registers a listener with the function and the response json
  // is then marshalled into the type U
  protected def executeAsyncAndMapResponse(listener: ResponseListener => Any,
                                           format: JsonFormat[U],
                                           failureHandler: Exception => Try[U]): Future[U] = {
    val p = Promise[U]()
    listener(new ResponseListener {
      override def onSuccess(r: Response): Unit = {
        logger.debug(s"onSuccess $r")
        val result = Try {
          format.fromJson(Source.fromInputStream(r.getEntity.getContent).mkString)
        }
        p.tryComplete(result)
      }

      override def onFailure(e: Exception): Unit = p.tryComplete(failureHandler(e))
    })
    p.future
  }

  def defaultFailureHandler(format: JsonFormat[U])(e: Exception): Try[U] = {
    logger.debug(s"onFailure $e")
    Failure(e)
  }

  def parse404FailureHandler(format: JsonFormat[U])(e: Exception): Try[U] = {
    logger.debug(s"onFailure $e")
    e match {
      case re: ResponseException if re.getResponse.getStatusLine.getStatusCode == 404 =>
        Try {
          format.fromJson(Source.fromInputStream(re.getResponse.getEntity.getContent).mkString)
        }
      case _ => Failure(e)
    }
  }
}
