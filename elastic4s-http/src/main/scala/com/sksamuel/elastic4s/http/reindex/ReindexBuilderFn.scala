package com.sksamuel.elastic4s.http.reindex

import com.sksamuel.elastic4s.http.search.queries.{QueryBuilderFn, SortBuilderFn}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.reindex.ReindexDefinition

object ReindexBuilderFn {

  def apply(request: ReindexDefinition): XContentBuilder = {
    val builder = XContentFactory.obj()

    builder.startObject("source")


    request.size.foreach(builder.field("size", _))

    request.remoteHost.foreach { host =>
      builder.startObject("remote")
      builder.field("host", host)
      request.remoteUser.foreach(builder.field("username", _))
      request.remotePass.foreach(builder.field("password", _))
      request.remoteSocketTimeout.foreach(builder.field("socket_timeout", _))
      builder.endObject()
    }

    builder.array("index", request.sourceIndexes.array)

    if (request.targetType.nonEmpty)
      builder.field("type", request.targetType.get)

    request.filter.foreach(q => builder.rawField("query", QueryBuilderFn(q)))
    // end source
    builder.endObject()

    builder.startObject("dest")
    builder.field("index", request.targetIndex.name)
    // end dest
    builder.endObject()
  }
}
