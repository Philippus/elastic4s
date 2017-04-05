package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.Preference.Shards
import com.sksamuel.elastic4s.analyzers.{FrenchLanguageAnalyzer, SnowballAnalyzer, WhitespaceAnalyzer}
import com.sksamuel.elastic4s._
import org.apache.lucene.search.join.ScoreMode
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.cluster.routing.Preference
import org.elasticsearch.common.geo.{GeoDistance, GeoPoint}
import org.elasticsearch.common.unit.DistanceUnit
import org.elasticsearch.index.query.{MultiMatchQueryBuilder, RegexpFlag, SimpleQueryStringFlag}
import org.elasticsearch.index.search.MatchQuery.ZeroTermsQuery
import org.elasticsearch.search.MultiValueMode
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import org.elasticsearch.search.fetch.subphase.FetchSourceContext
import org.elasticsearch.search.sort.ScriptSortBuilder.ScriptSortType
import org.elasticsearch.search.sort.{SortMode, SortOrder}
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder.SuggestMode
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, OneInstancePerTest}

class SearchDslTest extends FlatSpec with MockitoSugar with JsonSugar with OneInstancePerTest {

  import com.sksamuel.elastic4s.ElasticDsl._

  "the search dsl" should "accept wildcards for index and types" in {
    val req = search("*") types "*" limit 10
    req.show should matchJsonResource("/json/search/search_test1.json")
  }

  it should "accept sequences for indexes" in {
    val req = search("twitter", "other") types "*" limit 5 query "coldplay"
    req.show should matchJsonResource("/json/search/search_test2.json")
  }

  it should "accept sequences for type" in {
    val req = search("*") types("users", "tweets") from 5 query "sammy"
    req.show should matchJsonResource("/json/search/search_test3.json")
  }

  it should "use limit and and offset when specified" in {
    val req = search("*") types("users", "tweets") limit 6 from 9 query "coldplay"
    req.show should matchJsonResource("/json/search/search_test4.json")
  }

  it should "use terminateAfter when specified" in {
    val req = search("*") types("users", "tweets") terminateAfter 5 query "coldplay"
    req.show should matchJsonResource("/json/search/search_test_terminate_after.json")
  }

  it should "use include _source when fetchSource=true" ignore {
    val req = search("*") types("users", "tweets") fetchSource true query "coldplay"
    req.show should matchJsonResource("/json/search/search_test_fetch_source_true.json")
  }

  it should "not include _source when fetchSource=false" ignore {
    val req = search("*") types("users", "tweets") fetchSource false query "coldplay"
    req.show should matchJsonResource("/json/search/search_test_fetch_source_false.json")
  }

  it should "use preference when specified" in {
    val req = search("*") types("users", "tweets") query "coldplay" preference Preference.PRIMARY_FIRST
    req.show should matchJsonResource("/json/search/search_preference_primary_first.json")
  }

  it should "generate wrapped query for a raw query" in {
    val req = search("*") types("users", "tweets") limit 5 rawQuery {
      """{ "prefix": { "bands": { "prefix": "coldplay", "boost": 5.0, "rewrite": "yes" } } }"""
    } searchType SearchType.DFS_QUERY_THEN_FETCH
    req.show should matchJsonResource("/json/search/search_raw_json.json")
  }

  it should "generate json for a prefix query" in {
    val req = search("*") types("users", "tweets") limit 5 query {
      prefixQuery("bands" -> "coldplay") boost 5 rewrite "yes"
    } searchType SearchType.QUERY_THEN_FETCH
    req.show should matchJsonResource("/json/search/search_prefix_query.json")
  }

  it should "generate json for a term query" in {
    val req = search("*") types("users", "tweets") limit 5 query {
      termQuery("singer", "chris martin") boost 1.6
    } searchType SearchType.DEFAULT
    req.show should matchJsonResource("/json/search/search_term.json")
  }

  it should "generate json for a range query" in {
    val req = search("*") types("users", "tweets") limit 5 query {
      rangeQuery("coldplay") includeLower true includeUpper true from 4 to 10 boost 1.2
    }
    req.show should matchJsonResource("/json/search/search_range.json")
  }

  it should "generate json for a wildcard query" in {
    val req = search("*") types("users", "tweets") limit 5 query {
      wildcardQuery("name", "*coldplay") boost 7.6 rewrite "no"
    }
    req.show should matchJsonResource("/json/search/search_wildcard.json")
  }

