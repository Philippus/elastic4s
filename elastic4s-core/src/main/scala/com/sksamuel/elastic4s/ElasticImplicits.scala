package com.sksamuel.elastic4s

trait ElasticImplicits {

  implicit class RichString(index: String) {
    def /(`type`: String): IndexAndType = IndexAndType(index, `type`)
    def /(types: Iterable[String]): IndexAndTypes = IndexAndTypes(index, types.toSeq)
  }

  // allows any string to be indexed as source
  implicit object StringIndexable extends Indexable[String] {
    override def json(js: String): String = js
  }
}

object ElasticImplicits extends ElasticImplicits
