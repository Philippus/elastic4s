package com.sksamuel.elastic4s.http.index

import com.sksamuel.elastic4s.Show
import com.sksamuel.elastic4s.indexes.{CreateIndexRequest, IndexContentBuilder, IndexRequest}

trait IndexShowImplicits {

  implicit object IndexShow extends Show[IndexRequest] {
    override def show(req: IndexRequest): String = IndexContentBuilder(req).string()
  }

  implicit object CreateIndexShow extends Show[CreateIndexRequest] {
    override def show(req: CreateIndexRequest): String = CreateIndexContentBuilder(req).string()
  }
}
