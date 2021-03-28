package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.json.builder.{ContentBuilder, ContentFactory}
import com.sksamuel.elastic4s.requests.count.CountRequest

/**
  * A typeclass that is used to build the json bodies for requests.
  *
  * They accept a request instance, such as CountRequest or SearchRequest, and return
  * the json compatible with elasticsearch.
  */
trait BodyBuilder[R] {
  def toJson(req: R, factory: ContentFactory): ContentBuilder
}

object CountBodyBuilder extends BodyBuilder[CountRequest] {
  override def toJson(req: CountRequest, factory: ContentFactory): ContentBuilder = {
    val builder = factory.builder()
    // req.query.map(QueryBuilderFn.apply).foreach(builder.rawField("query", _))
    builder
  }
}
