package com.sksamuel.elastic4s.handlers

import com.sksamuel.elastic4s.{ElasticError, HttpResponse}

import scala.util.Try

object ElasticErrorParser {
  def parse(resp: HttpResponse): ElasticError = {
    resp.entity match {
      case Some(entity) =>
        Try(JacksonSupport.mapper.readTree(entity.content)).map { node =>
          if (node != null && node.has("error")) {
            val errorNode = node.get("error")
            JacksonSupport.mapper.readValue[ElasticError](JacksonSupport.mapper.writeValueAsBytes(errorNode))
          } else {
            ElasticError(resp.statusCode.toString, resp.statusCode.toString, None, None, None, Nil, None)
          }
        }.getOrElse(ElasticError(resp.statusCode.toString, resp.statusCode.toString, None, None, None, Nil, None))
      case _ =>
        ElasticError(resp.statusCode.toString, resp.statusCode.toString, None, None, None, Nil, None)
    }
  }
}
