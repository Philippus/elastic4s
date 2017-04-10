package com.sksamuel.elastic4s.http

import com.sksamuel.exts.Logging
import org.elasticsearch.client.RestClient

import scala.concurrent.Future

trait HttpClient2 {
  def rest: RestClient
  def execute[T, U](request: T)(implicit exec: HttpExec2[T, U]): Future[U] = exec.execute(rest, request)
}

object HttpClient2 extends Logging {
  def fromRestClient(client: RestClient): HttpClient2 = new HttpClient2 {
    override def rest: RestClient = client
  }
}
