package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.requests.common.DocumentRef
import com.sksamuel.elastic4s.ext.OptionImplicits._

import java.nio.ByteBuffer
import scala.collection.mutable
import scala.util.Try

/** A common trait for Get, MultiGet, Search and MultiSearch results to provide common functionality for the
  * [[HitReader]] typeclass.
  */
trait Hit {

  def id: String
  def index: String
  def version: Long
  def seqNo: Long
  def primaryTerm: Long

  def sort: Option[Seq[AnyRef]]

  final def ref: DocumentRef = DocumentRef(index, id)

  /** Uses a HitReader typeclass to convert the returned source into type T. This method will throw an exception if the
    * marshalling process fails
    */
  final def to[T: HitReader]: T = safeTo[T].get

  /** Uses a HitReader typeclass to convert the returned source into type T. This method will return a Left[Throwable]
    * if there was an error during the marshalling, otherwise it will return a Right[T]
    */
  final def safeTo[T](implicit reader: HitReader[T]): Try[T] = reader.read(this)

  /** Returns a Some(t) if the hit exists. It might not exist (an empty hit) if this was returned by a Get request and
    * the given id did not exist, in which case it will return None.
    */
  final def toOpt[T: HitReader]: Option[T]          = if (exists) to[T].some else None
  final def safeToOpt[T: HitReader]: Option[Try[T]] = if (exists) safeTo[T].some else None

  final def sourceField(name: String): AnyRef            = sourceAsMap(name)
  final def sourceFieldOpt(name: String): Option[AnyRef] = sourceAsMap.get(name)
  final def sourceAsBytes: Array[Byte]                   = sourceAsString.getBytes("UTF8")

  def sourceAsString: String
  def sourceAsMap: Map[String, AnyRef]

  final def sourceAsMutableMap: mutable.Map[String, AnyRef] = mutable.Map.apply(sourceAsMap.toSeq: _*)
  final def sourceAsByteBuffer: ByteBuffer                  = ByteBuffer.wrap(sourceAsBytes)
  final def isSourceEmpty: Boolean                          = sourceAsMap.isEmpty

  def exists: Boolean
  def score: Float
}
