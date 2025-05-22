package com.sksamuel.elastic4s

import java.net.URLEncoder
import scala.language.implicitConversions

trait IndexesLike {
  def values: Seq[String]
  def toIndexes: Indexes = Indexes(values)
}

object IndexesLike {
  implicit def apply(indexes: String): IndexesLike           = Indexes(indexes.split(',').toIndexedSeq)
  def apply(first: String, rest: String*): IndexesLike       = Indexes(first +: rest)
  implicit def apply(indexes: Iterable[String]): IndexesLike = Indexes(indexes.toSeq)
}

trait IndexLike extends IndexesLike {
  def index: String

  override def values: Seq[String] = Seq(index)
}

object IndexLike {
  implicit def toIndex(str: String): IndexLike = Index(str)
}

case class Index(name: String) extends IndexLike {
  override def index: String = name
}

object Index {
  val _all                                 = Index("_all")
  val All: Index                           = _all
  implicit def toIndex(str: String): Index = Index(str)
}

/** Models one or more indexes, eg
  *   - "index1"
  *   - "index1,index2"
  *   - "_all"
  */
case class Indexes(values: Seq[String]) extends IndexesLike {
  def size: Int                                  = values.size
  def isEmpty: Boolean                           = values.isEmpty
  def isNonEmpty: Boolean                        = values.nonEmpty
  def isAll: Boolean                             = values == Seq("_all")
  def string(urlEncode: Boolean = false): String = {
    if (values.isEmpty) {
      "_all"
    } else {
      val indexNames = if (urlEncode) values.map(URLEncoder.encode(_, "UTF8").replace("+", "%20")) else values
      indexNames.mkString(",")
    }
  }
  def array: Array[String]                       = values.toArray
}

object Indexes {
  val All: Indexes                                       = Indexes("_all")
  val Empty: Indexes                                     = Indexes(Nil)
  implicit def apply(indexes: String): Indexes           = Indexes(indexes.split(',').toIndexedSeq)
  def apply(first: String, rest: String*): Indexes       = Indexes(first +: rest)
  implicit def apply(indexes: Iterable[String]): Indexes = Indexes(indexes.toSeq)
}
