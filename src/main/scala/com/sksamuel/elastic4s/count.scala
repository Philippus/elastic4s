package com.sksamuel.elastic4s

/** @author Stephen Samuel */
case class CountReq(indexes: Seq[String], types: Seq[String], routing: Seq[String] = Nil)
