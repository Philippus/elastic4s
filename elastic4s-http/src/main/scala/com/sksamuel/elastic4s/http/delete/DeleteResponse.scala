package com.sksamuel.elastic4s.http.delete

import com.sksamuel.elastic4s.DocumentRef
import com.sksamuel.elastic4s.http.Shards

case class DeleteResponse(_shards: Shards,
                          found: Boolean,
                          private val _index: String,
                          private val _type: String,
                          private val _id: String,
                          private val _version: Long,
                          result: String) {
  def index: String = _index
  def `type`: String = _type
  def id: String = _id
  def version: Long = _version
  def ref = DocumentRef(index, `type`, id)
}
