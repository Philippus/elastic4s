package com.sksamuel.elastic4s.http.bulk

import com.sksamuel.elastic4s.bulk.BulkDefinition
import com.sksamuel.elastic4s.delete.DeleteByIdDefinition
import com.sksamuel.elastic4s.http.update.UpdateBuilderFn
import com.sksamuel.elastic4s.indexes.{IndexContentBuilder, IndexDefinition}
import com.sksamuel.elastic4s.json.XContentFactory
import com.sksamuel.elastic4s.update.UpdateDefinition

object BulkBuilderFn {

  def apply(bulk: BulkDefinition): Seq[String] = {
    val rows = List.newBuilder[String]
    bulk.requests.foreach {
      case index: IndexDefinition =>

        val builder = XContentFactory.jsonBuilder()
        builder.startObject("index")
        builder.field("_index", index.indexAndTypes.index)
        builder.field("_type", index.indexAndTypes.types.headOption.getOrElse(index.indexAndTypes.index))
        index.id.foreach(id => builder.field("_id", id.toString))
        index.parent.foreach(builder.field("_parent", _))
        index.routing.foreach(builder.field("_routing", _))
        builder.endObject()
        builder.endObject()

        rows += builder.string
        rows += IndexContentBuilder(index).string()

      case delete: DeleteByIdDefinition =>

        val builder = XContentFactory.jsonBuilder()
        builder.startObject("delete")
        builder.field("_index", delete.indexType.index)
        delete.indexType.types.headOption.foreach(builder.field("_type", _))
        builder.field("_id", delete.id.toString)
        delete.parent.foreach(builder.field("_parent", _))
        delete.routing.foreach(builder.field("_routing", _))
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
        update.routing.foreach(builder.field("_routing", _))
        builder.endObject()
        builder.endObject()

        rows += builder.string
        rows += UpdateBuilderFn(update).string()
    }
    rows.result()
  }
}
