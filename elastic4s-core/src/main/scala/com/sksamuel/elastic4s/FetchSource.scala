package com.sksamuel.elastic4s

case class FetchSource(enabled: Boolean, includes: Seq[String], excludes: Seq[String])