  it should "generate json for a string query" in {
    val req = search("*") types("users", "tweets") limit 5 query {
      stringQuery("coldplay") allowLeadingWildcard true analyzeWildcard true analyzer WhitespaceAnalyzer autoGeneratePhraseQueries true defaultField "name" boost 6.5 enablePositionIncrements true fuzzyMaxExpansions 4 fuzzyPrefixLength 3 lenient true phraseSlop 10 tieBreaker 0.5 operator "OR" rewrite "writer"
    }
    req.show should matchJsonResource("/json/search/search_string.json")
  }

  it should "generate json for a regex query" in {
    val req = search("*") types("users", "tweets") limit 5 query {
      regexQuery("drummmer" -> "will*") boost 4 flags RegexpFlag.INTERSECTION rewrite "rewrite-to"
    }
    req.show should matchJsonResource("/json/search/search_regex.json")
  }

  it should "generate json for a min score" in {
    val req = search("*") types("users", "tweets") query "coldplay" minScore 0.5
    req.show should matchJsonResource("/json/search/search_minscore.json")
  }

  it should "generate json for an index boost" ignore {
    val req = search("*") types("users", "tweets") query "coldplay" indexBoost("index1" -> 1.4, "index2" -> 1.3)
    req.show should matchJsonResource("/json/search/search_indexboost.json")
  }

  it should "generate json for a boosting query" in {
    val req = search("*") types("users", "tweets") limit 5 query {
      boostingQuery(stringQuery("coldplay"), stringQuery("jethro tull")) negativeBoost 5.6 boost 7.6
    }
    req.show should matchJsonResource("/json/search/search_boosting.json")
  }

  it should "generate json for a id query" in {
    val req = search("*") types("users", "tweets") limit 5 query {
      idsQuery("1", "2", "3") boost 1.6 types("a", "b")
    }
    req.show should matchJsonResource("/json/search/search_id.json")
  }

  it should "generate json for a match query" in {
    val req = search("*") types("users", "tweets") limit 5 query {
      matchQuery("name", "coldplay")
        .cutoffFrequency(3.4)
        .fuzzyTranspositions(true)
        .maxExpansions(4)
        .withAndOperator()
        .zeroTermsQuery("all")
        .minimumShouldMatch("75%")
        .fuzziness("2")
        .prefixLength(4)
        .analyzer(FrenchLanguageAnalyzer)
    }
    req.show should matchJsonResource("/json/search/search_match.json")
  }

  it should "generate json for a match query with default as or" in {
    val req = search("*") types("users", "tweets") limit 5 query {
      matchQuery("drummmer", "will") boost 4 operator "OR"
    }
    req.show should matchJsonResource("/json/search/search_match_or.json")
  }

  it should "generate json for a fuzzy query" in {
    val req = search("*") types("users", "tweets") limit 5 query {
      fuzzyQuery("drummmer", "will") boost 4 maxExpansions 10 prefixLength 10 transpositions true
    }
    req.show should matchJsonResource("/json/search/search_fuzzy.json")
  }

  it should "generate json for a match all query" in {
    val req = search("*") types("users", "tweets") limit 5 query {
      matchAllQuery boost 4
    }
    req.show should matchJsonResource("/json/search/search_match_all.json")
  }

  it should "generate json for a hasChild query" in {
    val req = search("*") types("users", "tweets") limit 5 query {
      hasChildQuery("sometype") query {
        "coldplay"
      } scoreMode ScoreMode.Avg boost 1.2
    }
    req.show should matchJsonResource("/json/search/search_haschild_query.json")
  }

  it should "generate json for a hasParent query" in {
    val req = search("*") types("users", "tweets") limit 5 query {
      hasParentQuery("sometype") query {
        "coldplay"
      } scoreMode true boost 1.2
    } 
    req.show should matchJsonResource("/json/search/search_hasparent_query.json")
  }

  it should "generate json for a boolean compound query" in {
    val req = search("*") types("bands", "artists") limit 5 query {
      boolQuery().must(
        regexQuery("drummmer" -> "will*") boost 5 flags RegexpFlag.ANYSTRING,
        termQuery("singer" -> "chris")
      ) should termQuery("bassist" -> "berryman") not termQuery("singer" -> "anderson")
    }
    req.show should matchJsonResource("/json/search/search_boolean.json")
  }

  it should "generate json for a boolean query" in {
    val req = search("space" -> "planets") limit 5 query {
      boolQuery().must(
        regexQuery("drummmer" -> "will*") boost 5,
        termQuery("singer" -> "chris")
      ).should {
        termQuery("bassist" -> "berryman")
      }.not {
        termQuery("singer" -> "anderson")
      } boost 2.4 minimumShouldMatch 2 adjustPureNegative false disableCoord true queryName "booly"
    }
    req.show should matchJsonResource("/json/search/search_boolean2.json")
  }

