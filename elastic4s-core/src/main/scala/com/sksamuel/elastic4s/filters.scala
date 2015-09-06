package com.sksamuel.elastic4s

import org.elasticsearch.index.query.{QueryBuilders, HasParentFilterBuilder, HasChildFilterBuilder, NestedFilterBuilder, FilterBuilders}
import org.elasticsearch.common.geo.GeoDistance
import org.elasticsearch.common.unit.DistanceUnit
import com.sksamuel.elastic4s.DefinitionAttributes._

/** @author Stephen Samuel */
trait FilterDsl {

  def existsFilter(field: String): ExistsFilter = new ExistsFilter(field)

  def geoHashCellFilter(field: String): GeoHashCellFilter = new GeoHashCellFilter(field)

  @deprecated("use geoBoxFilter, just to keep consistent with other geoXX methods", "1.6.5")
  def geoboxFilter(field: String): GeoBoundingBoxFilter = new GeoBoundingBoxFilter(field)

  def geoBoxFilter(field: String) = geoboxFilter(field)

  def geoDistance(field: String): GeoDistanceFilter = new GeoDistanceFilter(field)
  def geoPolygon(field: String): GeoPolygonFilter = new GeoPolygonFilter(field)
  def geoDistanceRangeFilter(field: String): GeoDistanceRangeQueryDefinition =
    new GeoDistanceRangeQueryDefinition(field)

  def hasChildFilter(`type`: String): HasChildExpectsQueryOrFilter = new HasChildExpectsQueryOrFilter(`type`)
  class HasChildExpectsQueryOrFilter(`type`: String) {
    def query(query: QueryDefinition) = new
        HasChildQueryDefinition(FilterBuilders.hasChildFilter(`type`, query.builder))
    def filter(filter: QueryDefinition) = new
        HasChildQueryDefinition(FilterBuilders.hasChildFilter(`type`, filter.builder))
  }

  def hasParentFilter(`type`: String): HasParentExpectsQueryOrFilter = new HasParentExpectsQueryOrFilter(`type`)
  class HasParentExpectsQueryOrFilter(`type`: String) {
    def query(query: QueryDefinition) = new
        HasParentQueryDefinition(FilterBuilders.hasParentFilter(`type`, query.builder))
    def filter(filter: QueryDefinition) = new
        HasParentQueryDefinition(FilterBuilders.hasParentFilter(`type`, filter.builder))
  }

  def inFilter(name: String, values: Iterable[String]): InQueryDefinition = new InQueryDefinition(name, values.toSeq)
  def inFilter(name: String, values: String*): InQueryDefinition = new InQueryDefinition(name, values)

  def indicesFilter(filter: QueryDefinition, indexes: Iterable[String]): IndicesQueryDefinition = {
    new IndicesQueryDefinition(filter, indexes.toSeq)
  }

  def indicesFilter(filter: QueryDefinition, indexes: String*): IndicesQueryDefinition = {
    new IndicesQueryDefinition(filter, indexes)
  }

  def nestedFilter(path: String): NestedFilterExpectsQueryOrFilter = new NestedFilterExpectsQueryOrFilter(path)
  class NestedFilterExpectsQueryOrFilter(path: String) {
    def query(query: QueryDefinition) = new NestedQueryDefinition(FilterBuilders.nestedFilter(path, query.builder))
    def filter(filter: QueryDefinition) = new NestedQueryDefinition(FilterBuilders.nestedFilter(path, filter.builder))
  }

  def matchAllFilter: MatchAllFilter = new MatchAllFilter

  def or(filters: QueryDefinition*): OrQueryDefinition = new OrQueryDefinition(filters: _*)
  def or(filters: Iterable[QueryDefinition]): OrQueryDefinition = new OrQueryDefinition(filters.toSeq: _*)
  def orFilter(filters: QueryDefinition*): OrQueryDefinition = new OrQueryDefinition(filters: _*)
  def orFilter(filters: Iterable[QueryDefinition]): OrQueryDefinition = new OrQueryDefinition(filters.toSeq: _*)

  def and(filters: QueryDefinition*): AndQueryDefinition = andFilter(filters)
  def and(filters: Iterable[QueryDefinition]): AndQueryDefinition = andFilter(filters)
  def andFilter(filters: QueryDefinition*): AndQueryDefinition = andFilter(filters)
  def andFilter(filters: Iterable[QueryDefinition]): AndQueryDefinition = new AndQueryDefinition(filters.toSeq: _*)

  @deprecated("deprecated in elasticsearch since 1.0", "1.6.5")
  def numericRangeFilter(field: String): NumericRangeFilter = new NumericRangeFilter(field)
  def rangeFilter(field: String): RangeFilter = new RangeFilter(field)

