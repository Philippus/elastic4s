package com.sksamuel.elastic4s.http.search.queries.compound

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.BoolQueryDefinition

object BoolQueryBuilderFn {

  def apply(bool: BoolQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject().startObject("bool")

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
    bool.disableCoord.foreach(builder.field("disable_coord", _))
    bool.queryName.foreach(builder.field("_name", _))
    bool.minimumShouldMatch.foreach(builder.field("minimum_should_match", _))

    builder.endObject().endObject()
  }
}
