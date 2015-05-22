package com.sksamuel.elastic4s

import org.elasticsearch.search.facet.{ FacetBuilder, FacetBuilders }
import org.elasticsearch.search.facet.terms.TermsFacet
import org.elasticsearch.search.facet.histogram.HistogramFacet
import org.elasticsearch.search.facet.datehistogram.DateHistogramFacet
import org.elasticsearch.common.geo.GeoDistance
import org.elasticsearch.search.facet.termsstats.TermsStatsFacet

/** @author Stephen Samuel */
@deprecated("Facets are deprecated, use aggregations", "1.3.0")
trait FacetDsl {
  def facet = new FacetExpectingType
  class FacetExpectingType {
    def terms(name: String) = new TermFacetDefinition(name)
    def range(name: String) = new RangeFacetDefinition(name)
    def histogram(name: String) = new HistogramExpectsInterval(name)
    class HistogramExpectsInterval(name: String) {
      def interval(interval: Long) = new HistogramFacetDefinition(name, interval)
    }
    def datehistogram(name: String) = new DateHistogramExpectsInterval(name)
    class DateHistogramExpectsInterval(name: String) {
      def interval(interval: String) = new DateHistogramFacetDefinition(name, interval)
    }
    def filter(name: String) = new FilterFacetDefinition(name)
    def query(name: String) = new QueryFacetDefinition(name)
    def statistical(name: String) = new StatisticalFacetDefinition(name)
    def termsStats(name: String) = new TermsStatsFacetDefinition(name)
    def geodistance(name: String) = new GeoDistanceFacetDefinition(name)
  }
}

@deprecated("Facets are deprecated, use aggregations", "1.3.0")
trait FacetDefinition {
  val builder: FacetBuilder
}

@deprecated("Facets are deprecated, use aggregations", "1.3.0")
class TermFacetDefinition(name: String) extends FacetDefinition {
  val builder = FacetBuilders.termsFacet(name)
  def allTerms(allTerms: Boolean): TermFacetDefinition = {
    builder.allTerms(allTerms)
    this
  }
  def executionHint(executionHint: String): TermFacetDefinition = {
    builder.executionHint(executionHint)
    this
  }
  def global(global: Boolean): TermFacetDefinition = {
    builder.global(global)
    this
  }
  def script(script: String): TermFacetDefinition = {
    builder.script(script)
    this
  }
  def lang(lang: String): TermFacetDefinition = {
    builder.lang(lang)
    this
  }
  def size(size: Int): TermFacetDefinition = {
    builder.size(size)
    this
  }
  def shardSize(shardSize: Int): TermFacetDefinition = {
    builder.shardSize(shardSize)
    this
  }
  def exclude(exclude: String*): TermFacetDefinition = {
    builder.exclude(exclude: _*)
    this
  }
  def nested(nested: String): TermFacetDefinition = {
    builder.nested(nested)
    this
  }
  def regex(regex: String): TermFacetDefinition = {
    builder.regex(regex)
    this
  }
  def order(order: TermsFacet.ComparatorType): TermFacetDefinition = {
    builder.order(order)
    this
  }
  def field(field: String) = fields(field)
  def fields(fields: String*): TermFacetDefinition = {
    builder.fields(fields: _*)
    this
  }
}

@deprecated("Facets are deprecated, use aggregations", "1.3.0")
class RangeFacetDefinition(name: String) extends FacetDefinition {
  val builder = FacetBuilders.rangeFacet(name)
  def range(from: Double, to: Double): RangeFacetDefinition = {
    builder.addRange(from, to)
    this
  }
  def range(r: (Any, Any)): RangeFacetDefinition = range(r._1.toString, r._2.toString)
  def range(from: String, to: String): RangeFacetDefinition = {
    builder.addRange(from, to)
    this
  }
  def to(n: Int): RangeFacetDefinition = to(n.toString)
  def to(n: String): RangeFacetDefinition = {
    builder.addUnboundedTo(n)
    this
  }
  def from(n: Int): RangeFacetDefinition = from(n.toString)
  def from(n: String): RangeFacetDefinition = {
    builder.addUnboundedFrom(n)
    this
  }
  def global(global: Boolean): RangeFacetDefinition = {
    builder.global(global)
    this
  }
  def field(field: String): RangeFacetDefinition = {
    builder.field(field)
    this
  }
  def keyField(keyField: String): RangeFacetDefinition = {
    builder.keyField(keyField)
    this
  }
  def valueField(valueField: String): RangeFacetDefinition = {
    builder.valueField(valueField)
    this
  }
  def nested(nested: String): RangeFacetDefinition = {
    builder.nested(nested)
    this
  }
}

@deprecated("Facets are deprecated, use aggregations", "1.3.0")
class HistogramFacetDefinition(name: String, interval: Long) extends FacetDefinition {
  val builder = FacetBuilders.histogramFacet(name).interval(interval)
  def global(global: Boolean): HistogramFacetDefinition = {
    builder.global(global)
    this
  }
  def valueField(valueField: String): HistogramFacetDefinition = {
    builder.valueField(valueField)
    this
  }
  def comparator(comparator: HistogramFacet.ComparatorType): HistogramFacetDefinition = {
    builder.comparator(comparator)
    this
  }
  def keyField(keyField: String): HistogramFacetDefinition = {
    builder.keyField(keyField)
    this
  }
  def nested(nested: String): HistogramFacetDefinition = {
    builder.nested(nested)
    this
  }
}

