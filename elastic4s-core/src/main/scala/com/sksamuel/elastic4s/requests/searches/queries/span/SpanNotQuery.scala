package com.sksamuel.elastic4s.requests.searches.queries.span

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class SpanNotQuery(include: SpanQuery,
                        exclude: SpanQuery,
                        dist: Option[Int] = None,
                        pre: Option[Int] = None,
                        post: Option[Int] = None,
                        boost: Option[Double] = None,
                        queryName: Option[String] = None)
    extends Query {

  def boost(boost: Double): SpanNotQuery         = copy(boost = Option(boost))
  def queryName(queryName: String): SpanNotQuery = copy(queryName = queryName.some)
  def post(post: Int): SpanNotQuery              = copy(post = post.some)
  def pre(pre: Int): SpanNotQuery                = copy(pre = pre.some)
  def dist(dist: Int): SpanNotQuery              = copy(dist = dist.some)
}
