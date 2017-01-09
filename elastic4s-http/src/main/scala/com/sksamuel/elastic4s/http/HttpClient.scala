package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.{ElasticsearchClientUri, JsonFormat}
import com.sksamuel.exts.Logging
import org.apache.http.HttpHost
import org.elasticsearch.client.{Response, ResponseListener, RestClient}

import scala.concurrent.{Future, Promise}
import scala.io.Source
import scala.util.control.NonFatal

trait HttpClient extends Logging {
  // returns the underlying java rest client
  def rest: RestClient

  def execute[T, U](request: T)(implicit executable: HttpExecutable[T, U], format: JsonFormat[U]): Future[U] = {
    logger.debug(s"Executing $request")
    try {
      val fn = executable.execute(rest, request)
      val p = Promise[U]()
      fn(new ResponseListener {
        override def onSuccess(r: Response): Unit = {
          logger.debug(s"onSuccess $r")
          try {
            val u = format.fromJson(Source.fromInputStream(r.getEntity.getContent).mkString)
            p.trySuccess(u)
          } catch {
            case e: Throwable =>
              p.tryFailure(e)
          }
        }
        override def onFailure(e: Exception): Unit = {
          logger.debug(s"onFailure $e")
          p.tryFailure(e)
        }
      }
      )
      p.future
    }
    catch {
      case NonFatal(e) => Future.failed(e)
    }
  }

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
    val hosts = uri.hosts.map { case (host, port) => new HttpHost("localhost", 9200, "http") }
    logger.info(s"Creating HTTP client on ${hosts.mkString(",")}")
    val client = RestClient.builder(hosts: _*).build()
    HttpClient.fromRestClient(client)
  }
}


/**
  *
  * @tparam T the type of the request object handled by this builder
  */
trait HttpExecutable[T, U] extends Logging {
  def execute(client: RestClient, request: T): ResponseListener => Any
}
