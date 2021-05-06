package com.sksamuel.elastic4s

import scala.language.implicitConversions

trait IndexesLike {
  def values: Seq[String]
  def toIndexes: Indexes = Indexes(values)
}

object IndexesLike {
  implicit def apply(indexes: String): IndexesLike           = Indexes(indexes.split(','))
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

/**
  * Models one or more indexes, eg
  * - "index1"
  * - "index1,index2"
  * - "_all"
  */
case class Indexes(values: Seq[String]) extends IndexesLike {
  // returns an IndexesAndTypes where the types is empty
  def toIndexesAndTypes: IndexesAndTypes         = IndexesAndTypes(values, Nil)
  def size: Int                                  = values.size
  def isEmpty: Boolean                           = values.isEmpty
  def isNonEmpty: Boolean                        = values.nonEmpty
  def isAll: Boolean                             = values == Seq("_all")
  def string(urlEncode: Boolean = false): String = {
    if (values.isEmpty) {
      "_all"
    } else {
      val indexNames = if (urlEncode) values.map(ElasticUrlEncoder.encodeUrlFragment) else values
      indexNames.mkString(",")
    }
  }
  def array: Array[String]                       = values.toArray
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
case class IndexAndType(index: String, `type`: String) extends IndexLike {
  @deprecated("types are deprecated now", "7.0")
  def toIndexAndTypes: IndexAndTypes     = IndexAndTypes(index, Seq(`type`))
  @deprecated("types are deprecated now", "7.0")
  def toIndexesAndTypes: IndexesAndTypes = IndexesAndTypes(Seq(index), Seq(`type`))
}

@deprecated("types are deprecated now", "7.0")
object IndexAndType {
  @deprecated("types are deprecated now", "7.0")
  implicit def apply(str: String): IndexLike = str.split('/') match {
    case Array(index)      => Index(index)
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
case class IndexAndTypes(index: String, types: Seq[String]) extends IndexLike {
  @deprecated("types are deprecated now", "7.0")
  def toIndexesAndTypes = IndexesAndTypes(Seq(index), types)
}

@deprecated("types are deprecated now", "7.0")
object IndexAndTypes {
  @deprecated("types are deprecated now", "7.0")
  implicit def apply(string: String): IndexLike =
    string.split("/") match {
      case Array(index)    => Index(index)
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
case class IndexesAndTypes(indexes: Seq[String], types: Seq[String]) extends IndexesLike {
  override def values: Seq[String] = indexes
}

@deprecated("types are deprecated now", "7.0")
object IndexesAndTypes {

  @deprecated("types are deprecated now", "7.0")
  implicit def apply(string: String): IndexesLike =
    string.split("/") match {
      case Array(index)    => Indexes(index.split(","))
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
case class IndexesAndType(indexes: Seq[String], `type`: Option[String] = None)

object IndexesAndType {
  def apply(indexes: Seq[String], `type`: String): IndexesAndType = IndexesAndType(indexes, Option(`type`))

  @deprecated("types are deprecated now", "7.0")
  implicit def apply(indexesLike: IndexesLike): IndexesAndType =
    IndexesAndType(indexesLike.values, None)

  @deprecated("types are deprecated now", "7.0")
  implicit def apply(indexAndType: IndexAndType): IndexesAndType =
    IndexesAndType(Seq(indexAndType.index), indexAndType.`type`)
}
