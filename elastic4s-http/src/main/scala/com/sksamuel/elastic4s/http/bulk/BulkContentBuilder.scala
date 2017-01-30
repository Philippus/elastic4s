package com.sksamuel.elastic4s.http.bulk

import com.sksamuel.elastic4s.bulk.BulkDefinition
import com.sksamuel.elastic4s.delete.DeleteByIdDefinition
import com.sksamuel.elastic4s.http.index.IndexContentBuilder
import com.sksamuel.elastic4s.http.update.UpdateContentBuilder
import com.sksamuel.elastic4s.indexes.IndexDefinition
import com.sksamuel.elastic4s.update.UpdateDefinition
import org.elasticsearch.common.xcontent.XContentFactory

object BulkContentBuilder {

  def apply(bulk: BulkDefinition): Seq[String] = {
    val rows = List.newBuilder[String]
    bulk.requests.foreach {
      case index: IndexDefinition =>

        val builder = XContentFactory.jsonBuilder()
        builder.startObject()
        builder.startObject("index")
        builder.field("_index", index.indexAndType.index)
        builder.field("_type", index.indexAndType.`type`)
        index.id.foreach(id => builder.field("_id", id))
        builder.endObject()
        builder.endObject()

        rows += builder.string
        rows += IndexContentBuilder(index).string()

      case delete: DeleteByIdDefinition =>

        val builder = XContentFactory.jsonBuilder()
        builder.startObject()
        builder.startObject("delete")
        builder.field("_index", delete.indexType.index)
        builder.field("_type", delete.indexType.`type`)
        builder.field("_id", delete.id)
        builder.endObject()
        builder.endObject()

        rows += builder.string

      case update: UpdateDefinition =>

        val builder = XContentFactory.jsonBuilder()
        builder.startObject()
        builder.startObject("update")
        builder.field("_index", update.indexAndTypes.index)
        builder.field("_type", update.indexAndTypes.types.head)
        builder.field("_id", update.id)
        builder.endObject()
        builder.endObject()

        rows += builder.string
        rows += UpdateContentBuilder(update).string()
    }
    rows.result()
  }
}
