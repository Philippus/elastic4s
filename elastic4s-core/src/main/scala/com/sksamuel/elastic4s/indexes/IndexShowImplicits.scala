package com.sksamuel.elastic4s.indexes

import cats.Show

trait IndexShowImplicits {

  implicit object IndexShow extends Show[IndexDefinition] {
    override def show(req: IndexDefinition): String = IndexContentBuilder(req).string()
  }

  implicit object CreateIndexShow extends Show[CreateIndexDefinition] {
    override def show(req: CreateIndexDefinition): String = CreateIndexContentBuilder(req).string()
  }
}
