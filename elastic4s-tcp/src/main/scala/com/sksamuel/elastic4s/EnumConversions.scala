package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.admin.IndicesOptions
import com.sksamuel.elastic4s.script.ScriptType
import com.sksamuel.elastic4s.searches.aggs.pipeline.GapPolicy
import com.sksamuel.elastic4s.searches.aggs.{HistogramOrder, PercentilesMethod, SubAggCollectionMode}
import com.sksamuel.elastic4s.searches.queries.funcscorer.{CombineFunction, FieldValueFactorFunctionModifier, FunctionScoreQueryScoreMode, MultiValueMode}
import com.sksamuel.elastic4s.searches.queries.geo.{GeoDistance, GeoExecType, GeoValidationMethod}
import com.sksamuel.elastic4s.searches.queries.matches.ZeroTermsQuery
import com.sksamuel.elastic4s.searches.queries.{RegexpFlag, SimpleQueryStringFlag}
import com.sksamuel.elastic4s.searches.sort.{ScriptSortType, SortMode, SortOrder}
import com.sksamuel.elastic4s.searches.suggestion.{SortBy, StringDistanceImpl, SuggestMode}
import com.sksamuel.elastic4s.searches.{GeoPoint, QueryRescoreMode, ScoreMode, SearchType}
import org.elasticsearch.action.support.WriteRequest
import org.elasticsearch.common.lucene.search.function.{FieldValueFactorFunction, FiltersFunctionScoreQuery}
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.search.aggregations.BucketOrder

import scala.language.implicitConversions

object EnumConversions {

  def indicesopts(opts: IndicesOptions): org.elasticsearch.action.support.IndicesOptions = {
    org.elasticsearch.action.support.IndicesOptions.fromOptions(opts.ignoreUnavailable, opts.allowNoIndices, opts.expandWildcardsOpen, opts.expandWildcardClosed)
  }

  implicit def settings(map: Map[String, Any]): Settings = {
    map.foldLeft(Settings.builder) { case (settings, (key, value)) =>
      settings.put(key, value.toString)
    }.build()
  }

  implicit def simpleQueryStringFlag(simpleQueryStringFlag: SimpleQueryStringFlag): org.elasticsearch.index.query.SimpleQueryStringFlag = {
    simpleQueryStringFlag match {
      case SimpleQueryStringFlag.ALL => org.elasticsearch.index.query.SimpleQueryStringFlag.ALL
      case SimpleQueryStringFlag.AND => org.elasticsearch.index.query.SimpleQueryStringFlag.AND
      case SimpleQueryStringFlag.ESCAPE => org.elasticsearch.index.query.SimpleQueryStringFlag.ESCAPE
      case SimpleQueryStringFlag.FUZZY => org.elasticsearch.index.query.SimpleQueryStringFlag.FUZZY
      case SimpleQueryStringFlag.NEAR => org.elasticsearch.index.query.SimpleQueryStringFlag.NEAR
      case SimpleQueryStringFlag.NONE => org.elasticsearch.index.query.SimpleQueryStringFlag.NONE
      case SimpleQueryStringFlag.OR => org.elasticsearch.index.query.SimpleQueryStringFlag.OR
      case SimpleQueryStringFlag.PHRASE => org.elasticsearch.index.query.SimpleQueryStringFlag.PHRASE
      case SimpleQueryStringFlag.PRECEDENCE => org.elasticsearch.index.query.SimpleQueryStringFlag.PRECEDENCE
      case SimpleQueryStringFlag.PREFIX => org.elasticsearch.index.query.SimpleQueryStringFlag.PREFIX
      case SimpleQueryStringFlag.SLOP => org.elasticsearch.index.query.SimpleQueryStringFlag.SLOP
      case SimpleQueryStringFlag.WHITESPACE => org.elasticsearch.index.query.SimpleQueryStringFlag.WHITESPACE
    }
  }

  implicit def execType(execType: GeoExecType): org.elasticsearch.index.query.GeoExecType = {
    execType match {
      case GeoExecType.Indexed => org.elasticsearch.index.query.GeoExecType.INDEXED
      case GeoExecType.Memory => org.elasticsearch.index.query.GeoExecType.MEMORY
    }
  }

  implicit def percentilesMethod(percentilesMethod: PercentilesMethod): org.elasticsearch.search.aggregations.metrics.percentiles.PercentilesMethod = {
    percentilesMethod match {
      case PercentilesMethod.HDR => org.elasticsearch.search.aggregations.metrics.percentiles.PercentilesMethod.HDR
      case PercentilesMethod.TDigest => org.elasticsearch.search.aggregations.metrics.percentiles.PercentilesMethod.TDIGEST
    }
  }

