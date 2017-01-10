package com.sksamuel.elastic4s

import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.common.bytes.BytesReference

import scala.collection.mutable

/**
  * A common trait for Get, MultiGet, Search and MultiSearch API results so that
  * the HitReader typeclass can unmarshall results from any of those.
  */
trait Hit {

  def id: String
  def index: String
  def `type`: String
  def version: Long

  final def ref: DocumentRef = DocumentRef(index, `type`, id)

  final def to[T: HitReader]: T = safeTo[T].fold(e => throw e, t => t)
  final def safeTo[T](implicit reader: HitReader[T]): Either[Throwable, T] = reader.read(this)

  final def toOpt[T: HitReader]: Option[T] = if (exists) to[T].some else None
  final def safeToOpt[T: HitReader]: Option[Either[Throwable, T]] = if (exists) safeTo[T].some else None

  def sourceField(name: String): AnyRef = sourceAsMap(name)
  def sourceFieldOpt(name: String): Option[AnyRef] = sourceAsMap.get(name)

  def sourceAsMap: Map[String, AnyRef]
  def sourceAsBytes: Array[Byte]
  def sourceAsString: String
  def sourceAsByteRef: BytesReference
  def isSourceEmpty: Boolean

  def sourceAsMutableMap: mutable.Map[String, AnyRef] = mutable.Map.apply(sourceAsMap.toSeq: _*)

  def exists: Boolean
}
