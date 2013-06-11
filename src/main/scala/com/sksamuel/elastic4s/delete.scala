package com.sksamuel.elastic4s

/** @author Stephen Samuel */
case class DeleteReq(`type`: String, id: String, routing: Option[String] = None, version: Int = 0)
case class DeleteResponse(ok: Boolean, index: String, `type`: String, id: String, found: Boolean)