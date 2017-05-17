package com.sksamuel.elastic4s

import scala.language.implicitConversions

trait ElasticImplicits {
  implicit class RichString(index: String) {
    def /(`type`: String): IndexAndType = IndexAndType(index, `type`)
    def /(types: Iterable[String]): IndexAndTypes = IndexAndTypes(index, types.toSeq)
  }
}

object ElasticImplicits extends ElasticImplicits
