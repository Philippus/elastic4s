package com.sksamuel.elastic4s.queries

import org.elasticsearch.index.query.QueryBuilders

case class TermsLookupQueryDefinition(field: String)
  extends QueryDefinition {

  val builder = QueryBuilders.termsLookupQuery(field)
  val _builder = builder

  def queryName(name: String): this.type = {
    builder.queryName(name)
    this
  }

  def index(index: String): this.type = {
    builder.lookupIndex(index)
    this
  }

  def lookupType(`type`: String): this.type = {
    builder.lookupType(`type`)
    this
  }

  def id(id: String): this.type = {
    builder.lookupId(id)
    this
  }

  def path(path: String): this.type = {
    builder.lookupPath(path)
    this
  }

  def routing(routing: String): this.type = {
    builder.lookupRouting(routing)
    this
  }
}
