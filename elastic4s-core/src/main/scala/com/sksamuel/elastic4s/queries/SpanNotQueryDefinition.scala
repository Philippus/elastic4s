package com.sksamuel.elastic4s.queries

import org.elasticsearch.index.query.QueryBuilders

class SpanNotQueryDefinition extends QueryDefinition {

  val builder = QueryBuilders.spanNotQuery()

  def boost(boost: Double): this.type = {
    builder.boost(boost.toFloat)
    this
  }

  def dist(dist: Int): this.type = {
    builder.dist(dist)
    this
  }

  def exclude(query: SpanQueryDefinition): this.type = {
    builder.exclude(query.builder)
    this
  }

  def include(query: SpanQueryDefinition): this.type = {
    builder.include(query.builder)
    this
  }

  def pre(pre: Int): this.type = {
    builder.pre(pre)
    this
  }

  def post(post: Int): this.type = {
    builder.post(post)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}
