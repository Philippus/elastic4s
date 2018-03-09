package com.sksamuel.elastic4s.http.bulk

import com.sksamuel.elastic4s.bulk.BulkDefinition
import com.sksamuel.elastic4s.delete.DeleteByIdDefinition
import com.sksamuel.elastic4s.http.index.VersionTypeHttpString
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
        builder.field("_index", index.indexAndType.index)
        builder.field("_type", index.indexAndType.`type`)
        index.id.foreach(id => builder.field("_id", id.toString))
        index.parent.foreach(builder.field("_parent", _))
        index.routing.foreach(builder.field("_routing", _))
        index.version.foreach(builder.field("version", _))
        index.versionType.foreach(versionType ⇒ builder.field("version_type", VersionTypeHttpString(versionType)))
        builder.endObject()
        builder.endObject()

        rows += builder.string
        rows += IndexContentBuilder(index).string()

      case delete: DeleteByIdDefinition =>
        val builder = XContentFactory.jsonBuilder()
        builder.startObject("delete")
        builder.field("_index", delete.indexType.index)
        builder.field("_type", delete.indexType.`type`)
        builder.field("_id", delete.id.toString)
        delete.parent.foreach(builder.field("_parent", _))
        delete.routing.foreach(builder.field("_routing", _))
        builder.endObject()
        builder.endObject()

        rows += builder.string

      case update: UpdateDefinition =>
        val builder = XContentFactory.jsonBuilder()
        builder.startObject("update")
        builder.field("_index", update.indexAndType.index)
        builder.field("_type", update.indexAndType.`type`)
        builder.field("_id", update.id)
        update.parent.foreach(builder.field("_parent", _))
        update.routing.foreach(builder.field("_routing", _))
        update.version.foreach(builder.field("version", _))
        update.versionType.foreach(builder.field("version_type", _))
        builder.endObject()
        builder.endObject()

        rows += builder.string
        rows += UpdateBuilderFn(update).string()
    }
    rows.result()
  }
}