  it should "generate json for a boolean query with filter" in {
    val req = search("*") limit 5 query {
      bool {
        must(termQuery("title", "Search")).filter(termQuery("status", "published"))
      }
    }
    req.show should matchJsonResource("/json/search/search_boolean_with_filter.json")
  }

  it should "generate json for a match phrase query" in {
    val req = search("*").types("bands", "artists").limit(5).query {
      matchPhraseQuery("name", "coldplay")
        .slop(3)
        .boost(15)
        .analyzer(SnowballAnalyzer)
    }
    req.show should matchJsonResource("/json/search/search_match_phrase.json")
  }

  it should "generate json for a match phrase prefix query" in {
    val req = search("*").types("bands", "artists").limit(5).query {
      matchPhrasePrefixQuery("name", "coldplay")
        .maxExpansions(3)
        .slop(3)
        .analyzer(SnowballAnalyzer)
    }
    req.show should matchJsonResource("/json/search/search_match_phrase_prefix.json")
  }

  it should "generate json for term post filter" in {
    val req = search("music") types "bands" postFilter {
      termQuery("singer", "chris martin") queryName "namey"
    }
    req.show should matchJsonResource("/json/search/search_term_filter.json")
  }

  it should "generate json for terms lookup filter" in {
    val req = search("music") types "bands" postFilter {
      termsQuery("user", "val", "vallllll").queryName("namey")
    }
    req.show should matchJsonResource("/json/search/search_terms_lookup_filter.json")
  }

  it should "generate json for int terms lookup filter" in {
    val req = search("music") types "bands" postFilter {
      termsQuery("formedYear", 2013, 2014)
    }
    req.show should matchJsonResource("/json/search/search_int_terms_lookup_filter.json")
  }

  it should "generate json for long terms lookup filter" in {
    val req = search("music") types "bands" postFilter {
      termsQuery("formedYear", 2013L, 2014L)
    }
    req.show should matchJsonResource("/json/search/search_long_terms_lookup_filter.json")
  }

  it should "generate json for terms lookup filter using iterable" in {
    val req = search("music") types "bands" postFilter {
      termsQuery("user", Iterable("val", "vallllll")).queryName("namey")
    }
    req.show should matchJsonResource("/json/search/search_terms_lookup_filter.json")
  }

  it should "generate json for int terms lookup filter using iterable" in {
    val req = search("music") types "bands" postFilter {
      termsQuery("formedYear", Iterable(2013, 2014))
    }
    req.show should matchJsonResource("/json/search/search_int_terms_lookup_filter.json")
  }

  it should "generate json for long terms lookup filter using iterable" in {
    val req = search("music") types "bands" postFilter {
      termsQuery("formedYear", Iterable(2013L, 2014L))
    }
    req.show should matchJsonResource("/json/search/search_long_terms_lookup_filter.json")
  }

  it should "generate json for script filter" in {
    val req = search("music") types "bands" postFilter {
      scriptQuery("doc['creationYear'].value > 2013")
    }
    req.show should matchJsonResource("/json/search/search_script_filter.json")
  }

  it should "generate json for regex query post filter" in {
    val req = search("music") types "bands" postFilter {
      regexQuery("singer", "chris martin")
    } preference com.sksamuel.elastic4s.Preference.PreferNode("a")
    req.show should matchJsonResource("/json/search/search_regex_query.json")
  }

  it should "generate json for prefix query post filter" in {
    val req = search("music") types "bands" postFilter {
      prefixQuery("singer", "chris martin")
    }
    req.show should matchJsonResource("/json/search/search_prefix_filter.json")
  }

  it should "generate json for has child query with filter" in {
    val req = search("music") types "bands" postFilter {
      hasChildQuery("singer").query {
        termQuery("name", "chris")
      }.scoreMode(ScoreMode.Min).minMaxChildren(2, 4).boost(2.3).queryName("namey")
    }
    req.show should matchJsonResource("/json/search/search_haschild_filter.json")
  }

  it should "generate json for has parent query with filter" in {
    val req = search("music") types "bands" postFilter {
      hasParentQuery("singer").query {
        termQuery("name", "chris")
      }.scoreMode(true).boost(2.3).queryName("spidername")
    }
    req.show should matchJsonResource("/json/search/search_hasparent_filter.json")
  }

