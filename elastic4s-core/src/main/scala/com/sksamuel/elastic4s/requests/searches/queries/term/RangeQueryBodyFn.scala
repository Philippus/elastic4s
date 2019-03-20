package com.sksamuel.elastic4s.requests.searches.queries.term

import com.sksamuel.elastic4s.requests.searches.queries.RangeQuery
import com.sksamuel.elastic4s.{ElasticDate, XContentBuilder, XContentFactory}

object RangeQueryBodyFn {

  def apply(range: RangeQuery): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("range").startObject(range.field)

    range.gte.foreach {
      case x: Long           => builder.field("gte", x)
      case x: Double         => builder.field("gte", x)
      case x: String         => builder.field("gte", x)
      case date: ElasticDate => builder.field("gte", date.show)
    }

    range.lte.foreach {
      case x: Long           => builder.field("lte", x)
      case x: Double         => builder.field("lte", x)
      case x: String         => builder.field("lte", x)
      case date: ElasticDate => builder.field("lte", date.show)
    }

    range.gt.foreach {
      case x: Long           => builder.field("gt", x)
      case x: Double         => builder.field("gt", x)
      case x: String         => builder.field("gt", x)
      case date: ElasticDate => builder.field("gt", date.show)
    }

    range.lt.foreach {
      case x: Long           => builder.field("lt", x)
      case x: Double         => builder.field("lt", x)
      case x: String         => builder.field("lt", x)
      case date: ElasticDate => builder.field("lt", date.show)
    }

    range.format.foreach(builder.field("format", _))
    range.boost.foreach(builder.field("boost", _))
    range.timeZone.foreach(builder.field("time_zone", _))
    range.queryName.foreach(builder.field("_name", _))
    range.relation.map(_.getClass.getSimpleName.toUpperCase.stripSuffix("$")).foreach(builder.field("relation", _))

    builder.endObject().endObject().endObject()
  }
}
