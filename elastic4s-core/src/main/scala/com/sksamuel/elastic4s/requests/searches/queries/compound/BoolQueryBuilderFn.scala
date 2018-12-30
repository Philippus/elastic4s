package com.sksamuel.elastic4s.requests.searches.queries.compound

import com.sksamuel.elastic4s.requests.searches.queries.{BoolQuery, QueryBuilderFn}
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object BoolQueryBuilderFn {

  def apply(bool: BoolQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("bool")

    if (bool.must.nonEmpty) {
      builder.startArray("must")
      val musts = bool.must.map(QueryBuilderFn.apply).map(_.string).mkString(",")
      builder.rawValue(musts)
      builder.endArray()
    }

    if (bool.should.nonEmpty) {
      builder.startArray("should")
      val should = bool.should.map(QueryBuilderFn.apply).map(_.string).mkString(",")
      builder.rawValue(should)
      builder.endArray()
    }

    if (bool.not.nonEmpty) {
      builder.startArray("must_not")
      val nots = bool.not.map(QueryBuilderFn.apply).map(_.string).mkString(",")
      builder.rawValue(nots)
      builder.endArray()
    }

    if (bool.filters.nonEmpty) {
      builder.startArray("filter")
      val filters = bool.filters.map(QueryBuilderFn.apply).map(_.string).mkString(",")
      builder.rawValue(filters)
      builder.endArray()
    }

    bool.boost.foreach(builder.field("boost", _))
    bool.queryName.foreach(builder.field("_name", _))
    bool.minimumShouldMatch.foreach(builder.field("minimum_should_match", _))

    builder.endObject().endObject()
  }
}
