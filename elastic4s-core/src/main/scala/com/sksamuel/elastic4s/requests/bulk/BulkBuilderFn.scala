package com.sksamuel.elastic4s.requests.bulk

import com.sksamuel.elastic4s.XContentFactory
import com.sksamuel.elastic4s.requests.delete.DeleteByIdRequest
import com.sksamuel.elastic4s.requests.indexes.{IndexContentBuilder, IndexRequest, VersionTypeHttpString}
import com.sksamuel.elastic4s.requests.update.{UpdateBuilderFn, UpdateRequest}

object BulkBuilderFn {

  def apply(bulk: BulkRequest): Seq[String] = {
    val rows = List.newBuilder[String]
    bulk.requests.foreach {
      case index: IndexRequest =>
        val builder       = XContentFactory.jsonBuilder()
        val createOrIndex = if (index.createOnly.getOrElse(false)) "create" else "index"
        builder.startObject(createOrIndex)
        builder.field("_index", index.indexAndType.index)
        builder.field("_type", index.indexAndType.`type`)
        index.id.foreach(id => builder.field("_id", id.toString))
        index.parent.foreach(builder.field("_parent", _))
        index.routing.foreach(builder.field("routing", _))
        index.version.foreach(builder.field("version", _))
        index.versionType.foreach(versionType ⇒ builder.field("version_type", VersionTypeHttpString(versionType)))
        index.pipeline.foreach(builder.field("pipeline", _))
        builder.endObject()
        builder.endObject()

        rows += builder.string
        rows += IndexContentBuilder(index).string()

      case delete: DeleteByIdRequest =>
        val builder = XContentFactory.jsonBuilder()
        builder.startObject("delete")
        builder.field("_index", delete.indexType.index)
        builder.field("_type", delete.indexType.`type`)
        builder.field("_id", delete.id.toString)
        delete.parent.foreach(builder.field("_parent", _))
        delete.routing.foreach(builder.field("_routing", _))
        delete.version.foreach(builder.field("version", _))
        delete.versionType.foreach(versionType ⇒ builder.field("version_type", VersionTypeHttpString(versionType)))
        builder.endObject()
        builder.endObject()

        rows += builder.string

      case update: UpdateRequest =>
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
