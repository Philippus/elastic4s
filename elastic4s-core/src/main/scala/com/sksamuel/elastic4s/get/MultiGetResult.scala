package com.sksamuel.elastic4s.get

import org.elasticsearch.action.get.MultiGetResponse
import scala.collection.JavaConverters._

case class MultiGetResult(original: MultiGetResponse) {

  @deprecated("use responses", "3.0.0")
  def getResponses = responses

  def responses: Seq[MultiGetItemResult] = original.iterator.asScala.map(MultiGetItemResult.apply).toList
}
