package com.sksamuel.elastic4s

import org.elasticsearch.search.sort.{SortBuilders, SortBuilder, SortOrder}
import org.elasticsearch.common.unit.DistanceUnit
import org.elasticsearch.common.geo.{GeoPoint, GeoDistance}
import com.sksamuel.elastic4s.MultiMode.Min

/** @author Stephen Samuel */
sealed abstract class Sort(asc: Boolean) {
    def builder: SortBuilder
}
case class FieldSort(field: String,
                     ignoreUnmapped: Boolean = true,
                     missing: Option[AnyRef] = None,
                     asc: Boolean = true,
                     mode: MultiMode = Min)
  extends Sort(asc) {
    def builder = {
        val builder = SortBuilders
          .fieldSort(field)
          .ignoreUnmapped(ignoreUnmapped)
          .order(if (asc) SortOrder.ASC else SortOrder.DESC)
          .sortMode(mode.toString)
        missing.foreach(builder.missing(_))
        builder
    }
}
case class ScoreSort(asc: Boolean = true, missing: Option[AnyRef] = None) extends Sort(asc) {
    def builder = {
        val builder = SortBuilders.scoreSort().order(if (asc) SortOrder.ASC else SortOrder.DESC)
        missing.foreach(builder.missing(_))
        builder
    }

}
case class ScriptSort(script: String,
                      `type`: String,
                      missing: Option[AnyRef] = None,
                      params: Map[String, Any] = Map.empty,
                      asc: Boolean = true) extends Sort(asc) {
    def builder = {
        val builder = SortBuilders.scriptSort(script, `type`).order(if (asc) SortOrder.ASC else SortOrder.DESC)
        missing.foreach(builder.missing(_))
        params.foreach(arg => builder.param(arg._1, arg._2))
        builder
    }
}
case class GeoDistanceSort(field: String,
                           geoDistance: Option[GeoDistance] = None,
                           geoPoint: Option[GeoPoint] = None,
                           geoHash: Option[String] = None,
                           unit: Option[DistanceUnit] = None,
                           asc: Boolean = true) extends Sort(asc) {
    def builder = {
        val builder = SortBuilders.geoDistanceSort(field).order(if (asc) SortOrder.ASC else SortOrder.DESC)
        geoDistance.foreach(builder.geoDistance(_))
        geoPoint.foreach(arg => builder.point(arg.lat, arg.lon))
        geoHash.foreach(builder.geohash(_))
        unit.foreach(builder.unit(_))
        builder
    }
}