package com.sksamuel.elastic4s

import org.elasticsearch.search.facet.FacetBuilders

/** @author Stephen Samuel */
trait FacetBuilder {
    def build: Facet
}

case class TermsFacet(name: String,
                      fields: Seq[String],
                      order: TermsFacetOrder = TermsFacetOrder.Count,
                      size: Int = 10,
                      allTerms: Boolean = false,
                      excludeTerms: Seq[String] = Nil,
                      script: Option[String] = None,
                      regex: Option[String] = None,
                      regexFlags: Seq[String] = Nil,
                      script_field: Option[String] = None) extends Facet(name) {
    def builder = FacetBuilders
      .termsFacet(name)
      .allTerms(allTerms)
      .order(order.elasticType)
      .regex(regex.orNull)
      .script(script.orNull)
      .global(global)
      .scriptField(script_field.orNull)
      .fields(fields: _ *)
      .size(size)
}

case class RangeFacet(name: String, field: String, ranges: Seq[Range], keyField: Option[String] = None, valueField: Option[String] = None)
  extends Facet(name) {
    def builder = {
        val builder = FacetBuilders.rangeFacet(name).field(field).keyField(keyField.orNull).valueField(valueField.orNull)
        for ( range <- ranges )
            builder.addRange(range.start, range.end)
        builder
    }
}

case class QueryFacet(name: String, query: Query) extends Facet(name) {
    def builder = FacetBuilders.queryFacet(name, null)
}

case class FilterFacet(name: String, query: Query) extends Facet(name) {
    def builder = FacetBuilders.filterFacet(name, null)
}

abstract class TermsFacetOrder(val elasticType: org.elasticsearch.search.facet.terms.TermsFacet.ComparatorType)
case object TermsFacetOrder {
    case object Count extends TermsFacetOrder(org.elasticsearch.search.facet.terms.TermsFacet.ComparatorType.COUNT)
    case object Term extends TermsFacetOrder(org.elasticsearch.search.facet.terms.TermsFacet.ComparatorType.TERM)
    case object ReverseCount extends TermsFacetOrder(org.elasticsearch.search.facet.terms.TermsFacet.ComparatorType.REVERSE_COUNT)
    case object ReverseTerm extends TermsFacetOrder(org.elasticsearch.search.facet.terms.TermsFacet.ComparatorType.REVERSE_TERM)
}
