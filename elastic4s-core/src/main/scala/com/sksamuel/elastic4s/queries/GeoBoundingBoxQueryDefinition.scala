package com.sksamuel.elastic4s.queries

import org.elasticsearch.index.query.QueryBuilders

case class GeoBoundingBoxQueryDefinition(field: String)
  extends QueryDefinition {

  val builder = QueryBuilders.geoBoundingBoxQuery(field)
  val _builder = builder

  private var _left: Double = _
  private var _top: Double = _
  private var _right: Double = _
  private var _bottom: Double = _
  private var _type: String = _

  def `type`(`type`: String): GeoBoundingBoxQueryDefinition = {
    _type = `type`
    builder.`type`(_type)
    this
  }

  def left(left: Double): GeoBoundingBoxQueryDefinition = {
    _left = left
    builder.topLeft(_top, _left)
    this
  }

  def top(top: Double): GeoBoundingBoxQueryDefinition = {
    _top = top
    builder.topLeft(_top, _left)
    this
  }

  def right(right: Double): GeoBoundingBoxQueryDefinition = {
    _right = right
    builder.bottomRight(_bottom, _right)
    this
  }

  def bottom(bottom: Double): GeoBoundingBoxQueryDefinition = {
    _bottom = bottom
    builder.bottomRight(_bottom, _right)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}
