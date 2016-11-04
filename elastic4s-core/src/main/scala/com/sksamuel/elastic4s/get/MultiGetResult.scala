package com.sksamuel.elastic4s.get

import com.sksamuel.elastic4s.HitReader
import org.elasticsearch.action.get.MultiGetResponse
import org.elasticsearch.action.get.MultiGetResponse.Failure

import scala.collection.JavaConverters._

case class MultiGetResult(original: MultiGetResponse) {

  @deprecated("use responses", "3.0.0")
  def getResponses = responses

  def size = responses.size
  def responses: Seq[MultiGetItemResult] = original.iterator.asScala.map(MultiGetItemResult.apply).toList

  // marshall all documents returned
  def to[T: HitReader]: Seq[Either[String, T]] = responses.map(_.to[T])

  def successes: Seq[RichGetResponse] = responses.flatMap(_.responseOpt)
  def failures: Seq[Failure] = responses.flatMap(_.failureOpt)
}
