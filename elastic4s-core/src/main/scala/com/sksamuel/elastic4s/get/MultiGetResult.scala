package com.sksamuel.elastic4s.get

import com.sksamuel.elastic4s.HitReader
import org.elasticsearch.action.get.MultiGetResponse
import org.elasticsearch.action.get.MultiGetResponse.Failure
import cats.syntax.either._

import scala.collection.JavaConverters._

case class MultiGetResult(original: MultiGetResponse) {

  @deprecated("use responses", "5.0.0")
  def getResponses = items

  def size = items.size
  def items: Seq[MultiGetItemResult] = original.iterator.asScala.map(MultiGetItemResult.apply).toList

  def to[T: HitReader]: Seq[T] = safeTo[T].flatMap(_.toOption)
  def safeTo[T: HitReader]: Seq[Either[String, T]] = items.map(_.safeTo)

  // returns only those items which were successful
  def successes: Seq[RichGetResponse] = items.flatMap(_.responseOpt)

  // returns only those items which failed
  def failures: Seq[Failure] = items.flatMap(_.failureOpt)
}
