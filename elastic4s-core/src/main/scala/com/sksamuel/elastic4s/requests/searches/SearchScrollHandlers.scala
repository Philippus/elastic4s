package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.{ElasticError, ElasticRequest, Handler, HttpEntity, HttpResponse, ResponseHandler, XContentBuilder, XContentFactory}

case class ClearScrollResponse(succeeded: Boolean, num_freed: Int)

trait SearchScrollHandlers {

  implicit object ClearScrollHandler extends Handler[ClearScrollRequest, ClearScrollResponse] {

    override def responseHandler: ResponseHandler[ClearScrollResponse] = new ResponseHandler[ClearScrollResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, ClearScrollResponse] =
        response.statusCode match {
          case 200 =>
            Right(ResponseHandler.fromResponse[ClearScrollResponse](response))
          case _ =>
            Left(ElasticError.parse(response))
        }
    }

    override def build(request: ClearScrollRequest): ElasticRequest = {

      val (method, endpoint) = ("DELETE", s"/_search/scroll/")

      val body = ClearScrollContentFn(request).string()
      logger.debug("Executing clear scroll: " + body)
      val entity = HttpEntity(body, "application/json")

      ElasticRequest(method, endpoint, entity)
    }
  }

  implicit object SearchScrollHandler extends Handler[SearchScrollRequest, SearchResponse] {

    override def responseHandler: ResponseHandler[SearchResponse] = new ResponseHandler[SearchResponse] {
      override def handle(response: HttpResponse): Either[ElasticError, SearchResponse] = response.statusCode match {
        case 200 => Right(ResponseHandler.fromResponse[SearchResponse](response))
        case _   => Left(ElasticError.parse(response))
      }
    }

    override def build(req: SearchScrollRequest): ElasticRequest = {

      val body = SearchScrollBuilderFn(req).string()
      logger.debug("Executing search scroll: " + body)
      val entity = HttpEntity(body, "application/json")

      ElasticRequest("POST", "/_search/scroll", entity)
    }
  }
}

object SearchScrollBuilderFn {
  def apply(req: SearchScrollRequest): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    req.keepAlive.foreach(builder.field("scroll", _))
    builder.field("scroll_id", req.id)
    builder.endObject()
  }
}

object ClearScrollContentFn {
  def apply(req: ClearScrollRequest): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.array("scroll_id", req.ids.toArray)
    builder.endObject()
  }
}