  def prefixFilter(field: String, prefix: Any): PrefixQueryDefinition = new PrefixQueryDefinition(field, prefix)
  def prefixFilter(tuple: (String, Any)): PrefixQueryDefinition = prefixFilter(tuple._1, tuple._2)

  def queryFilter(query: QueryDefinition): QueryQueryDefinition = new QueryQueryDefinition(query)

  def regexFilter(field: String, regex: Any): RegexQueryDefinition = new RegexQueryDefinition(field, regex)
  def regexFilter(tuple: (String, Any)): RegexQueryDefinition = regexFilter(tuple._1, tuple._2)

  def scriptFilter(script: String): ScriptQueryDefinition = new ScriptQueryDefinition(script)

  def termFilter(field: String, value: Any): TermQueryDefinition = new TermQueryDefinition(field, value)
  def termFilter(tuple: (String, Any)): TermQueryDefinition = termFilter(tuple._1, tuple._2)

  def termsFilter(field: String, values: Any*): TermsQueryDefinition = new
      TermsQueryDefinition(field, values.map(_.toString): _*)

  def termsLookupFilter(field: String): TermsLookupQueryDefinition = new TermsLookupQueryDefinition(field)

  def typeFilter(`type`: String): TypeQueryDefinition = new TypeQueryDefinition(`type`)
  def missingFilter(field: String): MissingQueryDefinition = new MissingQueryDefinition(field)
  def idsFilter(ids: String*): IdQueryDefinition = new IdQueryDefinition(ids: _*)

  def bool(block: => BoolQueryDefinition): QueryDefinition = block
  def must(queries: QueryDefinition*): BoolQueryDefinition = new BoolQueryDefinition().must(queries: _*)
  def must(queries: Iterable[QueryDefinition]): BoolQueryDefinition = new BoolQueryDefinition().must(queries)
  def should(queries: QueryDefinition*): BoolQueryDefinition = new BoolQueryDefinition().should(queries: _*)
  def should(queries: Iterable[QueryDefinition]): BoolQueryDefinition = new BoolQueryDefinition().should(queries)

  case object not {
    def filter(filter: QueryDefinition): NotQueryDefinition = new NotQueryDefinition(filter)
  }
  def not(filter: QueryDefinition): NotQueryDefinition = new NotQueryDefinition(filter)
  def not(queries: QueryDefinition*): BoolQueryDefinition = new BoolQueryDefinition().not(queries: _*)
  def not(queries: Iterable[QueryDefinition]): BoolQueryDefinition = new BoolQueryDefinition().not(queries)
}

class BoolQueryDefinition extends QueryDefinition {

  val builder = QueryDefinitions.boolFilter()

  def must(filters: QueryDefinition*): this.type = {
    filters.foreach(builder must _.builder)
    this
  }

  def must(filters: Iterable[QueryDefinition]): this.type = {
    filters.foreach(builder must _.builder)
    this
  }

  def should(filters: QueryDefinition*): this.type = {
    filters.foreach(builder should _.builder)
    this
  }

  def should(filters: Iterable[QueryDefinition]): this.type = {
    filters.foreach(builder should _.builder)
    this
  }

  def not(filters: QueryDefinition*): this.type = {
    filters.foreach(builder mustNot _.builder)
    this
  }

  def not(filters: Iterable[QueryDefinition]): this.type = {
    filters.foreach(builder mustNot _.builder)
    this
  }
}

class IdQueryDefinition(ids: String*) extends QueryDefinition {
  val builder = FilterBuilders.idsFilter().addIds(ids: _*)
  def filterName(filterName: String): IdQueryDefinition = {
    builder.filterName(filterName)
    this
  }
  def withIds(any: Any*): IdQueryDefinition = {
    any.foreach(id => builder.addIds(id.toString))
    this
  }
}

class IndicesQueryDefinition(filter: QueryDefinition, indexes: Seq[String]) extends QueryDefinition {

  val builder = FilterBuilders.indicesFilter(filter.builder, indexes: _*)

  def filterName(filterName: String): this.type = {
    builder.filterName(filterName)
    this
  }

  def noMatchFilter(filter: QueryDefinition): this.type = {
    builder.noMatchFilter(filter.builder)
    this
  }

  def noMatchFilter(noMatchFilter: String): this.type = {
    builder.noMatchFilter(noMatchFilter)
    this
  }
}

class InQueryDefinition(name: String, values: Seq[String])
  extends QueryDefinition with DefinitionAttributeCacheKey with DefinitionAttributeCache {

  val builder = FilterBuilders.inFilter(name, values: _*)
  val _builder = builder

  def filterName(filterName: String): this.type = {
    builder.filterName(filterName)
    this
  }

  def execution(execution: String): this.type = {
    builder.execution(execution)
    this
  }
}

