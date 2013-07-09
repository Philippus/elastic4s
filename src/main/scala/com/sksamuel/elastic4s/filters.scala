package com.sksamuel.elastic4s

import org.elasticsearch.index.query.{HasParentFilterBuilder, HasChildFilterBuilder, FilterBuilders}
import org.elasticsearch.common.geo.GeoDistance
import org.elasticsearch.common.unit.DistanceUnit

/** @author Stephen Samuel */
trait FilterDsl {

  def geoboxFilter(name: String) = new GeoBoundingBoxFilter(name)
  def geoDistance(name: String) = new GeoDistanceFilter(name)
  def geoPolygon(name: String) = new GeoPolygonFilter(name)

  def hasChildFilter(`type`: String) = new HasChildExpectsQueryOrFilter(`type`)
  class HasChildExpectsQueryOrFilter(`type`: String) {
    def query(query: QueryDefinition) =
      new HasChildFilterDefinition(FilterBuilders.hasChildFilter(`type`, query.builder))
    def filter(filter: FilterDefinition) =
      new HasChildFilterDefinition(FilterBuilders.hasChildFilter(`type`, filter.builder))
  }

  def hasParentFilter(`type`: String) = new HasParentExpectsQueryOrFilter(`type`)
  class HasParentExpectsQueryOrFilter(`type`: String) {
    def query(query: QueryDefinition) =
      new HasParentFilterDefinition(FilterBuilders.hasParentFilter(`type`, query.builder))
    def filter(filter: FilterDefinition) =
      new HasParentFilterDefinition(FilterBuilders.hasParentFilter(`type`, filter.builder))
  }

  def prefixFilter(field: String, prefix: Any): PrefixFilterDefinition = new PrefixFilterDefinition(field, prefix)
  def prefixFilter(tuple: (String, Any)): PrefixFilterDefinition = prefixFilter(tuple._1, tuple._2)

  def regexFilter(field: String, prefix: Any): RegexFilterDefinition = new RegexFilterDefinition(field, prefix)
  def regexFilter(tuple: (String, Any)): RegexFilterDefinition = regexFilter(tuple._1, tuple._2)

  def termFilter(field: String, prefix: Any): TermFilterDefinition = new TermFilterDefinition(field, prefix)
  def termFilter(tuple: (String, Any)): TermFilterDefinition = termFilter(tuple._1, tuple._2)

  def typeFilter(`type`: String): TypeFilterDefinition = new TypeFilterDefinition(`type`)
  def missingFilter(field: String): MissingFilterDefinition = new MissingFilterDefinition(field)
  def idsFilter(ids: String*): IdFilterDefinition = new IdFilterDefinition(ids: _*)

  def bool(block: => BoolFilterDefinition): FilterDefinition = block
  def must(queries: FilterDefinition*): BoolFilterDefinition = new BoolFilterDefinition().must(queries: _*)
  def should(queries: FilterDefinition*): BoolFilterDefinition = new BoolFilterDefinition().should(queries: _*)
  def not(queries: FilterDefinition*): BoolFilterDefinition = new BoolFilterDefinition().not(queries: _*)
}

class BoolFilterDefinition extends FilterDefinition {
  val builder = FilterBuilders.boolFilter()
  def must(filters: FilterDefinition*) = {
    filters.foreach(builder must _.builder)
    this
  }
  def should(filters: FilterDefinition*) = {
    filters.foreach(builder should _.builder)
    this
  }
  def not(filters: FilterDefinition*) = {
    filters.foreach(builder mustNot _.builder)
    this
  }
}

trait FilterDefinition {
  def builder: org.elasticsearch.index.query.FilterBuilder
}

class IdFilterDefinition(ids: String*) extends FilterDefinition {
  val builder = FilterBuilders.idsFilter().addIds(ids: _*)
  def filterName(filterName: String): IdFilterDefinition = {
    builder.filterName(filterName)
    this
  }
  def withIds(any: Any*): IdFilterDefinition = {
    any.foreach(id => builder.addIds(id.toString))
    this
  }
}

class TypeFilterDefinition(`type`: String) extends FilterDefinition {
  val builder = FilterBuilders.typeFilter(`type`)
}

class MissingFilterDefinition(field: String) extends FilterDefinition {
  val builder = FilterBuilders.missingFilter(field)
  def includeNull(nullValue: Boolean): MissingFilterDefinition = {
    builder.nullValue(nullValue)
    this
  }
  def filterName(filterName: String): MissingFilterDefinition = {
    builder.filterName(filterName)
    this
  }
  def existence(existence: Boolean): MissingFilterDefinition = {
    builder.existence(existence)
    this
  }
}

class HasChildFilterDefinition(val builder: HasChildFilterBuilder) extends FilterDefinition {
  def cache(cache: Boolean): HasChildFilterDefinition = {
    builder.cache(cache)
    this
  }
  def cacheKey(cacheKey: String): HasChildFilterDefinition = {
    builder.cacheKey(cacheKey)
    this
  }
  def name(name: String): HasChildFilterDefinition = {
    builder.filterName(name)
    this
  }
}