  it should "generate json for nested query with query" in {
    val req = search("music") types "bands" postFilter {
      nestedQuery("singer").query {
        termQuery("name", "chris")
      }.scoreMode("Min") queryName "namey"
    }
    req.show should matchJsonResource("/json/search/search_nested_filter.json")
  }

  it should "generate json for has child query with query" in {
    val req = search("music") types "bands" postFilter {
      hasChildQuery("singer") query {
        termQuery("name", "chris")
      } scoreMode ScoreMode.Min queryName "namey"
    }
    req.show should matchJsonResource("/json/search/search_haschild_filter_query.json")
  }

  it should "generate json for has parent query with query" in {
    val req = search("music") types "bands" postFilter {
      hasParentQuery("singer") query {
        termQuery("name", "chris")
      } scoreMode true queryName "namey"
    }
    req.show should matchJsonResource("/json/search/search_hasparent_filter_query.json")
  }

  it should "generate json for nested filter with query" in {
    val req = search("music") types "bands" postFilter {
      nestedQuery("singer") query {
        termQuery("name", "chris")
      } scoreMode "Min"
    }
    req.show should matchJsonResource("/json/search/search_nested_filter_query.json")
  }

  it should "generate json for id filter" in {
    val req = search("music") types "bands" postFilter {
      idsQuery("a", "b", "c").types("x", "y", "z")
    }
    req.show should matchJsonResource("/json/search/search_id_filter.json")
  }

  it should "generate json for type filter" in {
    val req = search("music") types "bands" postFilter {
      typeQuery("sometype")
    } preference com.sksamuel.elastic4s.Preference.Shards("5", "7")
    req.show should matchJsonResource("/json/search/search_type_filter.json")
  }

  it should "generate json for range filter" in {
    val req = search("music") types "bands" postFilter {
      rangeQuery("released") includeLower true includeUpper true gte "2010-01-01" lte "2012-12-12"
    } preference Shards("5", "7")
    req.show should matchJsonResource("/json/search/search_range_filter.json")
  }

  it should "generate json for field sort" in {
    val req = search("music") types "bands" sortBy {
      fieldSort("singer") missing "no-singer" order SortOrder.DESC mode SortMode.AVG nestedPath "nest"
    }
    req.show should matchJsonResource("/json/search/search_sort_field.json")
  }

  it should "generate json for nested field sort" in {
    val req = search("music") types "bands" sortBy {
      fieldSort("singer.weight") order SortOrder.DESC mode SortMode.SUM nestedFilter {
        termQuery("singer.name", "coldplay")
      }
    }
    req.show should matchJsonResource("/json/search/search_sort_nested_field.json")
  }

  it should "generate correct json for score sort" in {
    val req = search("music") types "bands" sortBy {
      scoreSort().order(SortOrder.ASC)
    }
    req.show should matchJsonResource("/json/search/search_sort_score.json")
  }

  it should "generate correct json for script sort" in {
    val req = search("music") types "bands" sortBy {
      scriptSort(script("document.score").lang("java")) typed "number" order SortOrder
        .DESC nestedPath "a.b.c" sortMode "min"
    } preference com.sksamuel.elastic4s.Preference.Custom("custom-node")
    req.show should matchJsonResource("/json/search/search_sort_script.json")
  }

  it should "generate correct json for script sort with params" in {
    val req = search("music") types "bands" sortBy {
      scriptSort(script("doc.score")
        .params(Map("param1" -> "value1", "param2" -> "value2"))) typed "number" order SortOrder.DESC
    } preference com.sksamuel.elastic4s.Preference.Custom("custom-node")
    req.show should matchJsonResource("/json/search/search_sort_script_params.json")
  }

  it should "generate correct json for geo sort" in {
    val req = search("music") types "bands" sortBy {
      geoSort("location").points("ABCDEFG").sortMode(SortMode.MAX).geoDistance(GeoDistance.ARC)
    }
    req.show should matchJsonResource("/json/search/search_sort_geo.json")
  }

  it should "generate correct json for geo sort with points" in {
    val lat = 269.9986267089844
    val lon = 539.9986267089844
    val req = search("music") types "bands" sortBy {
      geoSort("location").points(List(new GeoPoint(lat, lon))).mode(SortMode.MAX).geoDistance(GeoDistance.ARC)
    }
    req.show should matchJsonResource("/json/search/search_sort_geo.json")
  }

  it should "generate correct json for multiple sorts" in {
    val sorts = Seq(
      scriptSort("document.score") typed ScriptSortType.STRING order SortOrder.ASC,
      scoreSort().order(SortOrder.DESC),
      fieldSort("dancer") order SortOrder.DESC
    )
    val req = search("music") types "bands" sortBy sorts
    req.show should matchJsonResource("/json/search/search_sort_multiple.json")
  }

