package com.sksamuel.elastic4s.requests.update

// contains the source if specified by the _source parameter
case class UpdateGet(found: Boolean, _source: Map[String, Any])
