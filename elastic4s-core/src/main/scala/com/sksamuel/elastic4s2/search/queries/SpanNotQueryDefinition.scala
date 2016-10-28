package com.sksamuel.elastic4s2.search.queries

import com.sksamuel.elastic4s2.search.QueryDefinition
import org.elasticsearch.index.query.QueryBuilders

case class SpanNotQueryDefinition(include: SpanQueryDefinition,
                                  exclude: SpanQueryDefinition) extends QueryDefinition {

  val builder = QueryBuilders.spanNotQuery(include.builder, exclude.builder)

  def boost(boost: Double): this.type = {
    builder.boost(boost.toFloat)
    this
  }

  def dist(dist: Int): this.type = {
    builder.dist(dist)
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
