package com.sksamuel.elastic4s

trait ElasticImplicits {
  implicit class RichString(str: String) {
    def /(`type`: String): IndexAndTypes = IndexAndTypes(str, `type`)
  }
}

object ElasticImplicits extends ElasticImplicits
