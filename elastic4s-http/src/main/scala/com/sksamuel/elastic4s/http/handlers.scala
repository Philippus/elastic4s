package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.JsonFormat
import org.elasticsearch.client.Response

import scala.io.Source

// implementations know how to convert an apache http response into the appropriate response type
trait ResponseHandler[U] {
  def handle(response: Response): U
}

// a ResponseHandler that simply marshalls the body into the required type
// using an instance of JsonFormat
object ResponseHandler {
  def default[U](format: JsonFormat[U]): ResponseHandler[U] = new ResponseHandler[U] {
    override def handle(response: Response): U = {
      val body = Source.fromInputStream(response.getEntity.getContent).mkString
      format.fromJson(body)
    }
  }
}
