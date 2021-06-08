package com.sksamuel.elastic4s.handlers.reindex

import com.sksamuel.elastic4s.handlers
import com.sksamuel.elastic4s.handlers.searches.queries
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.reindex.ReindexRequest

object ReindexBuilderFn {

  def apply(request: ReindexRequest): XContentBuilder = {
    val builder = XContentFactory.obj()

    request.size.foreach(builder.field("size", _))

    request.script.foreach { script =>
      builder.rawField("script", handlers.script.ScriptBuilderFn(script))
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

    request.filter.foreach(q => builder.rawField("query", queries.QueryBuilderFn(q)))
    // end source
    builder.endObject()

    builder.startObject("dest")
    builder.field("index", request.targetIndex.name)

    if (request.targetType.nonEmpty)
      builder.field("type", request.targetType.get)

    request.proceedOnConflicts.foreach {
      case true => builder.field("conflicts", "proceed")
      case false => builder.field("conflicts", "abort")
    }

    // end dest
    builder.endObject()
  }
}
