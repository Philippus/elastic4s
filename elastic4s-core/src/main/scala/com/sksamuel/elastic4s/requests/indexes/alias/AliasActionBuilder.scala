package com.sksamuel.elastic4s.requests.indexes.alias

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.alias.{AddAliasActionRequest, IndicesAliasesRequest, RemoveAliasAction}
import com.sksamuel.elastic4s.requests.searches.queries.QueryBuilderFn

object AliasActionBuilder {

  def apply(r: IndicesAliasesRequest): XContentBuilder = {
    val source = XContentFactory.jsonBuilder().startArray("actions")

    val actionsArray = r.actions
      .map {
        case addAction: AddAliasActionRequest => buildAddAction(addAction).string()
        case removeAction: RemoveAliasAction  => buildRemoveAction(removeAction).string()
      }
      .mkString(",")

    source.rawValue(actionsArray)

    source.endArray().endObject()
  }

  private def buildAddAction(addAction: AddAliasActionRequest): XContentBuilder = {
    val jsonBuilder = XContentFactory.jsonBuilder().startObject("add")

    jsonBuilder.field("index", addAction.index)
    jsonBuilder.field("alias", addAction.alias)

    addAction.filter.map(QueryBuilderFn(_)).foreach { queryBuilder =>
      jsonBuilder.rawField("filter", queryBuilder)
    }
    addAction.routing.foreach(jsonBuilder.field("routing", _))
    addAction.searchRouting.foreach(jsonBuilder.field("search_routing", _))
    addAction.indexRouting.foreach(jsonBuilder.field("index_routing", _))
    addAction.isWriteIndex.foreach(jsonBuilder.field("is_write_index", _))

    jsonBuilder.endObject().endObject()
  }

  private def buildRemoveAction(removeAction: RemoveAliasAction): XContentBuilder = {
    val jsonBuilder = XContentFactory.jsonBuilder().startObject("remove")

    jsonBuilder.field("index", removeAction.index)
    jsonBuilder.field("alias", removeAction.alias)

    removeAction.filter.map(QueryBuilderFn(_)).foreach { queryBuilder =>
      jsonBuilder.rawField("filter", queryBuilder)
    }
    removeAction.routing.foreach(jsonBuilder.field("routing", _))
    removeAction.searchRouting.foreach(jsonBuilder.field("search_routing", _))
    removeAction.indexRouting.foreach(jsonBuilder.field("index_routing", _))

    jsonBuilder.endObject().endObject()
    jsonBuilder
  }
}
