package com.sksamuel.elastic4s.requests.cat

case class CatCount(indices: Seq[String] = Nil)
