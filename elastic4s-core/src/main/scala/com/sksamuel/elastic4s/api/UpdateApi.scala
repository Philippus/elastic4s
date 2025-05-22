package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.elastic4s.requests.update.{UpdateByQueryAsyncRequest, UpdateByQueryRequest, UpdateRequest}
import com.sksamuel.elastic4s.Index

trait UpdateApi {
  def updateById(index: Index, id: String): UpdateRequest = UpdateRequest(index.name, id)

  def updateByQuerySync(index: Index, query: Query): UpdateByQueryRequest = UpdateByQueryRequest(index.name, query)

  def updateByQueryAsync(index: Index, query: Query): UpdateByQueryAsyncRequest =
    UpdateByQueryAsyncRequest(index.name, query)
}
