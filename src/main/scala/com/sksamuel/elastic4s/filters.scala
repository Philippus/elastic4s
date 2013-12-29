package com.sksamuel.elastic4s

import org.elasticsearch.index.query.{HasParentFilterBuilder, HasChildFilterBuilder, FilterBuilders}
import org.elasticsearch.common.geo.GeoDistance
import org.elasticsearch.common.unit.DistanceUnit
import com.sksamuel.elastic4s.DefinitionAttributes._

/** @author Stephen Samuel */
trait FilterDsl {

  def existsFilter(field: String) = new ExistsFilter(field)

  def geoboxFilter(field: String) = new GeoBoundingBoxFilter(field)
  def geoDistance(field: String) = new GeoDistanceFilter(field)
  def geoPolygon(field: String) = new GeoPolygonFilter(field)
  def geoDistanceRangeFilter(field: String) = new GeoDistanceRangeFilterDefinition(field)

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

  def matchAllFilter = new MatchAllFilter

  def not = new NotExpectsFilter
  def not(filter: FilterDefinition) = new NotFilterDefinition(filter)
  class NotExpectsFilter {
    def filter(filter: FilterDefinition) = new NotFilterDefinition(filter)
  }
  def numericRangeFilter(field: String) = new NumericRangeFilter(field)
  def rangeFilter(field: String) = new RangeFilter(field)

  def prefixFilter(field: String, prefix: Any): PrefixFilterDefinition = new PrefixFilterDefinition(field, prefix)
  def prefixFilter(tuple: (String, Any)): PrefixFilterDefinition = prefixFilter(tuple._1, tuple._2)

  def queryFilter(query: QueryDefinition): QueryFilterDefinition = new QueryFilterDefinition(query)

  def regexFilter(field: String, regex: Any): RegexFilterDefinition = new RegexFilterDefinition(field, regex)
  def regexFilter(tuple: (String, Any)): RegexFilterDefinition = regexFilter(tuple._1, tuple._2)

  def scriptFilter(script: String): ScriptFilterDefinition = new ScriptFilterDefinition(script)

  def termFilter(field: String, value: Any): TermFilterDefinition = new TermFilterDefinition(field, value)
  def termFilter(tuple: (String, Any)): TermFilterDefinition = termFilter(tuple._1, tuple._2)

  def termsFilter(field: String, values: Any*): TermsFilterDefinition = new TermsFilterDefinition(field, values.map(_.toString): _*)

  def typeFilter(`type`: String): TypeFilterDefinition = new TypeFilterDefinition(`type`)
  def missingFilter(field: String): MissingFilterDefinition = new MissingFilterDefinition(field)
  def idsFilter(ids: String*): IdFilterDefinition = new IdFilterDefinition(ids: _*)

  def bool(block: => BoolFilterDefinition): FilterDefinition = block
  def must(queries: FilterDefinition*): BoolFilterDefinition = new BoolFilterDefinition().must(queries: _*)
  def should(queries: FilterDefinition*): BoolFilterDefinition = new BoolFilterDefinition().should(queries: _*)
  def not(queries: FilterDefinition*): BoolFilterDefinition = new BoolFilterDefinition().not(queries: _*)
}