class HasParentFilterDefinition(val builder: HasParentFilterBuilder) extends FilterDefinition {
  def cache(cache: Boolean): HasParentFilterDefinition = {
    builder.cache(cache)
    this
  }
  def cacheKey(cacheKey: String): HasParentFilterDefinition = {
    builder.cacheKey(cacheKey)
    this
  }
  def name(name: String): HasParentFilterDefinition = {
    builder.filterName(name)
    this
  }
}

class PrefixFilterDefinition(field: String, prefix: Any) extends FilterDefinition {
  val builder = FilterBuilders.prefixFilter(field, prefix.toString)
  def cache(cache: Boolean) = {
    builder.cache(cache)
    this
  }
  def cacheKey(cacheKey: String) = {
    builder.cacheKey(cacheKey)
    this
  }
  def name(name: String) = {
    builder.filterName(name)
    this
  }
}

class TermFilterDefinition(field: String, value: Any) extends FilterDefinition {
  val builder = FilterBuilders.termFilter(field, value.toString)
  def cache(cache: Boolean) = {
    builder.cache(cache)
    this
  }
  def cacheKey(cacheKey: String) = {
    builder.cacheKey(cacheKey)
    this
  }
  def name(name: String) = {
    builder.filterName(name)
    this
  }
}

class GeoPolygonFilter(name: String) extends FilterDefinition {
  val builder = FilterBuilders.geoPolygonFilter(name)
  def cache(cache: Boolean): GeoPolygonFilter = {
    builder.cache(cache)
    this
  }
  def cacheKey(cacheKey: String): GeoPolygonFilter = {
    builder.cacheKey(cacheKey)
    this
  }
  def point(lat: Double, lon: Double): GeoPolygonFilter = {
    builder.addPoint(lat, lon)
    this
  }
  def point(geohash: String): GeoPolygonFilter = {
    builder.addPoint(geohash)
    this
  }
}

class GeoDistanceFilter(name: String) extends FilterDefinition {
  val builder = FilterBuilders.geoDistanceFilter(name)
  def cache(cache: Boolean): GeoDistanceFilter = {
    builder.cache(cache)
    this
  }
  def cacheKey(cacheKey: String): GeoDistanceFilter = {
    builder.cacheKey(cacheKey)
    this
  }
  def geohash(geohash: String): GeoDistanceFilter = {
    builder.geohash(geohash)
    this
  }
  def lat(lat: Double): GeoDistanceFilter = {
    builder.lat(lat)
    this
  }
  def lon(long: Double): GeoDistanceFilter = {
    builder.lon(long)
    this
  }
  def method(method: GeoDistance): GeoDistanceFilter = geoDistance(method)
  def geoDistance(geoDistance: GeoDistance): GeoDistanceFilter = {
    builder.geoDistance(geoDistance)
    this
  }
  def distance(distance: String): GeoDistanceFilter = {
    builder.distance(distance)
    this
  }
  def distance(distance: Double, unit: DistanceUnit): GeoDistanceFilter = {
    builder.distance(distance, unit)
    this
  }
  def point(lat: Double, long: Double): GeoDistanceFilter = {
    builder.point(lat, long)
    this
  }
  def point(point: (Double, Double)): GeoDistanceFilter = {
    builder.point(point._1, point._2)
    this
  }
}

class GeoBoundingBoxFilter(name: String) extends FilterDefinition {
  val builder = FilterBuilders.geoBoundingBoxFilter(name)
  private var _left: Double = _
  private var _top: Double = _
  private var _right: Double = _
  private var _bottom: Double = _
  def cache(cache: Boolean): GeoBoundingBoxFilter = {
    builder.cache(cache)
    this
  }
  def cacheKey(cacheKey: String): GeoBoundingBoxFilter = {
    builder.cacheKey(cacheKey)
    this
  }
  def left(left: Double): GeoBoundingBoxFilter = {
    _left = left
    builder.topLeft(_left, _top)
    this
  }
  def top(top: Double): GeoBoundingBoxFilter = {
    _top = top
    builder.topLeft(_left, _top)
    this
  }
  def right(right: Double): GeoBoundingBoxFilter = {
    _right = right
    builder.bottomRight(_left, _top)
    this
  }
  def bottom(bottom: Double): GeoBoundingBoxFilter = {
    _bottom = bottom
    builder.bottomRight(_right, _bottom)
    this
  }
}

class RegexFilterDefinition(field: String, regex: Any) extends FilterDefinition {
  val builder = FilterBuilders.regexpFilter(field, regex.toString)
  def cache(cache: Boolean) = {
    builder.cache(cache)
    this
  }
  def cacheKey(cacheKey: String) = {
    builder.cacheKey(cacheKey)
    this
  }
  def name(name: String) = {
    builder.filterName(name)
    this
  }
}