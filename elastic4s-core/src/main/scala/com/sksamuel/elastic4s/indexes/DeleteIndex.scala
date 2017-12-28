package com.sksamuel.elastic4s.indexes

@deprecated("This class is now renamed to DeleteIndex", "6.1.2")
class DeleteIndexDefinition(indexes: Seq[String]) extends DeleteIndex(indexes)

case class DeleteIndex(indexes: Seq[String])
