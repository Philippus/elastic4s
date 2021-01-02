package com.sksamuel.elastic4s.requests.indexes.analyze

import com.sksamuel.elastic4s.{ElasticError, HttpResponse, ResponseHandler}

object AnalyzeResponseHandler extends ResponseHandler[AnalyzeResponse] {
  /**
   * Accepts a HttpResponse and returns an Either of an ElasticError or a type specific to the request
   * as determined by the instance of this handler.
   */
  override def handle(response: HttpResponse): Either[ElasticError, AnalyzeResponse] = {
    response.statusCode match {
      case 200 | 201 | 202 | 203 | 204 =>
        val entity = response.entity.getOrElse(sys.error("No entity defined"))
        val jsonNode = ResponseHandler.json(entity)
        jsonNode.get("detail") match {
          case nonNull if nonNull != null =>
            Right(ResponseHandler.fromNode[ExplainAnalyzeResponse](jsonNode))
          case _ =>
            Right(ResponseHandler.fromNode[NoExplainAnalyzeResponse](jsonNode))
        }
      case _ =>
        Left(ElasticError.parse(response))
    }
  }
}
