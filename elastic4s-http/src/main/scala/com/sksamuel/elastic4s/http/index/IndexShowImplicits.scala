package com.sksamuel.elastic4s.http.index

import cats.Show
import com.sksamuel.elastic4s.indexes.{CreateIndexDefinition, IndexContentBuilder, IndexDefinition}

trait IndexShowImplicits {

  implicit object IndexShow extends Show[IndexDefinition] {
    override def show(req: IndexDefinition): String = IndexContentBuilder(req).string()
  }

  implicit object CreateIndexShow extends Show[CreateIndexDefinition] {
    override def show(req: CreateIndexDefinition): String = CreateIndexContentBuilder(req).string()
  }
}