  it should "generate json for field sort with score tracking enabled" in {
    val req = search("music") types "bands" trackScores true sortBy {
      fieldSort("singer") order SortOrder.DESC
    }
    req.show should matchJsonResource("/json/search/search_sort_track_scores.json")
  }

  it should "generate correct json for geo bounding box filter" in {
    val req = search("music" / "bands").postFilter {
      geoBoxQuery("box").withCorners(40.6, 56.5, 33.5, 112.55)
    }
    req.show should matchJsonResource("/json/search/search_filter_geo_boundingbox.json")
  }

  it should "generate correct json for dismax query" in {
    val req = search("music") types "bands" query {
      dismax("coldplay", "london") boost 4.5 tieBreaker 1.2
    }
    req.show should matchJsonResource("/json/search/search_query_dismax.json")
  }

  it should "generate correct json for common terms query" in {
    val req = search("music") types "bands" query {
      commonQuery("name") text "some text here" analyzer WhitespaceAnalyzer boost 12.3 cutoffFrequency 14.4 highFreqOperator "AND" lowFreqOperator "OR" lowFreqMinimumShouldMatch "3<80%" highFreqMinimumShouldMatch 2
    }
    req.show should matchJsonResource("/json/search/search_query_commonterms.json")
  }

  it should "generate correct json for constant score query" in {
    val req = search("music") types "bands" query {
      constantScoreQuery {
        termQuery("name", "sammy")
      } boost 14.5
    }
    req.show should matchJsonResource("/json/search/search_query_constantscore.json")
  }

  it should "generate correct json for terms query" in {
    val req = search("music") types "bands" query {
      termsQuery("name", "chris", "will", "johnny", "guy") boost 1.2
    }
    req.show should matchJsonResource("/json/search/search_query_terms.json")
  }

  it should "generate correct json for multi match query" in {
    val req = search("music") types "bands" query {
      multiMatchQuery("this is my query") fields("name", "location", "genre") analyzer WhitespaceAnalyzer boost
        3.4 cutoffFrequency 1.7 fuzziness
        "something" prefixLength 4 minimumShouldMatch 2 tieBreaker 4.5 zeroTermsQuery
        ZeroTermsQuery.ALL fuzzyRewrite "some-rewrite" maxExpansions 4 lenient true prefixLength 4 operator "AND" matchType MultiMatchQueryBuilder.Type.CROSS_FIELDS
    }
    req.show should matchJsonResource("/json/search/search_query_multi_match.json")
  }

  it should "generate correct json for multi match query with minimum should match text clause" in {
    val req = search("music") types "bands" query {
      multiMatchQuery("this is my query") fields("name", "location", "genre") minimumShouldMatch "2<-1 5<80%" matchType "best_fields"
    }
    req.show should matchJsonResource("/json/search/search_query_multi_match_minimum_should_match.json")
  }

  it should "generate correct json for geo distance filter" in {
    val req = search("music") types "bands" postFilter {
      bool(
        should(
          geoDistanceQuery("distance") point(10.5d, 35.0d) geoDistance GeoDistance.PLANE geohash "geo1234" distance "120mi"
        ) not (
          geoDistanceQuery("distance").point(45.4d, 76.6d) distance(45, DistanceUnit.YARD)
          )
      )
    }
    req.show should matchJsonResource("/json/search/search_filter_geo_distance.json")
  }

  it should "generate correct json for a rescore query" in {
    val req = search("music") types "bands" rescore {
      rescore("coldplay").originalQueryWeight(1.4).rescoreQueryWeight(5.4).scoreMode("Max").window(14)
    }
    req.show should matchJsonResource("/json/search/search_rescore.json")
  }

  it should "generate correct json for function score query" ignore {

    val scorers = Seq(
      randomScore(1234).weight(1.2),
      scriptScore("some script here").weight(0.5),
      gaussianScore("field1", "1m", "2m").multiValueMode(MultiValueMode.MEDIAN),
      fieldFactorScore("field2").factor(1.2)
    )

    val req = search("music") types "bands" query {
      functionScoreQuery("coldplay").scoreMode("multiply").minScore(1.2).scorers(
        scorers
      ).boost(1.4).maxBoost(1.9).boostMode("max")
    }
    req.show should matchJsonResource("/json/search/search_function_score.json")
  }

