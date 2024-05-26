package com.sksamuel.elastic4s.handlers.termsenum

import com.sksamuel.elastic4s.handlers.searches.queries
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.termsenum.{TermsEnumRequest, TermsEnumResponse}
import com.sksamuel.elastic4s.{ElasticRequest, Handler, HttpEntity}

object TermsEnumBodyFn {
  def apply(request: TermsEnumRequest): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.field("field", request.field)
    request.string.foreach(builder.field("string", _))
    request.size.foreach(builder.field("size", _))
    request.timeout.foreach(builder.field("timeout", _))
    request.caseInsensitive.foreach(builder.field("case_insensitive", _))
    request.indexFilter.foreach(q => builder.rawField("index_filter", queries.QueryBuilderFn(q)))
    request.searchAfter.foreach(builder.field("search_after", _))
    builder.endObject()
  }
}

trait TermsEnumHandlers {
  implicit object TermsEnumHandler extends Handler[TermsEnumRequest, TermsEnumResponse] {
    override def build(request: TermsEnumRequest): ElasticRequest = {

      val endpoint = s"/${request.indexes.values.mkString(",")}/_terms_enum"

      val body   = TermsEnumBodyFn(request).string
      val entity = HttpEntity(body, "application/json")

      ElasticRequest("POST", endpoint, entity)
    }
  }
}