@deprecated("Facets are deprecated, use aggregations", "1.3.0")
class DateHistogramFacetDefinition(name: String, interval: String) extends FacetDefinition {
  val builder = FacetBuilders.dateHistogramFacet(name).interval(interval)
  def global(global: Boolean): DateHistogramFacetDefinition = {
    builder.global(global)
    this
  }
  def keyField(keyField: String): DateHistogramFacetDefinition = {
    builder.keyField(keyField)
    this
  }
  def valueField(valueField: String): DateHistogramFacetDefinition = {
    builder.valueField(valueField)
    this
  }
  def comparator(comparator: DateHistogramFacet.ComparatorType): DateHistogramFacetDefinition = {
    builder.comparator(comparator)
    this
  }
  def nested(nested: String): DateHistogramFacetDefinition = {
    builder.nested(nested)
    this
  }
}

@deprecated("Facets are deprecated, use aggregations", "1.3.0")
class FilterFacetDefinition(name: String) extends FacetDefinition {
  val builder = FacetBuilders.filterFacet(name)
  def global(global: Boolean): FilterFacetDefinition = {
    builder.global(global)
    this
  }
  def nested(nested: String): FilterFacetDefinition = {
    builder.nested(nested)
    this
  }
  def facetFilter(block: => FilterDefinition): FilterFacetDefinition = {
    builder.facetFilter(block.builder)
    this
  }
  def filter(block: => FilterDefinition): FilterFacetDefinition = {
    builder.filter(block.builder)
    this
  }
}

@deprecated("Facets are deprecated, use aggregations", "1.3.0")
class QueryFacetDefinition(name: String) extends FacetDefinition {
  val builder = FacetBuilders.queryFacet(name)
  def global(global: Boolean): QueryFacetDefinition = {
    builder.global(global)
    this
  }
  def nested(nested: String): QueryFacetDefinition = {
    builder.nested(nested)
    this
  }
  def query(block: => QueryDefinition): QueryFacetDefinition = {
    builder.query(block.builder)
    this
  }
  def facetFilter(block: => FilterDefinition): QueryFacetDefinition = {
    builder.facetFilter(block.builder)
    this
  }
}

@deprecated("Facets are deprecated, use aggregations", "1.3.0")
class StatisticalFacetDefinition(name: String) extends FacetDefinition {
  val builder = FacetBuilders.statisticalFacet(name)
  def field(field: String): StatisticalFacetDefinition = {
    builder.field(field)
    this
  }
  def global(global: Boolean): StatisticalFacetDefinition = {
    builder.global(global)
    this
  }
  def nested(nested: String): StatisticalFacetDefinition = {
    builder.nested(nested)
    this
  }
}

@deprecated("Facets are deprecated, use aggregations", "1.3.0")
class TermsStatsFacetDefinition(name: String) extends FacetDefinition {
  val builder = FacetBuilders.termsStatsFacet(name)
  def facetFilter(block: => FilterDefinition): TermsStatsFacetDefinition = {
    builder.facetFilter(block.builder)
    this
  }
  def global(global: Boolean): TermsStatsFacetDefinition = {
    builder.global(global)
    this
  }
  def keyField(keyField: String): TermsStatsFacetDefinition = {
    builder.keyField(keyField)
    this
  }
  def order(order: TermsStatsFacet.ComparatorType): TermsStatsFacetDefinition = {
    builder.order(order)
    this
  }
  def shardSize(shardSize: Int): TermsStatsFacetDefinition = {
    builder.shardSize(shardSize)
    this
  }
  def size(size: Int): TermsStatsFacetDefinition = {
    builder.size(size)
    this
  }
  def valueField(valueField: String): TermsStatsFacetDefinition = {
    builder.valueField(valueField)
    this
  }
  def valueScript(valueScript: String): TermsStatsFacetDefinition = {
    builder.valueScript(valueScript)
    this
  }
}

@deprecated("Facets are deprecated, use aggregations", "1.3.0")
class GeoDistanceFacetDefinition(name: String) extends FacetDefinition {
  val builder = FacetBuilders.geoDistanceFacet(name)
  def global(global: Boolean): GeoDistanceFacetDefinition = {
    builder.global(global)
    this
  }
  def range(tuple: (Double, Double)): GeoDistanceFacetDefinition = range(tuple._1, tuple._2)
  def range(from: Double, to: Double): GeoDistanceFacetDefinition = {
    builder.addRange(from, to)
    this
  }
  def field(field: String): GeoDistanceFacetDefinition = {
    builder.field(field)
    this
  }
  def facetFilter(block: => FilterDefinition): GeoDistanceFacetDefinition = {
    builder.facetFilter(block.builder)
    this
  }
  def valueField(valueField: String): GeoDistanceFacetDefinition = {
    builder.valueField(valueField)
    this
  }
  def valueScript(valueScript: String): GeoDistanceFacetDefinition = {
    builder.valueScript(valueScript)
    this
  }
  def geoDistance(geoDistance: GeoDistance): GeoDistanceFacetDefinition = {
    builder.geoDistance(geoDistance)
    this
  }
  def geohash(geohash: String): GeoDistanceFacetDefinition = {
    builder.geohash(geohash)
    this
  }
  def lang(lang: String): GeoDistanceFacetDefinition = {
    builder.lang(lang)
    this
  }
  def point(lat: Double, long: Double): GeoDistanceFacetDefinition = {
    builder.point(lat, long)
    this
  }
  def addUnboundedFrom(addUnboundedFrom: Double): GeoDistanceFacetDefinition = {
    builder.addUnboundedFrom(addUnboundedFrom)
    this
  }
  def addUnboundedTo(addUnboundedTo: Double): GeoDistanceFacetDefinition = {
    builder.addUnboundedTo(addUnboundedTo)
    this
  }
}