  it should "generate correct json for geo polygon filter" in {
    val req = search("music") types "bands" postFilter {
      geoPolygonQuery(
        "distance",
        new GeoPoint(10, 10),
        new GeoPoint(20, 20),
        new GeoPoint(30, 30)
      )
    }
    req.show should matchJsonResource("/json/search/search_filter_geo_polygon.json")
  }

  it should "generate correct json for a boolean filter" in {
    val req = search("music") types "bands" postFilter {
      must {
        termQuery("name", "sammy")
      } should {
        termQuery("location", "oxford")
      } not {
        termQuery("type", "rap")
      }
    }
    req.show should matchJsonResource("/json/search/search_filter_bool.json")
  }

  it should "generate correct json for datehistogram aggregation" in {
    val req = search("music") types "bands" aggs {
      dateHistogramAggregation("years") field "date" interval DateHistogramInterval.YEAR minDocCount 0
    }
    req.show should matchJsonResource("/json/search/search_aggregations_datehistogram.json")
  }

  it should "generate correct json for range aggregation" ignore {
    val req = search("music") types "bands" aggs {
      rangeAggregation("range_agg") field "score" range(10.0, 15.0)
    }
    req.show should matchJsonResource("/json/search/search_aggregations_range.json")
  }

  it should "generate correct json for date range aggregation" in {
    val req = search("music") types "bands" aggs {
      dateRangeAggregation("daterange_agg") field "date" range("now-1Y", "now")
    }
    req.show should matchJsonResource("/json/search/search_aggregations_daterange.json")
  }

  it should "generate correct json for date range aggregation with unbounded from" in {
    val req = search("music") types "bands" aggs {
      dateRangeAggregation("daterange_agg") field "date" unboundedFrom("key", "now-1Y")
    }
    req.show should matchJsonResource("/json/search/search_aggregations_daterange_from.json")
  }

  it should "generate correct json for date range aggregation with unbounded to" ignore {
    val req = search("music") types "bands" aggs {
      dateRangeAggregation(
        "daterange_agg"
      ) field "date" unboundedTo("key", "now")
    }
    req.show should matchJsonResource("/json/search/search_aggregations_daterange_to.json")
  }

  it should "generate correct json for histogram aggregation" in {
    val req = search("music") types "bands" aggs {
      histogramAggregation("score_histogram") field "score" interval 2
    }
    req.show should matchJsonResource("/json/search/search_aggregations_histogram.json")
  }

  it should "generate correct json for filter aggregation" in {
    val req = search("music") types "bands" aggs {
      filterAggregation("my_filter_agg").query {
        must {
          termQuery("name", "sammy")
        } should {
          termQuery("location", "oxford")
        } not {
          termQuery("type", "rap")
        }
      }
    }
    req.show should matchJsonResource("/json/search/search_aggregations_filter.json")
  }

  it should "generate correct json for terms aggregation" ignore {
    val req = search("music") types "bands" aggs {
      termsAggregation("my_terms_agg") field "keyword" size 10 order Terms.Order.count(false)
    }
    req.show should matchJsonResource("/json/search/search_aggregations_terms.json")
  }

  it should "generate correct json for top hits aggregation" ignore {
    val req = search("music") types "bands" aggs {
      termsAggregation("top-tags") field "tags" size 3 order Terms.Order.count(true) subAggregation (
        topHitsAggregation("top_tag_hits") size 1 sortBy {
          fieldSort("last_activity_date") order SortOrder.DESC
        } fetchSource(Array("title"), Array.empty)
        )
    }
    req.show should matchJsonResource("/json/search/search_aggregations_top_hits.json")
  }

  it should "generate correct json for geobounds aggregation" ignore {
    val req = search("music") types "bands" aggs {
      geoBoundsAggregation("geo_agg") field "geo_point"
    }
    req.show should matchJsonResource("/json/search/search_aggregations_geobounds.json")
  }

  it should "generate correct json for geodistance aggregation" ignore {
    val req = search("music") types "bands" aggs {
      geoDistanceAggregation("geo_agg") origin(45.0, 27.0) field "geo_point" geoDistance GeoDistance.ARC range(1.0, 1.0)
    }
    req.show should matchJsonResource("/json/search/search_aggregations_geodistance.json")
  }

  it should "generate correct json for sub aggregation" ignore {
    val req = search("music") types "bands" aggs {
      aggregation datehistogram "days" field "date" interval DateHistogramInterval.DAY subAggregations(
        termsAggregation("keywords") field "keyword" size 5,
        termsAggregation("countries") field "country")
    }
    req.show should matchJsonResource("/json/search/search_aggregations_datehistogram_subs.json")
  }