  implicit def modifier(modifier: FieldValueFactorFunctionModifier): FieldValueFactorFunction.Modifier = {
    modifier match {
      case FieldValueFactorFunctionModifier.LN => FieldValueFactorFunction.Modifier.LN
      case FieldValueFactorFunctionModifier.LN1P => FieldValueFactorFunction.Modifier.LN1P
      case FieldValueFactorFunctionModifier.LN2P => FieldValueFactorFunction.Modifier.LN2P
      case FieldValueFactorFunctionModifier.LOG => FieldValueFactorFunction.Modifier.LOG
      case FieldValueFactorFunctionModifier.LOG1P => FieldValueFactorFunction.Modifier.LOG1P
      case FieldValueFactorFunctionModifier.LOG2P => FieldValueFactorFunction.Modifier.LOG2P
      case FieldValueFactorFunctionModifier.NONE => FieldValueFactorFunction.Modifier.NONE
      case FieldValueFactorFunctionModifier.RECIPROCAL => FieldValueFactorFunction.Modifier.RECIPROCAL
      case FieldValueFactorFunctionModifier.SQRT => FieldValueFactorFunction.Modifier.SQRT
      case FieldValueFactorFunctionModifier.SQUARE => FieldValueFactorFunction.Modifier.SQUARE
    }
  }

  implicit def operator(operator: Operator): org.elasticsearch.index.query.Operator = {
    operator match {
      case Operator.Or => org.elasticsearch.index.query.Operator.OR
      case Operator.And => org.elasticsearch.index.query.Operator.AND
    }
  }

  implicit def zeroTermsQuery(zeroTermsQuery: ZeroTermsQuery): org.elasticsearch.index.search.MatchQuery.ZeroTermsQuery = {
    zeroTermsQuery match {
      case ZeroTermsQuery.All => org.elasticsearch.index.search.MatchQuery.ZeroTermsQuery.ALL
      case ZeroTermsQuery.None => org.elasticsearch.index.search.MatchQuery.ZeroTermsQuery.NONE
    }
  }

  implicit def multiValueMode(mode: MultiValueMode): org.elasticsearch.search.MultiValueMode = {
    mode match {
      case MultiValueMode.Avg => org.elasticsearch.search.MultiValueMode.AVG
      case MultiValueMode.Max => org.elasticsearch.search.MultiValueMode.MAX
      case MultiValueMode.Median => org.elasticsearch.search.MultiValueMode.MEDIAN
      case MultiValueMode.Min => org.elasticsearch.search.MultiValueMode.MIN
      case MultiValueMode.Sum => org.elasticsearch.search.MultiValueMode.SUM
    }
  }

  implicit def functionScoreQueryScoreMode(mode: FunctionScoreQueryScoreMode): FiltersFunctionScoreQuery.ScoreMode = {
    mode match {
      case FunctionScoreQueryScoreMode.Avg => FiltersFunctionScoreQuery.ScoreMode.AVG
      case FunctionScoreQueryScoreMode.First => FiltersFunctionScoreQuery.ScoreMode.FIRST
      case FunctionScoreQueryScoreMode.Max => FiltersFunctionScoreQuery.ScoreMode.MAX
      case FunctionScoreQueryScoreMode.Min => FiltersFunctionScoreQuery.ScoreMode.MIN
      case FunctionScoreQueryScoreMode.Multiply => FiltersFunctionScoreQuery.ScoreMode.MULTIPLY
      case FunctionScoreQueryScoreMode.Sum => FiltersFunctionScoreQuery.ScoreMode.SUM
    }
  }

  implicit def fetchSource(fetch: FetchSourceContext): org.elasticsearch.search.fetch.subphase.FetchSourceContext = {
    if (fetch.fetchSource) {
      val inc = if (fetch.includes.isEmpty) null else fetch.includes
      val exc = if (fetch.excludes.isEmpty) null else fetch.excludes
      new org.elasticsearch.search.fetch.subphase.FetchSourceContext(fetch.fetchSource, inc, exc)
    } else {
      new org.elasticsearch.search.fetch.subphase.FetchSourceContext(false)
    }
  }

  implicit def sortBy(sortBy: SortBy): org.elasticsearch.search.suggest.SortBy = {
    sortBy match {
      case SortBy.Frequency => org.elasticsearch.search.suggest.SortBy.FREQUENCY
      case SortBy.Score => org.elasticsearch.search.suggest.SortBy.SCORE
    }
  }

  implicit def searchType(searchType: SearchType): org.elasticsearch.action.search.SearchType = {
    searchType match {
      case SearchType.DfsQueryThenFetch => org.elasticsearch.action.search.SearchType.DFS_QUERY_THEN_FETCH
      case SearchType.QueryThenFetch => org.elasticsearch.action.search.SearchType.QUERY_THEN_FETCH
    }
  }

  implicit def stringDistance(suggestMode: StringDistanceImpl): org.elasticsearch.search.suggest.term.TermSuggestionBuilder.StringDistanceImpl = {
    suggestMode match {
      case StringDistanceImpl.DAMERAU_LEVENSHTEIN => org.elasticsearch.search.suggest.term.TermSuggestionBuilder.StringDistanceImpl.DAMERAU_LEVENSHTEIN
      case StringDistanceImpl.INTERNAL => org.elasticsearch.search.suggest.term.TermSuggestionBuilder.StringDistanceImpl.INTERNAL
      case StringDistanceImpl.JAROWINKLER => org.elasticsearch.search.suggest.term.TermSuggestionBuilder.StringDistanceImpl.JAROWINKLER
      case StringDistanceImpl.LEVENSTEIN => org.elasticsearch.search.suggest.term.TermSuggestionBuilder.StringDistanceImpl.LEVENSTEIN
      case StringDistanceImpl.NGRAM => org.elasticsearch.search.suggest.term.TermSuggestionBuilder.StringDistanceImpl.NGRAM
    }
  }

