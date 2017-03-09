package com.sksamuel.elastic4s.http.index

import com.sksamuel.elastic4s.DocumentRef
import com.sksamuel.elastic4s.http.Shards

case class IndexResponse(private val _id: String,
                         private val _index: String,
                         private val _type: String,
                         private val _version: Long,
                         result: String,
                         private val forced_refresh: Boolean,
                         shards: Shards,
                         created: Boolean) {

  def index = _index
  def `type` = _type
  def id = _id
  def version = _version
  def ref = DocumentRef(index, `type`, id)
  def forceRefresh: Boolean = forced_refresh
}
