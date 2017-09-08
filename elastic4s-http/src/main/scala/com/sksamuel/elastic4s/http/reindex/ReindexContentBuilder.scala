package com.sksamuel.elastic4s.http.reindex

import com.sksamuel.elastic4s.http.ScriptBuilderFn
import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.reindex.ReindexDefinition
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

object ReindexContentBuilder {
  def apply(request: ReindexDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject()

    builder.startObject("source")

    val indexAndTypes = request.sourceIndexes.toIndexesAndTypes
    builder.startArray("index")
    indexAndTypes.indexes.foreach { sourceIndex =>
      builder.rawValue(new BytesArray(sourceIndex), XContentType.JSON)
    }
    builder.endArray()

    if (indexAndTypes.types.nonEmpty) {
      builder.startArray("type")
      indexAndTypes.types.foreach { sourceType =>
        builder.rawValue(new BytesArray(sourceType), XContentType.JSON)
      }
      builder.endArray()
    }

    if (request.filter.nonEmpty) {
      builder.startObject("query")
      request.filter.map(QueryBuilderFn.apply).foreach(x =>
        builder.rawField("query", new BytesArray(x.string), XContentType.JSON))
      builder.endObject()
    }

    builder.endObject()

    builder.startObject("dest")
    builder.rawField("index", new BytesArray(request.targetIndex), XContentType.JSON)
    request.targetType.foreach { targetType =>
      builder.rawField("type", new BytesArray(targetType), XContentType.JSON)
    }
    builder.endObject()

    request.size.foreach(builder.field("size", _))

    request.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script).bytes, XContentType.JSON)
    }

    builder.endObject()
    builder
  }
}
