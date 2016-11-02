package com.sksamuel.elastic4s

trait ElasticImplicits {
  implicit class RichString(index: String) {
    def /(`type`: String): IndexAndType = IndexAndType(index, `type`)
    def /(types: Iterable[String]): IndexAndTypes = IndexAndTypes(index, types.toSeq)
  }
}

object ElasticImplicits extends ElasticImplicits
