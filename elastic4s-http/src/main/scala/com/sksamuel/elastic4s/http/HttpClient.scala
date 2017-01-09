package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.{AbstractElasticClient, ElasticsearchClientUri, JsonFormat}
import com.sksamuel.exts.Logging
import org.apache.http.HttpHost
import org.elasticsearch.client.{Client, Response, ResponseListener, RestClient}
import org.elasticsearch.{ElasticsearchException, ElasticsearchWrapperException}

import scala.concurrent.{Future, Promise}
import scala.io.Source

trait HttpClient extends AbstractElasticClient with Logging {

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
      case e: ElasticsearchException => Future.failed(e)
      case e: ElasticsearchWrapperException => Future.failed(e)
    }
  }
}

object HttpClient {

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
    // return the underlying Java TCP client
    override def java: Client = ???
  }

  def apply(uri: ElasticsearchClientUri): HttpClient = {
    val hosts = uri.hosts.map { case (host, port) => new HttpHost("localhost", 9200, "http") }
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
