package com.sksamuel.elastic4s

import scala.language.implicitConversions

trait ElasticImplicits {

  @deprecated("use index / type not index -> type")
  implicit def tupleToIndexAndType(tuple: (String, String)): IndexAndType = IndexAndType(tuple._1, tuple._2)

  implicit class RichString(index: String) {
    def /(`type`: String): IndexAndType = IndexAndType(index, `type`)
    def /(types: Iterable[String]): IndexAndTypes = IndexAndTypes(index, types.toSeq)
  }
}

object ElasticImplicits extends ElasticImplicits
