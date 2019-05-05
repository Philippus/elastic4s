package com.sksamuel.elastic4s

import scala.language.implicitConversions

trait ElasticImplicits {
  @deprecated("use index / type not index -> type", "6.0.0")
  implicit def tupleToIndexAndType(tuple: (String, String)): IndexAndType = IndexAndType(tuple._1, tuple._2)
}

object ElasticImplicits extends ElasticImplicits
