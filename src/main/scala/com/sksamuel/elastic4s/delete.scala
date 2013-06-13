package com.sksamuel.elastic4s

/** @author Stephen Samuel */
case class DeleteReq(index: String,
                     `type`: String,
                     id: String,
                     refresh: Boolean = false,
                     routing: Option[String] = None,
                     version: Int = 0,
                     parent: Option[String] = null)

case class DeleteByQueryReq(indexes: Seq[String], types: Seq[String], query: Query, routing: Seq[String] = Nil)