class TypeQueryDefinition(`type`: String) extends QueryDefinition {
  val builder = FilterBuilders.typeFilter(`type`)
}

class ExistsFilter(field: String) extends QueryDefinition {
  val builder = FilterBuilders.existsFilter(field)
  def filterName(filterName: String): ExistsFilter = {
    builder.filterName(filterName)
    this
  }
}

class QueryQueryDefinition(q: QueryDefinition)
  extends QueryDefinition
  with DefinitionAttributeCache {
  val builder = FilterBuilders.queryFilter(q.builder)
  val _builder = builder
  def filterName(filterName: String): QueryQueryDefinition = {
    builder.filterName(filterName)
    this
  }
}


class MissingQueryDefinition(field: String) extends QueryDefinition {
  val builder = FilterBuilders.missingFilter(field)
  def includeNull(nullValue: Boolean): MissingQueryDefinition = {
    builder.nullValue(nullValue)
    this
  }
  def filterName(filterName: String): MissingQueryDefinition = {
    builder.filterName(filterName)
    this
  }
  def existence(existence: Boolean): MissingQueryDefinition = {
    builder.existence(existence)
    this
  }
}

class ScriptQueryDefinition(script: String)
  extends QueryDefinition
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey
  with DefinitionAttributeFilterName {
  val builder = FilterBuilders.scriptFilter(script)
  val _builder = builder
  def lang(lang: String): ScriptQueryDefinition = {
    builder.lang(lang)
    this
  }
  def param(name: String, value: Any): ScriptQueryDefinition = {
    builder.addParam(name, value)
    this
  }
  def params(map: Map[String, Any]): ScriptQueryDefinition = {
    for ( entry <- map ) param(entry._1, entry._2)
    this
  }
}

class MatchAllFilter extends QueryDefinition {
  val builder = FilterBuilders.matchAllFilter()
}

@deprecated("deprecated in elasticsearch 1.0", "1.0")
class NumericRangeFilter(field: String)
  extends QueryDefinition
  with DefinitionAttributeFrom
  with DefinitionAttributeTo
  with DefinitionAttributeLt
  with DefinitionAttributeGt
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey {

  val builder = FilterBuilders.numericRangeFilter(field)
  val _builder = builder

  @deprecated("deprecated in elasticsearch since 1.0", "1.6.5")
  def filterName(filterName: String): NumericRangeFilter = {
    builder.filterName(filterName)
    this
  }

  @deprecated("deprecated in elasticsearch since 1.0", "1.6.5")
  def includeLower(includeLower: Boolean): NumericRangeFilter = {
    builder.includeLower(includeLower)
    this
  }

  @deprecated("deprecated in elasticsearch since 1.0", "1.6.5")
  def includeUpper(includeUpper: Boolean): NumericRangeFilter = {
    builder.includeUpper(includeUpper)
    this
  }

  @deprecated("deprecated in elasticsearch since 1.0", "1.6.5")
  def lte(lte: Double): NumericRangeFilter = {
    builder.lte(lte)
    this
  }

  @deprecated("deprecated in elasticsearch since 1.0", "1.6.5")
  def lte(lte: Long): NumericRangeFilter = {
    builder.lte(lte)
    this
  }

  @deprecated("deprecated in elasticsearch since 1.0", "1.6.5")
  def gte(gte: Double): NumericRangeFilter = {
    builder.gte(gte)
    this
  }

  @deprecated("deprecated in elasticsearch since 1.0", "1.6.5")
  def gte(gte: Long): NumericRangeFilter = {
    builder.gte(gte)
    this
  }
}

class RangeFilter(field: String)
  extends QueryDefinition
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
  def execution(execution: String): RangeFilter = {
    builder.setExecution(execution)
    this
  }
}

class HasChildQueryDefinition(val builder: HasChildFilterBuilder)
  extends QueryDefinition {
  val _builder = builder
  def name(name: String): HasChildQueryDefinition = {
    builder.filterName(name)
    this
  }
}

class HasParentQueryDefinition(val builder: HasParentFilterBuilder)
  extends QueryDefinition {
  val _builder = builder
  def name(name: String): HasParentQueryDefinition = {
    builder.filterName(name)
    this
  }
}

class NestedQueryDefinition(val builder: NestedFilterBuilder)
  extends QueryDefinition
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey
  with DefinitionAttributeFilterName {
  val _builder = builder
  def join(join: Boolean): NestedQueryDefinition = {
    builder.join(join)
    this
  }
}

