package com.sksamuel.elastic4s.searches

import org.elasticsearch.script.mustache.SearchTemplateResponse

case class RichSearchTemplateResponse(original: SearchTemplateResponse) {
  def status = original.status()
  def hasResponse = original.hasResponse
  def response = RichSearchResponse(original.getResponse)
}
