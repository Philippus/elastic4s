package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.searches.{GeoPoint, ScoreMode}
import com.sksamuel.elastic4s.searches.aggs.{SubAggCollectionMode, ValueType}
import com.sksamuel.elastic4s.searches.aggs.pipeline.GapPolicy
import com.sksamuel.elastic4s.searches.queries.RegexpFlag
import com.sksamuel.elastic4s.searches.queries.geo.{GeoDistance, GeoValidationMethod}
import com.sksamuel.elastic4s.searches.sort.{ScriptSortType, SortMode, SortOrder}
import org.elasticsearch.action.support.WriteRequest
import org.elasticsearch.common.settings.Settings

object EnumConversions {

  implicit def settings(map: Map[String, Any]): Settings = {
    map.foldLeft(Settings.builder) { case (settings, (key, value)) =>
      settings.put(key, value.toString)
    }.build()
  }

  implicit def refreshPolicy(refreshPolicy: RefreshPolicy): WriteRequest.RefreshPolicy = {
    refreshPolicy match {
      case RefreshPolicy.Immediate => WriteRequest.RefreshPolicy.IMMEDIATE
      case RefreshPolicy.None => WriteRequest.RefreshPolicy.NONE
      case RefreshPolicy.WaitFor => WriteRequest.RefreshPolicy.WAIT_UNTIL
    }
  }

  implicit def valueType(valueType: ValueType): org.elasticsearch.search.aggregations.support.ValueType = {
    valueType match {
      case ValueType.BOOLEAN => org.elasticsearch.search.aggregations.support.ValueType.BOOLEAN
      case ValueType.DATE => org.elasticsearch.search.aggregations.support.ValueType.DATE
      case ValueType.DOUBLE => org.elasticsearch.search.aggregations.support.ValueType.DOUBLE
      case ValueType.GEOPOINT => org.elasticsearch.search.aggregations.support.ValueType.GEOPOINT
      case ValueType.IP => org.elasticsearch.search.aggregations.support.ValueType.IP
      case ValueType.LONG => org.elasticsearch.search.aggregations.support.ValueType.LONG
      case ValueType.NUMBER => org.elasticsearch.search.aggregations.support.ValueType.NUMBER
      case ValueType.NUMERIC => org.elasticsearch.search.aggregations.support.ValueType.NUMERIC
      case ValueType.STRING => org.elasticsearch.search.aggregations.support.ValueType.STRING
    }
  }

  implicit def gapPolicy(policy: GapPolicy): org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy = {
    policy match {
      case GapPolicy.INSERT_ZEROS => org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy.INSERT_ZEROS
      case GapPolicy.SKIP => org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy.SKIP
    }
  }

  implicit def geo(point: GeoPoint): org.elasticsearch.common.geo.GeoPoint = new org.elasticsearch.common.geo.GeoPoint(point.lat, point.long)

  implicit def sortOrder(sortOrder: SortOrder): org.elasticsearch.search.sort.SortOrder = sortOrder match {
    case SortOrder.Asc => org.elasticsearch.search.sort.SortOrder.ASC
    case SortOrder.Desc => org.elasticsearch.search.sort.SortOrder.DESC
  }

  implicit def regexpFlags(flag: RegexpFlag): org.elasticsearch.index.query.RegexpFlag = flag match {
    case RegexpFlag.All => org.elasticsearch.index.query.RegexpFlag.ALL
    case RegexpFlag.AnyString => org.elasticsearch.index.query.RegexpFlag.ANYSTRING
    case RegexpFlag.Complement => org.elasticsearch.index.query.RegexpFlag.COMPLEMENT
    case RegexpFlag.Empty => org.elasticsearch.index.query.RegexpFlag.EMPTY
    case RegexpFlag.Intersection => org.elasticsearch.index.query.RegexpFlag.INTERSECTION
    case RegexpFlag.Interval => org.elasticsearch.index.query.RegexpFlag.INTERVAL
    case RegexpFlag.None => org.elasticsearch.index.query.RegexpFlag.NONE
  }