class PrefixQueryDefinition(field: String, prefix: Any)
  extends QueryDefinition
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey {

  val builder = FilterBuilders.prefixFilter(field, prefix.toString)
  val _builder = builder

  def name(name: String): this.type = {
    builder.filterName(name)
    this
  }
}

class TermQueryDefinition(field: String, value: Any)
  extends QueryDefinition
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey {

  val builder = FilterBuilders.termFilter(field, value.toString)
  val _builder = builder

  def name(name: String): this.type = {
    builder.filterName(name)
    this
  }
}

class TermsQueryDefinition(field: String, values: Any*)
  extends QueryDefinition
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey {

  import scala.collection.JavaConverters._

  val builder = FilterBuilders.termsFilter(field, values.asJava)
  val _builder = builder

  def name(name: String): this.type = {
    builder.filterName(name)
    this
  }

  def execution(execution: String): this.type = {
    builder.execution(execution)
    this
  }
}

class TermsLookupQueryDefinition(field: String)
  extends QueryDefinition
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey {

  val builder = FilterBuilders.termsLookupFilter(field)
  val _builder = builder

  def name(name: String): this.type = {
    builder.filterName(name)
    this
  }

  def index(index: String): this.type = {
    builder.lookupIndex(index)
    this
  }

  def lookupType(`type`: String): this.type = {
    builder.lookupType(`type`)
    this
  }

  def id(id: String): this.type = {
    builder.lookupId(id)
    this
  }

  def path(path: String): this.type = {
    builder.lookupPath(path)
    this
  }

  def routing(routing: String): this.type = {
    builder.lookupRouting(routing)
    this
  }

  def lookupCache(cache: Boolean): this.type = {
    builder.lookupCache(cache)
    this
  }
}

class GeoPolygonFilter(name: String)
  extends QueryDefinition
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

class GeoDistanceRangeQueryDefinition(field: String)
  extends QueryDefinition
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
  def geoDistance(geoDistance: GeoDistance): GeoDistanceRangeQueryDefinition = {
    builder.geoDistance(geoDistance)
    this
  }
  def geohash(geohash: String): GeoDistanceRangeQueryDefinition = {
    builder.geohash(geohash)
    this
  }
  def gte(gte: Any): GeoDistanceRangeQueryDefinition = {
    builder.gte(gte)
    this
  }
  def lte(lte: Any): GeoDistanceRangeQueryDefinition = {
    builder.lte(lte)
    this
  }
  def includeLower(includeLower: Boolean): GeoDistanceRangeQueryDefinition = {
    builder.includeLower(includeLower)
    this
  }
  def includeUpper(includeUpper: Boolean): GeoDistanceRangeQueryDefinition = {
    builder.includeUpper(includeUpper)
    this
  }
  def name(name: String): GeoDistanceRangeQueryDefinition = {
    builder.filterName(name)
    this
  }
}

class NotQueryDefinition(filter: QueryDefinition)
  extends QueryDefinition
  with DefinitionAttributeCache
  with DefinitionAttributeFilterName {
  val builder = FilterBuilders.notFilter(filter.builder)
  val _builder = builder
}

class OrQueryDefinition(filters: QueryDefinition*)
  extends QueryDefinition
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey {

  val builder = FilterBuilders.orFilter(filters.map(_.builder).toArray: _*)
  val _builder = builder

  def name(name: String): this.type = {
    builder.filterName(name)
    this
  }
}

class AndQueryDefinition(filters: QueryDefinition*)
  extends QueryDefinition
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey {

  val builder = FilterBuilders.andFilter(filters.map(_.builder).toArray: _*)
  val _builder = builder

  def name(name: String): this.type = {
    builder.filterName(name)
    this
  }
}

class GeoDistanceFilter(name: String)
  extends QueryDefinition
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
  extends QueryDefinition
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
    builder.topLeft(_top, _left)
    this
  }
  def top(top: Double): GeoBoundingBoxFilter = {
    _top = top
    builder.topLeft(_top, _left)
    this
  }
  def right(right: Double): GeoBoundingBoxFilter = {
    _right = right
    builder.bottomRight(_bottom, _right)
    this
  }
  def bottom(bottom: Double): GeoBoundingBoxFilter = {
    _bottom = bottom
    builder.bottomRight(_bottom, _right)
    this
  }
}

class RegexQueryDefinition(field: String, regex: Any)
  extends QueryDefinition
  with DefinitionAttributeCache
  with DefinitionAttributeCacheKey {

  val builder = FilterBuilders.regexpFilter(field, regex.toString)
  val _builder = builder

  def name(name: String): this.type = {
    builder.filterName(name)
    this
  }
}
