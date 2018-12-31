package com.sksamuel.elastic4s

import java.net.URLEncoder

import scala.language.implicitConversions

case class Index(name: String) {
  def toIndexes: Indexes = Indexes(Seq(name))
}

object Index {
  val _all                                 = Index("_all")
  val All: Index                           = _all
  implicit def toIndex(str: String): Index = Index(str)
}

/**
  * Models one or more indexes, eg
  * - "index1"
  * - "index1,index2"
  * - "_all"
  */
case class Indexes(values: Seq[String]) {
  // returns an IndexesAndTypes where the types is empty
  def toIndexesAndTypes: IndexesAndTypes = IndexesAndTypes(values, Nil)
  def size: Int                          = values.size
  def isEmpty: Boolean                   = values.isEmpty
  def isNonEmpty: Boolean                = values.nonEmpty
  def isAll: Boolean                     = values == Seq("_all")
  def string: String                     = if (values.isEmpty) "_all" else values.map(URLEncoder.encode(_, "UTF8")).mkString(",")
  def array: Array[String]               = values.toArray
}

object Indexes {
  val All                                                = Indexes("_all")
  val Empty                                              = Indexes(Nil)
  implicit def apply(indexes: String): Indexes           = Indexes(indexes.split(','))
  def apply(first: String, rest: String*): Indexes       = Indexes(first +: rest)
  implicit def apply(indexes: Iterable[String]): Indexes = Indexes(indexes.toSeq)
}

/**
  * Models exactly one index associated with exactly one type.
  */
@deprecated("types are deprecated now", "7.0")
case class IndexAndType(index: String, `type`: String) {
  @deprecated("types are deprecated now", "7.0")
  def toIndexAndTypes: IndexAndTypes     = IndexAndTypes(index, Seq(`type`))
  @deprecated("types are deprecated now", "7.0")
  def toIndexesAndTypes: IndexesAndTypes = IndexesAndTypes(Seq(index), Seq(`type`))
}

@deprecated("types are deprecated now", "7.0")
object IndexAndType {
  @deprecated("types are deprecated now", "7.0")
  implicit def apply(str: String): IndexAndType = str.split('/') match {
    case Array(index, tpe) => IndexAndType(index, tpe)
    case _                 => sys.error(s"Could not parse '$str' into index/type")
  }
}

/**
  * Models one index associated with one or more types.
  *
  * So for example,
  * - index1/type1
  * - index1/type1,type2
  */
@deprecated("types are deprecated now", "7.0")
case class IndexAndTypes(index: String, types: Seq[String]) {
  @deprecated("types are deprecated now", "7.0")
  def toIndexesAndTypes = IndexesAndTypes(Seq(index), types)
}

@deprecated("types are deprecated now", "7.0")
object IndexAndTypes {
  @deprecated("types are deprecated now", "7.0")
  implicit def apply(string: String): IndexAndTypes =
    string.split("/") match {
      case Array(index)    => IndexAndTypes(index, Nil)
      case Array(index, t) => IndexAndTypes(index, t.split(","))
      case _               => sys.error(s"Could not parse '$string' into index/type1[,type2,...]")
    }
  @deprecated("types are deprecated now", "7.0")
  implicit def apply(indexAndType: IndexAndType): IndexAndTypes     = apply(indexAndType.index, indexAndType.`type`)
  @deprecated("types are deprecated now", "7.0")
  implicit def apply(index: String, `type`: String): IndexAndTypes  = IndexAndTypes(index, Seq(`type`))
  @deprecated("types are deprecated now", "7.0")
  implicit def apply(indexAndType: (String, String)): IndexAndTypes = apply(indexAndType._1, indexAndType._2)

}

/**
  * Models one or more indexes associated with zero or more types.
  *
  * So for example,
  * - index1
  * - index1/index2
  * - index1/type1
  * - index1/type1,type2
  * - index1,index2/type1
  * - index1,index2/type1,type2
  */
@deprecated("types are deprecated now", "7.0")
case class IndexesAndTypes(indexes: Seq[String], types: Seq[String])

@deprecated("types are deprecated now", "7.0")
object IndexesAndTypes {

  @deprecated("types are deprecated now", "7.0")
  implicit def apply(string: String): IndexesAndTypes =
    string.split("/") match {
      case Array(index)    => IndexesAndTypes(index.split(","), Nil)
      case Array(index, t) => IndexesAndTypes(index.split(","), t.split(","))
      case _               => sys.error(s"Could not parse '$string' into index1[,index2,...]/type1[,type2,...]")
    }

  @deprecated("types are deprecated now", "7.0")
  implicit def apply(indexAndType: IndexAndType): IndexesAndTypes = apply(indexAndType.index, indexAndType.`type`)

  // iterables of strings are assumed to be lists of indexes with no types
  @deprecated("types are deprecated now", "7.0")
  implicit def apply(indexes: String*): IndexesAndTypes          = apply(indexes)
  @deprecated("types are deprecated now", "7.0")
  implicit def apply(indexes: Iterable[String]): IndexesAndTypes = IndexesAndTypes(indexes.toSeq, Nil)

  // a tuple is assumed to be an index and a type
  @deprecated("types are deprecated now", "7.0")
  implicit def apply(indexAndType: (String, String)): IndexesAndTypes = apply(indexAndType._1, indexAndType._2)
  @deprecated("types are deprecated now", "7.0")
  implicit def apply(index: String, `type`: String): IndexesAndTypes  = IndexesAndTypes(Seq(index), Seq(`type`))

  @deprecated("types are deprecated now", "7.0")
  implicit def apply(indexAndTypes: IndexAndTypes): IndexesAndTypes = indexAndTypes.toIndexesAndTypes
}

// Models one ore more indexes associated with exactly one type
@deprecated("types are deprecated now", "7.0")
case class IndexesAndType(indexes: Seq[String], `type`: String)

object IndexesAndType {
  @deprecated("types are deprecated now", "7.0")
  implicit def apply(indexAndType: IndexAndType): IndexesAndType =
    IndexesAndType(Seq(indexAndType.index), indexAndType.`type`)
}
