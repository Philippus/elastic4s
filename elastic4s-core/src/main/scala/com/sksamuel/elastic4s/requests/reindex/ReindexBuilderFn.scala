package com.sksamuel.elastic4s.requests.reindex

import com.sksamuel.elastic4s.requests.script.ScriptBuilderFn
import com.sksamuel.elastic4s.requests.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object ReindexBuilderFn {

  def apply(request: ReindexRequest): XContentBuilder = {
    val builder = XContentFactory.obj()

    request.size.foreach(builder.field("size", _))

    request.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script))
    }

    builder.startObject("source")

    request.remoteHost.foreach { host =>
      builder.startObject("remote")
      builder.field("host", host)
      request.remoteUser.foreach(builder.field("username", _))
      request.remotePass.foreach(builder.field("password", _))
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
