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
    if (indexAndTypes.indexes.size > 1) {
      builder.startArray("index")
      indexAndTypes.indexes.foreach { sourceIndex =>
        builder.value(sourceIndex)
      }
      builder.endArray()
    } else {
      indexAndTypes.indexes.foreach { sourceIndex =>
        builder.field("index", sourceIndex)
      }
    }

    if (indexAndTypes.types.size > 1) {
      builder.startArray("type")
      indexAndTypes.types.foreach { sourceType =>
        builder.value(sourceType)
      }
      builder.endArray()

    } else {
      indexAndTypes.types.foreach { sourceType =>
        builder.field("type", sourceType)
      }
    }

    if (request.filter.nonEmpty) {
      builder.startObject("query")
      request.filter.map(QueryBuilderFn.apply).foreach(x =>
        builder.rawField("query", new BytesArray(x.string), XContentType.JSON))
      builder.endObject()
    }

    builder.endObject()

    builder.startObject("dest")
    builder.field("index", request.targetIndex)
    request.targetType.foreach { targetType =>
      builder.field("type", targetType)
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
