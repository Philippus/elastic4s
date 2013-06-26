package com.sksamuel.elastic4s

import org.elasticsearch.search.sort.{SortOrder, SortBuilder, SortBuilders}
import org.elasticsearch.common.geo.GeoDistance

/** @author Stephen Samuel */
trait SortDsl {

    def by = new ExpectsSort
    class ExpectsSort {

        def prefix(tuple: (String, Any)): PrefixQueryDefinition = prefix(tuple._1, tuple._2)
        def prefix(field: String, value: Any): PrefixQueryDefinition = new PrefixQueryDefinition(field, value)

        def score = new ScoreSortDefinition

        def geo(field: String): GeoDistanceSortDefinition = new GeoDistanceSortDefinition(field)
        def field(field: String): FieldSortDefinition = new FieldSortDefinition(field)

        def script(tuple: (String, String)): ScriptSortDefinition = script(tuple._1, tuple._2)
        def script(field: String, lang: String): ScriptSortDefinition = new ScriptSortDefinition(field, lang)
        def script(field: String): ExpectsScriptLang = new ExpectsScriptLang(field)
        class ExpectsScriptLang(field: String) {
            def as(lang: String): ScriptSortDefinition = new ScriptSortDefinition(field, lang)
        }
    }
}

sealed abstract class MultiMode(val elastic: String)
case object MultiMode {
    case object Min extends MultiMode("min")
    case object Max extends MultiMode("max")
    case object Sum extends MultiMode("sum")
    case object Avg extends MultiMode("avg")
}

trait SortDefinition {
    val builder: SortBuilder
}

class FieldSortDefinition(field: String) extends SortDefinition {
    val builder = SortBuilders.fieldSort(field)
    def missing(missing: AnyRef) = {
        builder.missing(missing)
        this
    }
    def ignoreUnmapped(ignoreUnmapped: Boolean) = {
        builder.ignoreUnmapped(ignoreUnmapped)
        this
    }
    def nestedPath(nestedPath: String) = {
        builder.setNestedPath(nestedPath)
        this
    }
    def mode(mode: MultiMode) = {
        builder.sortMode(mode.elastic)
        this
    }
    def order(order: SortOrder) = {
        builder.order(order)
        this
    }
}
class ScriptSortDefinition(script: String, `type`: String) extends SortDefinition {
    val builder = SortBuilders.scriptSort(script, `type`)
    def missing(missing: AnyRef) = {
        builder.missing(missing)
        this
    }
    def nestedPath(nestedPath: String) = {
        builder.setNestedPath(nestedPath)
        this
    }
    def mode(mode: MultiMode) = {
        builder.sortMode(mode.elastic)
        this
    }
    def order(order: SortOrder) = {
        builder.order(order)
        this
    }
}
class GeoDistanceSortDefinition(field: String) extends SortDefinition {
    val builder = SortBuilders.geoDistanceSort(field)
    def missing(missing: AnyRef) = {
        builder.missing(missing)
        this
    }
    def nestedPath(nestedPath: String) = {
        builder.setNestedPath(nestedPath)
        this
    }
    def mode(mode: MultiMode) = {
        builder.sortMode(mode.elastic)
        this
    }
    def order(order: SortOrder) = {
        builder.order(order)
        this
    }
    def geoDistance(geoDistance: GeoDistance) = {
        builder.geoDistance(geoDistance)
        this
    }
    def geohash(geohash: String) = {
        builder.geohash(geohash)
        this
    }
    def point(lat: Double, long: Double) = {
        builder.point(lat, long)
        this
    }
}
object Score
class ScoreSortDefinition extends SortDefinition {
    val builder = SortBuilders.scoreSort()
    def missing(missing: AnyRef) = {
        builder.missing(missing)
        this
    }
    def order(order: SortOrder) = {
        builder.order(order)
        this
    }
}