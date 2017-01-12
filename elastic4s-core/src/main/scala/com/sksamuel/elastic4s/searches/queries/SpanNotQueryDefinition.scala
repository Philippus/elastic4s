package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.exts.OptionImplicits._

case class SpanNotQueryDefinition(include: SpanQueryDefinition,
                                  exclude: SpanQueryDefinition,
                                  dist: Option[Int] = None,
                                  pre: Option[Int] = None,
                                  post: Option[Int] = None,
                                  boost: Option[Double] = None,
                                  queryName: Option[String] = None) extends QueryDefinition {

  def boost(boost: Double): SpanNotQueryDefinition = copy(boost = Option(boost))
  def queryName(queryName: String): SpanNotQueryDefinition = copy(queryName = queryName.some)
  def post(post: Int): SpanNotQueryDefinition = copy(post = post.some)
  def pre(pre: Int): SpanNotQueryDefinition = copy(pre = pre.some)
  def dist(dist: Int): SpanNotQueryDefinition = copy(dist = dist.some)
}