  it should "generate correct json for min aggregation" in {
    val req = search("school") types "student" aggs {
      minAggregation("grades_min") field "grade" script {
        script("doc['grade'].value").lang("lua").param("apple", "bad")
      }
    }
    req.show should matchJsonResource("/json/search/search_aggregations_min.json")
  }

  it should "generate correct json for max aggregation" in {
    val req = search("school") types "student" aggs {
      maxAggregation("grades_max") field "grade" script {
        script("doc['grade'].value").lang("lua")
      }
    }
    req.show should matchJsonResource("/json/search/search_aggregations_max.json")
  }

  it should "generate correct json for sum aggregation" in {
    val req = search("school") types "student" aggs {
      sumAggregation("grades_sum") field "grade" script {
        script("doc['grade'].value").lang("lua") params Map("classsize" -> "30", "room" -> "101A")
      }
    }
    req.show should matchJsonResource("/json/search/search_aggregations_sum.json")
  }

  it should "generate correct json for avg aggregation" in {
    val req = search("school") types "student" aggs {
      aggregation avg "grades_avg" field "grade" script {
        script("doc['grade'].value").lang("lua")
      }
    }
    req.show should matchJsonResource("/json/search/search_aggregations_avg.json")
  }

  it should "generate correct json for stats aggregation" in {
    val req = search("school") aggs {
      aggregation stats "grades_stats" field "grade" script {
        script("doc['grade'].value").lang("lua")
      }
    }
    req.show should matchJsonResource("/json/search/search_aggregations_stats.json")
  }

  it should "generate correct json for extendedstats aggregation" in {
    val req = search("school") aggs {
      aggregation extendedstats "grades_extendedstats" field "grade" script {
        script("doc['grade'].value").lang("lua")
      }
    }
    req.show should matchJsonResource("/json/search/search_aggregations_extendedstats.json")
  }

  it should "generate correct json for percentiles aggregation" ignore {
    val req = search("school") aggs {
      aggregation percentiles "grades_percentiles" field "grade" percents(95, 99, 99.9) compression 200
    }
    req.show should matchJsonResource("/json/search/search_aggregations_percentiles.json")
  }

  it should "generate correct json for percentileranks aggregation" in {
    val req = search("school") aggs {
      aggregation percentileranks "grades_percentileranks" field "grade" percents(95, 99, 99.9) compression 200
    }
    req.show should matchJsonResource("/json/search/search_aggregations_percentileranks.json")
  }

  it should "generate correct json for value count aggregation" in {
    val req = search("school") aggs {
      aggregation count "grades_count" field "grade" script {
        script("doc['grade'].value").lang("lua")
      }
    }
    req.show should matchJsonResource("/json/search/search_aggregations_count.json")
  }

  it should "generate correct json for cardinality aggregation" ignore {
    val req = search("school") aggs {
      aggregation cardinality "grades_cardinality" field "grade" precisionThreshold 40000
    }
    req.show should matchJsonResource("/json/search/search_aggregations_cardinality.json")
  }

  it should "generate correct json for nested aggregation" ignore {
    val req = search("music") aggs {
      aggregation nested "nested_agg" path "nested_obj" subAggregations {
        aggregation terms "my_nested_terms_agg" field "keyword"
      }
    }
    req.show should matchJsonResource("/json/search/search_aggregations_nested.json")
  }

  it should "generate correct json for highlighting" in {
    val req = search("music").highlighting(
      highlightOptions().tagsSchema("styled") boundaryChars "\\b" boundaryMaxScan 4 order "score" preTags
        "<b>" postTags "</b>" encoder "html",
      "name" fragmentSize 100 numberOfFragments 3 fragmentOffset 4,
      "type" numberOfFragments 100 fragmentSize 44 highlighterType "some-type"
    )
    req.show should matchJsonResource("/json/search/search_highlighting.json")
  }

  it should "generate correct json for multiple suggestions" in {
    val req = search("music") types "bands" query "coldplay" suggestions(
      termSuggestion("my-suggestion-1") on "names" text "clocks by culdpaly" maxEdits 2 mode "Popular" shardSize 2 accuracy 0.6,
      termSuggestion("my-suggestion-2") on "names" text "aqualuck by jethro toll" size 5 mode "Missing" minDocFreq 0.2 prefixLength 3,
      termSuggestion("my-suggestion-3") on "names" text "bountiful day by u22" maxInspections 3 stringDistance "levenstein",
      termSuggestion("my-suggestion-4") on "names" text "whatever some text" maxTermFreq 0.5 minWordLength 5 mode
        SuggestMode.ALWAYS
    )
    req.show should matchJsonResource("/json/search/search_suggestions_multiple.json")
  }