  implicit def distanceUnit(distanceUnit: DistanceUnit): org.elasticsearch.common.unit.DistanceUnit = distanceUnit match {
    case DistanceUnit.Centimeters => org.elasticsearch.common.unit.DistanceUnit.CENTIMETERS
    case DistanceUnit.Feet => org.elasticsearch.common.unit.DistanceUnit.FEET
    case DistanceUnit.Inch => org.elasticsearch.common.unit.DistanceUnit.INCH
    case DistanceUnit.Kilometers => org.elasticsearch.common.unit.DistanceUnit.KILOMETERS
    case DistanceUnit.Meters => org.elasticsearch.common.unit.DistanceUnit.METERS
    case DistanceUnit.Miles => org.elasticsearch.common.unit.DistanceUnit.MILES
    case DistanceUnit.Millimeters => org.elasticsearch.common.unit.DistanceUnit.MILLIMETERS
    case DistanceUnit.NauticalMiles => org.elasticsearch.common.unit.DistanceUnit.NAUTICALMILES
    case DistanceUnit.Yard => org.elasticsearch.common.unit.DistanceUnit.YARD
  }

  implicit def geoDistance(geoDistance: GeoDistance): org.elasticsearch.common.geo.GeoDistance = geoDistance match {
    case GeoDistance.Arc => org.elasticsearch.common.geo.GeoDistance.ARC
    case GeoDistance.Plane => org.elasticsearch.common.geo.GeoDistance.PLANE
  }

  implicit def geoValidationMethod(method: GeoValidationMethod): org.elasticsearch.index.query.GeoValidationMethod = method match {
    case GeoValidationMethod.COERCE => org.elasticsearch.index.query.GeoValidationMethod.COERCE
    case GeoValidationMethod.IGNORE_MALFORMED => org.elasticsearch.index.query.GeoValidationMethod.IGNORE_MALFORMED
    case GeoValidationMethod.STRICT => org.elasticsearch.index.query.GeoValidationMethod.STRICT
  }

  implicit def sortMode(sortMode: SortMode): org.elasticsearch.search.sort.SortMode = sortMode match {
    case SortMode.Avg => org.elasticsearch.search.sort.SortMode.AVG
    case SortMode.Max => org.elasticsearch.search.sort.SortMode.MAX
    case SortMode.Min => org.elasticsearch.search.sort.SortMode.MIN
    case SortMode.Median => org.elasticsearch.search.sort.SortMode.MEDIAN
    case SortMode.Sum => org.elasticsearch.search.sort.SortMode.SUM
  }

  implicit def scoreMode(scoreMode: ScoreMode): org.apache.lucene.search.join.ScoreMode = scoreMode match {
    case ScoreMode.Avg => org.apache.lucene.search.join.ScoreMode.Avg
    case ScoreMode.Max => org.apache.lucene.search.join.ScoreMode.Max
    case ScoreMode.Min => org.apache.lucene.search.join.ScoreMode.Min
    case ScoreMode.None => org.apache.lucene.search.join.ScoreMode.None
    case ScoreMode.Total => org.apache.lucene.search.join.ScoreMode.Total
  }

  implicit def scriptSortType(sortType: ScriptSortType): org.elasticsearch.search.sort.ScriptSortBuilder.ScriptSortType = sortType match {
    case ScriptSortType.String => org.elasticsearch.search.sort.ScriptSortBuilder.ScriptSortType.STRING
    case ScriptSortType.Number => org.elasticsearch.search.sort.ScriptSortBuilder.ScriptSortType.NUMBER
  }

  implicit def collectMode(mode: SubAggCollectionMode): org.elasticsearch.search.aggregations.Aggregator.SubAggCollectionMode = mode match {
    case SubAggCollectionMode.BreadthFirst => org.elasticsearch.search.aggregations.Aggregator.SubAggCollectionMode.BREADTH_FIRST
    case SubAggCollectionMode.DepthFirst => org.elasticsearch.search.aggregations.Aggregator.SubAggCollectionMode.DEPTH_FIRST
  }
}
