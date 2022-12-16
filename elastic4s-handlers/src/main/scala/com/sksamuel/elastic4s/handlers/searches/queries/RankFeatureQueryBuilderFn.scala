package com.sksamuel.elastic4s.handlers.searches.queries

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.RankFeatureQuery

object RankFeatureQueryBuilderFn {
  def apply(q: RankFeatureQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("rank_feature")
    builder.field("field", q.field)
    q.boost.foreach(builder.field("boost", _))
    q.saturation.foreach { s =>
      builder.startObject("saturation")
      s.pivot.foreach(builder.field("pivot", _))
      builder.endObject()
    }
    q.log.foreach { l =>
      builder.startObject("log")
      builder.field("scaling_factor", l.scalingFactor)
      builder.endObject()
    }
    q.sigmoid.foreach { s =>
      builder.startObject("sigmoid")
      builder.field("pivot", s.pivot)
      builder.field("exponent", s.exponent)
      builder.endObject()
    }
    q.linear.foreach { _ =>
      builder.rawField("linear", "{}")
    }
    builder.endObject()
    builder
  }
}
