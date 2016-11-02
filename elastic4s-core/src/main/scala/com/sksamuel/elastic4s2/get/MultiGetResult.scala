package com.sksamuel.elastic4s2.get

import org.elasticsearch.action.get.MultiGetResponse
import scala.collection.JavaConverters._

case class MultiGetResult(original: MultiGetResponse) {
  def responses: Seq[MultiGetItemResult] = original.iterator.asScala.map(MultiGetItemResult.apply).toList
}