trait FilterDefinition {
  def builder: org.elasticsearch.index.query.FilterBuilder
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

class ExistsFilter(field: String) extends FilterDefinition {
  val builder = FilterBuilders.existsFilter(field)
  def filterName(filterName: String): ExistsFilter = {
    builder.filterName(filterName)
    this
  }
}

class QueryFilterDefinition(q: QueryDefinition)
  extends FilterDefinition
  with DefinitionAttributeCache {
  val builder = FilterBuilders.queryFilter(q.builder)
  val _builder = builder
  def filterName(filterName: String): QueryFilterDefinition = {
    builder.filterName(filterName)
    this
  }
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

class ScriptFilterDefinition(script: String)
  extends FilterDefinition
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey
  with DefinitionAttributeFilterName {
  val builder = FilterBuilders.scriptFilter(script)
  val _builder = builder
  def lang(lang: String): ScriptFilterDefinition = {
    builder.lang(lang)
    this
  }
  def param(name: String, value: Any): ScriptFilterDefinition = {
    builder.addParam(name, value)
    this
  }
  def params(map: Map[String, Any]): ScriptFilterDefinition = {
    for ( entry <- map ) param(entry._1, entry._2)
    this
  }
}

class MatchAllFilter extends FilterDefinition {
  val builder = FilterBuilders.matchAllFilter()
}

@deprecated("deprecated in elasticsearch 1.0", "1.0")
class NumericRangeFilter(field: String)
  extends FilterDefinition
  with DefinitionAttributeFrom
  with DefinitionAttributeTo
  with DefinitionAttributeLt
  with DefinitionAttributeGt
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey {
  val builder = FilterBuilders.numericRangeFilter(field)
  val _builder = builder
  def filterName(filterName: String): NumericRangeFilter = {
    builder.filterName(filterName)
    this
  }
  def includeLower(includeLower: Boolean): NumericRangeFilter = {
    builder.includeLower(includeLower)
    this
  }
  def includeUpper(includeUpper: Boolean): NumericRangeFilter = {
    builder.includeUpper(includeUpper)
    this
  }
  def lte(lte: Double): NumericRangeFilter = {
    builder.lte(lte)
    this
  }
  def lte(lte: Long): NumericRangeFilter = {
    builder.lte(lte)
    this
  }
  def gte(gte: Double): NumericRangeFilter = {
    builder.gte(gte)
    this
  }
  def gte(gte: Long): NumericRangeFilter = {
    builder.gte(gte)
    this
  }
}

class RangeFilter(field: String)
  extends FilterDefinition
  with DefinitionAttributeTo
  with DefinitionAttributeFrom
  with DefinitionAttributeLt
  with DefinitionAttributeGt
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey
  with DefinitionAttributeFilterName {
  val builder = FilterBuilders.rangeFilter(field)
  val _builder = builder

  def includeLower(includeLower: Boolean): RangeFilter = {
    builder.includeLower(includeLower)
    this
  }
  def includeUpper(includeUpper: Boolean): RangeFilter = {
    builder.includeUpper(includeUpper)
    this
  }
  def lte(lte: String): RangeFilter = {
    builder.lte(lte)
    this
  }
  def gte(gte: String): RangeFilter = {
    builder.gte(gte)
    this
  }
}

class HasChildFilterDefinition(val builder: HasChildFilterBuilder)
  extends FilterDefinition
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey {
  val _builder = builder
  def name(name: String): HasChildFilterDefinition = {
    builder.filterName(name)
    this
  }
}

class HasParentFilterDefinition(val builder: HasParentFilterBuilder)
  extends FilterDefinition
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey {
  val _builder = builder
  def name(name: String): HasParentFilterDefinition = {
    builder.filterName(name)
    this
  }
}

class PrefixFilterDefinition(field: String, prefix: Any)
  extends FilterDefinition
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey {
  val builder = FilterBuilders.prefixFilter(field, prefix.toString)
  val _builder = builder
  def name(name: String) = {
    builder.filterName(name)
    this
  }
}

class TermFilterDefinition(field: String, value: Any)
  extends FilterDefinition
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey {
  val builder = FilterBuilders.termFilter(field, value.toString)
  val _builder = builder
  def name(name: String) = {
    builder.filterName(name)
    this
  }
}

class TermsFilterDefinition(field: String, value: String*)
  extends FilterDefinition
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey {
  val builder = FilterBuilders.termsFilter(field, value: _*)
  val _builder = builder
  def name(name: String) = {
    builder.filterName(name)
    this
  }
}

class GeoPolygonFilter(name: String)
  extends FilterDefinition
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey {
  val builder = FilterBuilders.geoPolygonFilter(name)
  val _builder = builder
  def point(geohash: String): GeoPolygonFilter = {
    builder.addPoint(geohash)
    this
  }
  def point(lat: Double, lon: Double): this.type = {
    _builder.addPoint(lat, lon)
    this
  }
}

class GeoDistanceRangeFilterDefinition(field: String)
  extends FilterDefinition
  with DefinitionAttributeTo
  with DefinitionAttributeFrom
  with DefinitionAttributeLt
  with DefinitionAttributeGt
  with DefinitionAttributeLat
  with DefinitionAttributeLon
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey
  with DefinitionAttributePoint {
  val builder = FilterBuilders.geoDistanceRangeFilter(field)
  val _builder = builder
  def geoDistance(geoDistance: GeoDistance): GeoDistanceRangeFilterDefinition = {
    builder.geoDistance(geoDistance)
    this
  }
  def geohash(geohash: String): GeoDistanceRangeFilterDefinition = {
    builder.geohash(geohash)
    this
  }
  def gte(gte: Any): GeoDistanceRangeFilterDefinition = {
    builder.gte(gte)
    this
  }
  def lte(lte: Any): GeoDistanceRangeFilterDefinition = {
    builder.lte(lte)
    this
  }
  def includeLower(includeLower: Boolean): GeoDistanceRangeFilterDefinition = {
    builder.includeLower(includeLower)
    this
  }
  def includeUpper(includeUpper: Boolean): GeoDistanceRangeFilterDefinition = {
    builder.includeUpper(includeUpper)
    this
  }
  def name(name: String): GeoDistanceRangeFilterDefinition = {
    builder.filterName(name)
    this
  }
}

class NotFilterDefinition(filter: FilterDefinition)
  extends FilterDefinition
  with DefinitionAttributeCache
  with DefinitionAttributeFilterName {
  val builder = FilterBuilders.notFilter(filter.builder)
  val _builder = builder
}

class GeoDistanceFilter(name: String)
  extends FilterDefinition
  with DefinitionAttributeLat
  with DefinitionAttributeLon
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey {
  val builder = FilterBuilders.geoDistanceFilter(name)
  val _builder = builder
  def geohash(geohash: String): GeoDistanceFilter = {
    builder.geohash(geohash)
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
}

class GeoBoundingBoxFilter(name: String)
  extends FilterDefinition
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey {
  val builder = FilterBuilders.geoBoundingBoxFilter(name)
  val _builder = builder
  private var _left: Double = _
  private var _top: Double = _
  private var _right: Double = _
  private var _bottom: Double = _
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

class RegexFilterDefinition(field: String, regex: Any)
  extends FilterDefinition
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey {
  val builder = FilterBuilders.regexpFilter(field, regex.toString)
  val _builder = builder
  def name(name: String) = {
    builder.filterName(name)
    this
  }
}