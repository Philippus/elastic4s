package com.sksamuel.elastic4s.http.bulk

import com.sksamuel.elastic4s.bulk.BulkDefinition
import com.sksamuel.elastic4s.delete.DeleteByIdDefinition
import com.sksamuel.elastic4s.http.update.UpdateContentBuilder
import com.sksamuel.elastic4s.indexes.{IndexContentBuilder, IndexDefinition}
import com.sksamuel.elastic4s.json.XContentFactory
import com.sksamuel.elastic4s.update.UpdateDefinition

object BulkContentBuilder {

  def apply(bulk: BulkDefinition): Seq[String] = {
    val rows = List.newBuilder[String]
    bulk.requests.foreach {
      case index: IndexDefinition =>

        val builder = XContentFactory.jsonBuilder()
        builder.startObject("index")
        builder.field("_index", index.indexAndType.index)
        builder.field("_type", index.indexAndType.`type`)
        index.id.foreach(id => builder.field("_id", id))
        index.parent.foreach(builder.field("_parent", _))
        builder.endObject()
        builder.endObject()

        rows += builder.string
        rows += IndexContentBuilder(index).string()

      case delete: DeleteByIdDefinition =>

        val builder = XContentFactory.jsonBuilder()
        builder.startObject("delete")
        builder.field("_index", delete.indexType.index)
        builder.field("_type", delete.indexType.`type`)
        builder.field("_id", delete.id)
        delete.parent.foreach(builder.field("_parent", _))
        builder.endObject()
        builder.endObject()

        rows += builder.string

      case update: UpdateDefinition =>

        val builder = XContentFactory.jsonBuilder()
        builder.startObject("update")
        builder.field("_index", update.indexAndTypes.index)
        builder.field("_type", update.indexAndTypes.types.head)
        builder.field("_id", update.id)
        update.parent.foreach(builder.field("_parent", _))
        builder.endObject()
        builder.endObject()

        rows += builder.string
        rows += UpdateContentBuilder(update).string()
    }
    rows.result()
  }
}
