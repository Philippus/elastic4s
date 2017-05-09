package com.sksamuel.elastic4s.searches

import org.elasticsearch.rest.RestStatus
import org.elasticsearch.script.mustache.SearchTemplateResponse

case class RichSearchTemplateResponse(original: SearchTemplateResponse) {
  def status: RestStatus = original.status()
  def hasResponse: Boolean = original.hasResponse
  def response = RichSearchResponse(original.getResponse)
}