  implicit def suggestMode(suggestMode: SuggestMode): org.elasticsearch.search.suggest.term.TermSuggestionBuilder.SuggestMode = {
    suggestMode match {
      case SuggestMode.Always => org.elasticsearch.search.suggest.term.TermSuggestionBuilder.SuggestMode.ALWAYS
      case SuggestMode.Popular => org.elasticsearch.search.suggest.term.TermSuggestionBuilder.SuggestMode.POPULAR
      case SuggestMode.Missing => org.elasticsearch.search.suggest.term.TermSuggestionBuilder.SuggestMode.MISSING
    }
  }

  implicit def refreshPolicy(refreshPolicy: RefreshPolicy): WriteRequest.RefreshPolicy = {
    refreshPolicy match {
      case RefreshPolicy.Immediate => WriteRequest.RefreshPolicy.IMMEDIATE
      case RefreshPolicy.None => WriteRequest.RefreshPolicy.NONE
      case RefreshPolicy.WaitFor => WriteRequest.RefreshPolicy.WAIT_UNTIL
    }
  }

  implicit def combineFunction(combineFunction: CombineFunction): org.elasticsearch.common.lucene.search.function.CombineFunction = {
    combineFunction match {
      case CombineFunction.Avg => org.elasticsearch.common.lucene.search.function.CombineFunction.AVG
      case CombineFunction.Max => org.elasticsearch.common.lucene.search.function.CombineFunction.MAX
      case CombineFunction.Min => org.elasticsearch.common.lucene.search.function.CombineFunction.MIN
      case CombineFunction.Multiply => org.elasticsearch.common.lucene.search.function.CombineFunction.MULTIPLY
      case CombineFunction.Replace => org.elasticsearch.common.lucene.search.function.CombineFunction.REPLACE
      case CombineFunction.Sum => org.elasticsearch.common.lucene.search.function.CombineFunction.SUM
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
    case GeoValidationMethod.Coerce => org.elasticsearch.index.query.GeoValidationMethod.COERCE
    case GeoValidationMethod.IgnoreMalformed => org.elasticsearch.index.query.GeoValidationMethod.IGNORE_MALFORMED
    case GeoValidationMethod.Strict => org.elasticsearch.index.query.GeoValidationMethod.STRICT
  }

  implicit def sortMode(sortMode: SortMode): org.elasticsearch.search.sort.SortMode = sortMode match {
    case SortMode.Avg => org.elasticsearch.search.sort.SortMode.AVG
    case SortMode.Max => org.elasticsearch.search.sort.SortMode.MAX
    case SortMode.Min => org.elasticsearch.search.sort.SortMode.MIN
    case SortMode.Median => org.elasticsearch.search.sort.SortMode.MEDIAN
    case SortMode.Sum => org.elasticsearch.search.sort.SortMode.SUM
  }

  implicit def scriptType(scriptType: ScriptType): org.elasticsearch.script.ScriptType = scriptType match {
    case ScriptType.Inline => org.elasticsearch.script.ScriptType.INLINE
    case ScriptType.Stored => org.elasticsearch.script.ScriptType.STORED
  }

  implicit def scoreMode(scoreMode: QueryRescoreMode): org.elasticsearch.search.rescore.QueryRescoreMode = scoreMode match {
    case QueryRescoreMode.Avg => org.elasticsearch.search.rescore.QueryRescoreMode.Avg
    case QueryRescoreMode.Max => org.elasticsearch.search.rescore.QueryRescoreMode.Max
    case QueryRescoreMode.Min => org.elasticsearch.search.rescore.QueryRescoreMode.Min
    case QueryRescoreMode.Multiply => org.elasticsearch.search.rescore.QueryRescoreMode.Multiply
    case QueryRescoreMode.Total => org.elasticsearch.search.rescore.QueryRescoreMode.Total
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

  implicit def histogramOrder(histogramOrder: HistogramOrder): BucketOrder = {
    histogramOrder match {
      case HistogramOrder.COUNT_ASC => BucketOrder.count(true)
      case HistogramOrder.COUNT_DESC => BucketOrder.count(false)
      case HistogramOrder.KEY_ASC => BucketOrder.key(true)
      case HistogramOrder.KEY_DESC => BucketOrder.key(false)
    }
  }

  implicit def versionType(versionType: VersionType): org.elasticsearch.index.VersionType = {
    versionType match {
      case VersionType.External => org.elasticsearch.index.VersionType.EXTERNAL
      case VersionType.ExternalGte => org.elasticsearch.index.VersionType.EXTERNAL_GTE
      case VersionType.Force => org.elasticsearch.index.VersionType.FORCE
      case VersionType.Internal => org.elasticsearch.index.VersionType.INTERNAL
    }
  }
}
