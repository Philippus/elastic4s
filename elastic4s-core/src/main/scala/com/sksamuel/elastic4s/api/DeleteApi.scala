package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.delete.{DeleteByIdRequest, DeleteByQueryRequest}
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.elastic4s.Index

trait DeleteApi {
  def deleteById(index: Index, id: String): DeleteByIdRequest         = DeleteByIdRequest(index.name, id)
  def deleteByQuery(index: Index, query: Query): DeleteByQueryRequest = DeleteByQueryRequest(index.name, query)
}