  // for backwards compatibility default suggester is the term suggester
  it should "generate correct json for suggestions" in {
    val req = search("music") types "bands" query termQuery("name", "coldplay") suggestions(
      termSuggestion("suggestion-1") on "name" text "clocks by culdpaly" maxEdits 2,
      termSuggestion("suggestion-2") on "name" text "aqualuck by jethro toll"
    )
    req.show should matchJsonResource("/json/search/search_suggestions.json")
  }

  it should "generate correct json for script fields" in {
    val req =
      search("sesportfolio") types "positions" query matchAllQuery scriptfields(
        scriptField("balance") script "portfolioscript" lang "native" params Map("fieldName" -> "rate_of_return"),
        scriptField("date") script "doc['date'].value" lang "groovy"
      )
    req.show should matchJsonResource("/json/search/search_script_field_poc.json")
  }

  it should "generate correct json for suggestions of multiple suggesters" in {
    val req = search("music") types "bands" query termQuery("name", "coldplay") suggestions(
      termSuggestion("suggestion-term") on "name" text "culdpaly" maxEdits 2,
      phraseSuggestion("suggestion-phrase") on "name" text "aqualuck by jethro toll",
      completionSuggestion("suggestion-completion") on "ac" text "cold"
    )
    req.show should matchJsonResource("/json/search/search_suggestions_multiple_suggesters.json")
  }

  it should "generate correct json for completion suggestions" in {
    val req = search("music") types "bands" query "coldplay" suggestions
      completionSuggestion("my-suggestion-1").on("artist").text("wildcats by ratatat")
    req.show should matchJsonResource("/json/search/search_suggestions_context.json")
  }

  it should "generate correct json for nested query" in {
    val req = search("music") types "bands" query {
      nestedQuery("obj1") query {
        constantScoreQuery {
          termQuery("name", "sammy")
        }
      } scoreMode "Avg" boost 14.5 queryName "namey"
    }
    req.show should matchJsonResource("/json/search/search_query_nested.json")
  }

  it should "generate correct json for nested query with inner highlight" ignore {
    val req = search("music") query {
      nestedQuery("obj1") query {
        constantScoreQuery {
          termQuery("name", "sammy")
        }
      } scoreMode "avg" inner
        innerHits("obj1").size(6).highlighting(highlight("name").fragmentSize(20))
    }
    req.show should matchJsonResource("/json/search/search_query_nested_inner_highlight.json")
  }

  it should "generate correct json for nested query with inner-hits source modulation" ignore {
    val req = search("music") query {
      nestedQuery("obj1") query {
        constantScoreQuery {
          termQuery("name", "sammy")
        }
      } scoreMode "avg" inner innerHits("obj1").fetchSource(new FetchSourceContext(true, Seq("incme").toArray, Seq("excme").toArray))
    }
    req.show should matchJsonResource("/json/search/search_query_nested_inner_hits_source.json")
  }

  it should "generate correct json for a SpanTermQueryDefinition" in {
    val req = search("*") types("users", "tweets") query {
      spanTermQuery("name", "coldplay").boost(123)
    }
    req.show should matchJsonResource("/json/search/search_query_span_term.json")
  }

  it should "generate correct json for a geo distance range filter" in {
    val req = search("*") types("users", "tweets") postFilter {
      geoDistanceQuery("postcode").geohash("hash123").queryName("myfilter")
    }
    req.show should matchJsonResource("/json/search/search_filter_geo_range.json")
  }

  it should "generate correct json for a simple string query" in {
    val req = search("*") types("users", "tweets") query {
      simpleStringQuery("coldplay")
        .analyzer("whitespace")
        .defaultOperator("AND")
        .field("name")
       .flags(SimpleQueryStringFlag.PRECEDENCE, SimpleQueryStringFlag.OR, SimpleQueryStringFlag.SLOP)
    }
    req.show should matchJsonResource("/json/search/search_simple_string_query.json")
  }

  it should "generate correct json for global aggregation" in {
    val req = search("music") aggs {
      globalAggregation("global_agg")
    }
    req.show should matchJsonResource("/json/search/search_aggregations_global.json")
  }

  it should "generate json for ignored field type sort" in {
    val req = search("music") types "bands" sortBy {
      fieldSort("singer.weight") unmappedType "long" order SortOrder.DESC
    }
    req.show should matchJsonResource("/json/search/search_sort_unmapped_field_type.json")
  }

}
