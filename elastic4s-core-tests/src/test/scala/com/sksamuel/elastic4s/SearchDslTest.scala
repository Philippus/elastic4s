package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.Preference.Shards
import com.sksamuel.elastic4s.SuggestMode.{Missing, Popular}
import com.sksamuel.elastic4s.analyzers.{SnowballAnalyzer, StandardAnalyzer, WhitespaceAnalyzer}
import org.elasticsearch.common.geo.GeoDistance
import org.elasticsearch.common.unit.DistanceUnit
import org.elasticsearch.index.query.MatchQueryBuilder.{Operator, ZeroTermsQuery}
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type
import org.elasticsearch.index.query.{MatchQueryBuilder, RegexpFlag, SimpleQueryStringFlag}
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import org.elasticsearch.search.sort.SortOrder
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FlatSpec, OneInstancePerTest}

/** @author Stephen Samuel */
class SearchDslTest extends FlatSpec with MockitoSugar with JsonSugar with OneInstancePerTest {

  "the search dsl" should "accept wilcards for index and types" in {
    val req = search in "*" types "*" limit 10
    req.show should matchJsonResource("/json/search/search_test1.json")
  }

  it should "accept sequences for indexes" in {
    val req = search in("twitter", "other") types "*" limit 5 query "coldplay"
    req.show should matchJsonResource("/json/search/search_test2.json")
  }

  it should "accept sequences for type" in {
    val req = search in "*" types("users", "tweets") from 5 query "sammy"
    req.show should matchJsonResource("/json/search/search_test3.json")
  }

  it should "use limit and and offset when specified" in {
    val req = search in "*" types("users", "tweets") limit 6 from 9 query "coldplay"
    req.show should matchJsonResource("/json/search/search_test4.json")
  }

  it should "use terminateAfter when specified" in {
    val req = search in "*" types("users", "tweets") terminateAfter 5 query "coldplay"
    req.show should matchJsonResource("/json/search/search_test_terminate_after.json")
  }

  it should "use fetchSource when specified" in {
    val req = search in "*" types("users", "tweets") fetchSource false query "coldplay"
    req.show should matchJsonResource("/json/search/search_test_fetch_source.json")
  }

  it should "use preference when specified" in {
    val req = search in "*" types("users", "tweets") query "coldplay" preference Preference.PrimaryFirst
    req.show should matchJsonResource("/json/search/search_preference_primary_first.json")
  }

  it should "use custom preference when specified" in {
    val req = search in "*" types("users", "tweets") query "coldplay" preference Preference.Custom("custom")
    req.show should matchJsonResource("/json/search/search_preference_custom.json")
  }

  it should "generate json for a raw query" in {
    val req = search in "*" types("users", "tweets") limit 5 rawQuery {
      """{ "prefix": { "bands": { "prefix": "coldplay", "boost": 5.0, "rewrite": "yes" } } }"""
    } searchType SearchType.Scan
    req.show should matchJsonResource("/json/search/search_test5.json")
  }

  it should "generate json for a prefix query" in {
    val req = search in "*" types("users", "tweets") limit 5 query {
      prefixQuery("bands" -> "coldplay") boost 5 rewrite "yes"
    } searchType SearchType.Scan
    req.show should matchJsonResource("/json/search/search_test5.json")
  }

  it should "generate json for a term query" in {
    val req = search in "*" types("users", "tweets") limit 5 query {
      termQuery("singer", "chris martin") boost 1.6
    } searchType SearchType.DfsQueryAndFetch
    req.show should matchJsonResource("/json/search/search_term.json")
  }

  it should "generate json for a range query" in {
    val req = search in "*" types("users", "tweets") limit 5 query {
      rangeQuery("coldplay") includeLower true includeUpper true from 4 to 10 boost 1.2
    } searchType SearchType.QueryThenFetch
    req.show should matchJsonResource("/json/search/search_range.json")
  }

  it should "generate json for a wildcard query" in {
    val req = search in "*" types("users", "tweets") limit 5 query {
      wildcardQuery("name", "*coldplay") boost 7.6 rewrite "no"
    }
    req.show should matchJsonResource("/json/search/search_wildcard.json")
  }

  it should "generate json for a string query" in {
    val req = search in "*" types("users", "tweets") limit 5 query {
      stringQuery("coldplay") allowLeadingWildcard true analyzeWildcard true analyzer WhitespaceAnalyzer autoGeneratePhraseQueries true defaultField "name" boost 6.5 enablePositionIncrements true fuzzyMaxExpansions 4 fuzzyPrefixLength 3 lenient true phraseSlop 10 tieBreaker 0.5 operator "OR" rewrite "writer"
    } searchType SearchType.DfsQueryThenFetch
    req.show should matchJsonResource("/json/search/search_string.json")
  }

  it should "generate json for a regex query" in {
    val req = search in "*" types("users", "tweets") limit 5 query {
      regexQuery("drummmer" -> "will*") boost 4 flags RegexpFlag.INTERSECTION rewrite "rewrite-to"
    } searchType SearchType.DfsQueryAndFetch
    req.show should matchJsonResource("/json/search/search_regex.json")
  }

  it should "generate json for a min score" in {
    val req = search in "*" types("users", "tweets") query "coldplay" minScore 0.5
    req.show should matchJsonResource("/json/search/search_minscore.json")
  }

  it should "generate json for an index boost" in {
    val req = search in "*" types("users", "tweets") query "coldplay" indexBoost("index1" -> 1.4, "index2" -> 1.3)
    req.show should matchJsonResource("/json/search/search_indexboost.json")
  }

  it should "generate json for a bpoosting query" in {
    val req = search in "*" types("users", "tweets") limit 5 query {
      boostingQuery positive {
        stringQuery("coldplay")
      } negative {
        stringQuery("jethro tull")
      } negativeBoost 5.6 positiveBoost 7.6
    } searchType SearchType.DfsQueryAndFetch
    req.show should matchJsonResource("/json/search/search_boosting.json")
  }

  it should "generate json for a id query" in {
    val req = search in "*" types("users", "tweets") limit 5 query {
      idsQuery("1", "2", "3") boost 1.6 types("a", "b")
    }
    req.show should matchJsonResource("/json/search/search_id.json")
  }

  it should "generate json for a match query" in {
    val req = search in "*" types("users", "tweets") limit 5 query {
      matchQuery("name", "coldplay")
        .cutoffFrequency(3.4)
        .fuzzyTranspositions(true)
        .maxExpansions(4)
        .operator(MatchQueryBuilder.Operator.AND)
        .zeroTermsQuery(ZeroTermsQuery.ALL)
        .slop(3)
        .setLenient(true)
        .minimumShouldMatch("75%")
        .fuzziness(2f)
        .prefixLength(4)
        .analyzer(SnowballAnalyzer)
    } searchType SearchType.QueryThenFetch
    req.show should matchJsonResource("/json/search/search_match.json")
  }

  it should "generate json for a match query with default as or" in {
    val req = search in "*" types("users", "tweets") limit 5 query {
      matchQuery("drummmer" -> "will") boost 4 operator "OR"
    }
    req.show should matchJsonResource("/json/search/search_match_or.json")
  }

  it should "generate json for a fuzzy query" in {
    val req = search in "*" types("users", "tweets") limit 5 query {
      fuzzyQuery("drummmer", "will") boost 4 maxExpansions 10 prefixLength 10 transpositions true
    } searchType SearchType.QueryThenFetch
    req.show should matchJsonResource("/json/search/search_fuzzy.json")
  }

  it should "generate json for a filtered query" in {
    val req = search in "music" types "bands" query {
      filteredQuery query {
        "coldplay"
      } filter {
        termQuery("location", "uk")
      } boost 1.2
    } preference Preference.Primary
    req.show should matchJsonResource("/json/search/search_query_filteredquery.json")
  }

  it should "generate json for a match all query" in {
    val req = search in "*" types("users", "tweets") limit 5 query {
      matchAllQuery boost 4
    } searchType SearchType.QueryAndFetch
    req.show should matchJsonResource("/json/search/search_match_all.json")
  }

  it should "generate json for a hasChild query" in {
    val req = search in "*" types("users", "tweets") limit 5 query {
      hasChildQuery("sometype") query {
        "coldplay"
      } boost 1.2 scoreMode "type"
    } searchType SearchType.QueryThenFetch
    req.show should matchJsonResource("/json/search/search_haschild_query.json")
  }

  it should "generate json for a hasParent query" in {
    val req = search in "*" types("users", "tweets") limit 5 query {
      hasParentQuery("sometype") query {
        "coldplay"
      } boost 1.2 scoreType "type"
    } searchType SearchType.QueryThenFetch preference new Preference.Custom("custompref")
    req.show should matchJsonResource("/json/search/search_hasparent_query.json")
  }

  it should "generate json for a boolean compound query" in {
    val req = search in "*" types("bands", "artists") limit 5 query {
      bool {
        must(
          regexQuery("drummmer" -> "will*") boost 5 flags RegexpFlag.ANYSTRING,
          termQuery("singer" -> "chris")
        ) should termQuery("bassist" -> "berryman") not termQuery("singer" -> "anderson")
      }
    } preference Preference.Local
    req.show should matchJsonResource("/json/search/search_boolean.json")
  }

  it should "generate json for a boolean query" in {
    val req = search in "space" -> "planets" limit 5 query {
      bool {
        must(
          regexQuery("drummmer" -> "will*") boost 5,
          termQuery("singer" -> "chris")
        ) should {
          termQuery("bassist" -> "berryman")
        } not {
          termQuery("singer" -> "anderson")
        }
      } boost 2.4 minimumShouldMatch 2 adjustPureNegative false disableCoord true queryName "booly"
    } preference Preference.Local
    req.show should matchJsonResource("/json/search/search_boolean2.json")
  }

  it should "generate json for a boolean query with filter" in {
    val req = search in "*" limit 5 query {
      bool {
        must(termQuery("title", "Search")) filter(termQuery("status", "published"))
      }
    }
    req.show should matchJsonResource("/json/search/search_boolean_with_filter.json")
  }

  it should "generate json for a match phrase query" in {
    val req = search("*").types("bands", "artists").limit(5).query {
      matchPhraseQuery("name", "coldplay")
        .cutoffFrequency(3.4)
        .fuzzyTranspositions(true)
        .maxExpansions(4)
        .operator(MatchQueryBuilder.Operator.AND)
        .zeroTermsQuery(ZeroTermsQuery.ALL)
        .slop(3)
        .operator("AND")
        .minimumShouldMatch("75%")
        .fuzziness(2f)
        .boost(15)
        .setLenient(true)
        .prefixLength(4)
        .analyzer(SnowballAnalyzer)
    } preference Preference.OnlyNode("a")
    req.show should matchJsonResource("/json/search/search_match_phrase.json")
  }

  it should "generate json for a match phrase prefix query" in {
    val req = search("*").types("bands", "artists").limit(5).query {
      matchPhrasePrefixQuery("name", "coldplay")
        .cutoffFrequency(3.4)
        .fuzzyTranspositions(true)
        .maxExpansions(4)
        .operator(MatchQueryBuilder.Operator.AND)
        .zeroTermsQuery(ZeroTermsQuery.ALL)
        .slop(3)
        .operator("AND")
        .minimumShouldMatch("75%")
        .setLenient(true)
        .fuzziness(2f)
        .prefixLength(4)
        .analyzer(SnowballAnalyzer)
    } preference Preference.OnlyNode("a")
    req.show should matchJsonResource("/json/search/search_match_phrase_prefix.json")
  }

  it should "generate json for term post filter" in {
    val req = search in "music" types "bands" postFilter {
      termQuery("singer", "chris martin") queryName "namey"
    } preference Preference.Shards("a")
    req.show should matchJsonResource("/json/search/search_term_filter.json")
  }

  it should "generate json for terms lookup filter" in {
    val req = search in "music" types "bands" postFilter {
      termsQuery("user", "val", "vallllll").queryName("namey")
    }
    req.show should matchJsonResource("/json/search/search_terms_lookup_filter.json")
  }

  it should "generate json for regex query" in {
    val req = search in "music" types "bands" postFilter {
      regexQuery("singer", "chris martin")
    } preference Preference.PreferNode("a")
    req.show should matchJsonResource("/json/search/search_regex_query.json")
  }

  it should "generate json for prefix query" in {
    val req = search in "music" types "bands" postFilter {
      prefixQuery("singer", "chris martin")
    } preference Preference.Primary
    req.show should matchJsonResource("/json/search/search_prefix_query.json")
  }

  it should "generate json for has child filter with filter" in {
    val req = search in "music" types "bands" postFilter {
      hasChildQuery("singer").query {
        termQuery("name", "chris")
      }.minChildren(2).maxChildren(4).shortCircuitCutoff(3).boost(2.3).queryName("namey")
    } preference Preference.Primary
    req.show should matchJsonResource("/json/search/search_haschild_filter.json")
  }

  it should "generate json for has parent filter with filter" in {
    val req = search in "music" types "bands" postFilter {
      hasParentQuery("singer").query {
        termQuery("name", "chris")
      }.boost(2.3).scoreType("scoreType").queryName("spidername")
    } preference Preference.Primary
    req.show should matchJsonResource("/json/search/search_hasparent_filter.json")
  }

  it should "generate json for nested filter with filter" in {
    val req = search in "music" types "bands" postFilter {
      nestedQuery("singer").query {
        termQuery("name", "chris")
      } queryName "namey"
    } preference Preference.Primary
    req.show should matchJsonResource("/json/search/search_nested_filter.json")
  }

  it should "generate json for has child filter with query" in {
    val req = search in "music" types "bands" postFilter {
      hasChildQuery("singer") query {
        termQuery("name", "chris")
      } queryName "namey"
    } preference Preference.Primary
    req.show should matchJsonResource("/json/search/search_haschild_filter_query.json")
  }

  it should "generate json for has parent filter with query" in {
    val req = search in "music" types "bands" postFilter {
      hasParentQuery("singer") query {
        termQuery("name", "chris")
      } queryName "namey"
    } preference Preference.Primary
    req.show should matchJsonResource("/json/search/search_hasparent_filter_query.json")
  }

  it should "generate json for nested filter with query" in {
    val req = search in "music" types "bands" postFilter {
      nestedQuery("singer") query {
        termQuery("name", "chris")
      } queryName "namey"
    } preference Preference.Primary
    req.show should matchJsonResource("/json/search/search_nested_filter_query.json")
  }

  it should "generate json for id filter" in {
    val req = search in "music" types "bands" postFilter {
      idsQuery("a", "b", "c").types("x", "y", "z")
    } preference Preference.PrimaryFirst
    req.show should matchJsonResource("/json/search/search_id_filter.json")
  }

  it should "generate json for type filter" in {
    val req = search in "music" types "bands" postFilter {
      typeQuery("sometype")
    } preference new Shards("5", "7")
    req.show should matchJsonResource("/json/search/search_type_filter.json")
  }

  it should "generate json for type range filter" in {
    val req = search in "music" types "bands" postFilter {
      rangeQuery("released") includeLower true includeUpper true gte "2010-01-01" lte "2012-12-12"
    } preference new Shards("5", "7")
    req.show should matchJsonResource("/json/search/search_range_filter.json")
  }

  it should "generate json for missing filter" in {
    val req = search in "music" types "bands" postFilter {
      missingQuery("producer") existence true queryName "named" includeNull true
    } preference Preference.PrimaryFirst
    req.show should matchJsonResource("/json/search/search_missing_filter.json")
  }

  it should "generate json for field sort" in {
    val req = search in "music" types "bands" sort {
      fieldSort("singer") missing "no-singer" order SortOrder.DESC mode MultiMode.Avg nestedPath "nest"
    }
    req.show should matchJsonResource("/json/search/search_sort_field.json")
  }

  it should "generate json for nested field sort" in {
    val req = search in "music" types "bands" sort {
      fieldSort("singer.weight") ignoreUnmapped true order SortOrder.DESC mode MultiMode.Sum nestedFilter {
        termQuery("singer.name", "coldplay")
      }
    }
    req.show should matchJsonResource("/json/search/search_sort_nested_field.json")
  }

  it should "generate correct json for score sort" in {
    val req = search in "music" types "bands" sort {
      scoreSort().missing("213").order(SortOrder.ASC)
    }
    req.show should matchJsonResource("/json/search/search_sort_score.json")
  }

  it should "generate correct json for script sort" in {
    val req = search in "music" types "bands" sort {
      scriptSort("document.score") typed "number" lang "java" order SortOrder.DESC nestedPath "a.b.c" sortMode "min"
    } preference new Preference.Custom("custom-node")
    req.show should matchJsonResource("/json/search/search_sort_script.json")
  }

  it should "generate correct json for script sort with params" in {
    val req = search in "music" types "bands" sort {
      scriptSort("doc.score") typed "number" order SortOrder.DESC params Map("param1" -> "value1", "param2" -> "value2")
    } preference new Preference.Custom("custom-node")
    req.show should matchJsonResource("/json/search/search_sort_script_params.json")
  }

  it should "generate correct json for geo sort" in {
    val req = search in "music" types "bands" sort {
      geo sort "location" geohash "ABCDEFG" missing "567.8889" order SortOrder.DESC mode
        MultiMode.Sum point(56.6, 78.8) nested "nested-path" mode MultiMode.Max geoDistance GeoDistance.ARC
    }
    req.show should matchJsonResource("/json/search/search_sort_geo.json")
  }

  it should "generate correct json for multiple sorts" in {
    val req = search in "music" types "bands" sort(
      scriptSort("document.score") as "java" order SortOrder.ASC,
      scoreSort().order(SortOrder.DESC),
      fieldSort("dancer") order SortOrder.DESC
      )
    req.show should matchJsonResource("/json/search/search_sort_multiple.json")
  }

  it should "generate json for field sort with score tracking enabled" in {
    val req = search in "music" types "bands" trackScores true sort {
      fieldSort("singer") order SortOrder.DESC
    }
    req.show should matchJsonResource("/json/search/search_sort_track_scores.json")
  }

  it should "generate correct json for geo bounding box filter" in {
    val req = search in "music" types "bands" postFilter {
      geoBoxQuery("box") left 40.6 top 56.5 right 45.5 bottom 12.55
    }
    req.show should matchJsonResource("/json/search/search_filter_geo_boundingbox.json")
  }

  it should "generate correct json for geo bounding box filter2" in {
    val req = search in "music" types "bands" postFilter {
      geoBoxQuery("box") left 40.6 top 56.5 bottom 12.55 right 45.5
    }
    req.show should matchJsonResource("/json/search/search_filter_geo_boundingbox.json")
  }

  it should "generate correct json for geo bounding box filter3" in {
    val req = search in "music" types "bands" postFilter {
      geoBoxQuery("box") top 56.5 left 40.6 right 45.5 bottom 12.55
    }
    req.show should matchJsonResource("/json/search/search_filter_geo_boundingbox.json")
  }

  it should "generate correct json for dismax query" in {
    val req = search in "music" types "bands" query {
      dismax boost 4.5 query "coldplay" query "london" tieBreaker 1.2
    }
    req.show should matchJsonResource("/json/search/search_query_dismax.json")
  }

  it should "generate correct json for common terms query" in {
    val req = search in "music" types "bands" query {
      commonQuery("name") text "some text here" analyzer WhitespaceAnalyzer boost 12.3 cutoffFrequency 14.4 highFreqOperator "AND" lowFreqOperator "OR" lowFreqMinimumShouldMatch 3 highFreqMinimumShouldMatch 2
    }
    req.show should matchJsonResource("/json/search/search_query_commonterms.json")
  }

  it should "generate correct json for constant score query" in {
    val req = search in "music" types "bands" query {
      constantScoreQuery {
        termQuery("name", "sammy")
      } boost 14.5
    }
    req.show should matchJsonResource("/json/search/search_query_constantscore.json")
  }

  it should "generate correct json for terms query" in {
    val req = search in "music" types "bands" query {
      termsQuery("name", "chris", "will", "johnny", "guy") boost 1.2 minimumShouldMatch 4 disableCoord true
    }
    req.show should matchJsonResource("/json/search/search_query_terms.json")
  }

  it should "generate correct json for multi match query" in {
    val req = search in "music" types "bands" query {
      multiMatchQuery("this is my query") fields("name", "location", "genre") analyzer WhitespaceAnalyzer boost 3.4 cutoffFrequency 1.7 fuzziness "something" prefixLength 4 minimumShouldMatch 2 tieBreaker 4.5 zeroTermsQuery
        MatchQueryBuilder
          .ZeroTermsQuery
          .ALL fuzzyRewrite "some-rewrite" maxExpansions 4 lenient true prefixLength 4 operator Operator
        .AND matchType Type.CROSS_FIELDS
    }
    req.show should matchJsonResource("/json/search/search_query_multi_match.json")
  }

  it should "generate correct json for multi match query with minimum should match text clause" in {
    val req = search in "music" types "bands" query {
      multiMatchQuery("this is my query") fields("name", "location", "genre") minimumShouldMatch "2<-1 5<80%" matchType "best_fields"
    }
    req.show should matchJsonResource("/json/search/search_query_multi_match_minimum_should_match.json")
  }

  it should "generate correct json for geo distance filter" in {
    val req = search in "music" types "bands" postFilter {
      bool(
        should(
          geoDistanceQuery("distance") point(10.5d, 35.0d) geoDistance GeoDistance
            .FACTOR geohash "geo1234" distance "120mi"
        ) not (
          geoDistanceQuery("distance") lat 45.4d lon 76.6d distance(45, DistanceUnit.YARD)
          )
      )
    }
    req.show should matchJsonResource("/json/search/search_filter_geo_distance.json")
  }

  it should "generate correct json for a rescore query" in {
    val req = search in "music" types "bands" rescore {
      rescore("coldplay").originalQueryWeight(1.4).rescoreQueryWeight(5.4).scoreMode("modey").window(14)
    }
    req.show should matchJsonResource("/json/search/search_rescore.json")
  }

  it should "generate correct json for function score query" in {
    val req = search in "music" types "bands" query {
      functionScoreQuery("coldplay").boost(1.4).maxBoost(1.9).scoreMode("multiply").boostMode("max").scorers(
        randomScore(1234).weight(1.2),
        scriptScore("some script here").weight(0.5),
        gaussianScore("field1", "1m", "2m").filter(termQuery("band", "coldplay")),
        fieldFactorScore("field2").factor(1.2).filter(termQuery("band", "taylor swift"))
      )
    }
    req.show should matchJsonResource("/json/search/search_function_score.json")
  }

  it should "generate correct json for geo polygon filter" in {
    val req = search in "music" types "bands" postFilter {
      geoPolygonQuery("distance") point(10, 10) point(20, 20) point(30, 30) point "123456"
    }
    req.show should matchJsonResource("/json/search/search_filter_geo_polygon.json")
  }

  it should "generate correct json for a boolean filter" in {
    val req = search in "music" types "bands" postFilter {
      bool {
        must {
          termQuery("name", "sammy")
        } should {
          termQuery("location", "oxford")
        } not {
          termQuery("type", "rap")
        }
      }
    }
    req.show should matchJsonResource("/json/search/search_filter_bool.json")
  }

  it should "generate correct json for datehistogram aggregation" in {
    val req = search in "music" types "bands" aggs {
      aggregation datehistogram "years" field "date" interval DateHistogramInterval.YEAR minDocCount 0
    }
    req.show should matchJsonResource("/json/search/search_aggregations_datehistogram.json")
  }

  it should "generate correct json for range aggregation" in {
    val req = search in "music" types "bands" aggs {
      aggregation range "range_agg" field "score" range(10.0, 15.0)
    }
    req.show should matchJsonResource("/json/search/search_aggregations_range.json")
  }

  it should "generate correct json for date range aggregation" in {
    val req = search in "music" types "bands" aggs {
      aggregation daterange "daterange_agg" field "date" range("now-1Y", "now")
    }
    req.show should matchJsonResource("/json/search/search_aggregations_daterange.json")
  }

  it should "generate correct json for date range aggregation with unbounded from" in {
    val req = search in "music" types "bands" aggs {
      aggregation daterange "daterange_agg" field "date" unboundedFrom("key", "now-1Y")
    }
    req.show should matchJsonResource("/json/search/search_aggregations_daterange_from.json")
  }

  it should "generate correct json for date range aggregation with unbounded to" in {
    val req = search in "music" types "bands" aggs {
      aggregation daterange "daterange_agg" field "date" unboundedTo("key", "now")
    }
    req.show should matchJsonResource("/json/search/search_aggregations_daterange_to.json")
  }

  it should "generate correct json for histogram aggregation" in {
    val req = search in "music" types "bands" aggs {
      aggregation histogram "score_histogram" field "score" interval 2
    }
    req.show should matchJsonResource("/json/search/search_aggregations_histogram.json")
  }

  it should "generate correct json for filter aggregation" in {
    val req = search in "music" types "bands" aggs {
      aggregation filter "my_filter_agg" filter {
        bool {
          must {
            termQuery("name", "sammy")
          } should {
            termQuery("location", "oxford")
          } not {
            termQuery("type", "rap")
          }
        }
      }
    }
    req.show should matchJsonResource("/json/search/search_aggregations_filter.json")
  }

  it should "generate correct json for terms aggregation" in {
    val req = search in "music" types "bands" aggs {
      aggregation terms "my_terms_agg" field "keyword" size 10 order Terms.Order.count(false)
    }
    req.show should matchJsonResource("/json/search/search_aggregations_terms.json")
  }

  it should "generate correct json for top hits aggregation" in {
    val req = search in "music" types "bands" aggs {
      aggregation terms "top-tags" field "tags" size 3 order Terms.Order.count(false) aggregations (
        aggregation topHits "top_tag_hits" size 1 sort {
          fieldSort("last_activity_date") order SortOrder.DESC
        } fetchSource(Array("title"), Array.empty)
        )
    }
    req.show should matchJsonResource("/json/search/search_aggregations_top_hits.json")
  }

  it should "generate correct json for geobounds aggregation" in {
    val req = search in "music" types "bands" aggs {
      aggregation geobounds "geo_agg" field "geo_point" wrapLongitude true
    }
    req.show should matchJsonResource("/json/search/search_aggregations_geobounds.json")
  }

  it should "generate correct json for geodistance aggregation" in {
    val req = search in "music" types "bands" aggs {
      aggregation geodistance "geo_agg" field "geo_point" point(45.0, 27.0) geoDistance GeoDistance.ARC range(1.0, 1.0)
    }
    req.show should matchJsonResource("/json/search/search_aggregations_geodistance.json")
  }

  it should "generate correct json for sub aggregation" in {
    val req = search in "music" types "bands" aggs {
      aggregation datehistogram "days" field "date" interval DateHistogramInterval.DAY aggs(
        aggregation terms "keywords" field "keyword" size 5,
        aggregation terms "countries" field "country")
    }
    req.show should matchJsonResource("/json/search/search_aggregations_datehistogram_subs.json")
  }

  it should "generate correct json for min aggregation" in {
    val req = search in "school" types "student" aggs {
      aggregation min "grades_min" field "grade" script {
        script("doc['grade'].value").lang("lua").param("apple", "bad")
      }
    }
    req.show should matchJsonResource("/json/search/search_aggregations_min.json")
  }

  it should "generate correct json for max aggregation" in {
    val req = search in "school" types "student" aggs {
      aggregation max "grades_max" field "grade" script {
        script("doc['grade'].value").lang("lua")
      }
    }
    req.show should matchJsonResource("/json/search/search_aggregations_max.json")
  }

  it should "generate correct json for sum aggregation" in {
    val req = search in "school" types "student" aggs {
      aggregation sum "grades_sum" field "grade" script {
        script("doc['grade'].value").lang("lua") params Map("classsize" -> "30", "room" -> "101A")
      }
    }
    req.show should matchJsonResource("/json/search/search_aggregations_sum.json")
  }

  it should "generate correct json for avg aggregation" in {
    val req = search in "school" types "student" aggs {
      aggregation avg "grades_avg" field "grade" script {
        script("doc['grade'].value").lang("lua")
      }
    }
    req.show should matchJsonResource("/json/search/search_aggregations_avg.json")
  }

  it should "generate correct json for stats aggregation" in {
    val req = search in "school" types "student" aggs {
      aggregation stats "grades_stats" field "grade" script {
        script("doc['grade'].value").lang("lua")
      }
    }
    req.show should matchJsonResource("/json/search/search_aggregations_stats.json")
  }

  it should "generate correct json for extendedstats aggregation" in {
    val req = search in "school" types "student" aggs {
      aggregation extendedstats "grades_extendedstats" field "grade" script {
        script("doc['grade'].value").lang("lua")
      }
    }
    req.show should matchJsonResource("/json/search/search_aggregations_extendedstats.json")
  }

  it should "generate correct json for percentiles aggregation" in {
    val req = search in "school" types "student" aggs {
      aggregation percentiles "grades_percentiles" field "grade" percents(95, 99, 99.9) compression 200
    }
    req.show should matchJsonResource("/json/search/search_aggregations_percentiles.json")
  }

  it should "generate correct json for percentileranks aggregation" in {
    val req = search in "school" types "student" aggs {
      aggregation percentileranks "grades_percentileranks" field "grade" percents(95, 99, 99.9) compression 200
    }
    req.show should matchJsonResource("/json/search/search_aggregations_percentileranks.json")
  }

  it should "generate correct json for value count aggregation" in {
    val req = search in "school" types "student" aggs {
      aggregation count "grades_count" field "grade" script {
        script("doc['grade'].value").lang("lua")
      }
    }
    req.show should matchJsonResource("/json/search/search_aggregations_count.json")
  }

  it should "generate correct json for cardinality aggregation" in {
    val req = search in "school" types "student" aggs {
      aggregation cardinality "grades_cardinality" field "grade" rehash true precisionThreshold 40000
    }
    req.show should matchJsonResource("/json/search/search_aggregations_cardinality.json")
  }

  it should "generate correct json for nested aggregation" in {
    val req = search in "music" types "bands" aggs {
      aggregation nested "nested_agg" path "nested_obj" aggs {
        aggregation terms "my_nested_terms_agg" field "keyword"
      }
    }
    req.show should matchJsonResource("/json/search/search_aggregations_nested.json")
  }

  it should "generate correct json for highlighting" in {
    val req = search in "music" types "bands" highlighting(
      options tagSchema TagSchema.Styled boundaryChars "\\b" boundaryMaxScan 4 order HighlightOrder
        .Score preTags "<b>" postTags "</b>" encoder HighlightEncoder.Html,
      "name" fragmentSize 100 numberOfFragments 3 fragmentOffset 4,
      "type" numberOfFragments 100 fragmentSize 44 highlighterType "some-type"
      )
    req.show should matchJsonResource("/json/search/search_highlighting.json")
  }

  it should "generate correct json for multiple suggestions" in {
    val req = search in "music" types "bands" query "coldplay" suggestions(
      term suggestion "my-suggestion-1" text "clocks by culdpaly" field "names" maxEdits 4 mode Popular shardSize 2 accuracy 0.6,
      term suggestion "my-suggestion-2" text "aqualuck by jethro toll" field "names" size 5 mode Missing minDocFreq 0.2 prefixLength 3,
      term suggestion "my-suggestion-3" text "bountiful day by u22" field "names" analyzer StandardAnalyzer maxInspections 3 stringDistance "levenstein",
      term suggestion "my-suggestion-4" text "whatever some text" field "names" maxTermFreq 0.5 minWordLength 5 mode SuggestMode
        .Always
      )
    req.show should matchJsonResource("/json/search/search_suggestions_multiple.json")
  }

  // for backwards compatibility default suggester is the term suggester
  it should "generate correct json for suggestions" in {
    val req = search in "music" types "bands" query termQuery("name", "coldplay") suggestions(
      term suggestion "suggestion-1" text "clocks by culdpaly" field "name" maxEdits 2,
      term suggestion "suggestion-2" text "aqualuck by jethro toll" field "name"
      )
    req.show should matchJsonResource("/json/search/search_suggestions.json")
  }

  it should "generate correct json for script fields" in {
    val req =
      search in "sesportfolio" types "positions" query matchAllQuery scriptfields(
        scriptField("balance") script "portfolioscript" lang "native" params Map("fieldName" -> "rate_of_return"),
        scriptField("date") script "doc['date'].value" lang "groovy"
        )
    req.show should matchJsonResource("/json/search/search_script_field_poc.json")
  }

  it should "generate correct json for suggestions of multiple suggesters" in {
    val req = search in "music" types "bands" query termQuery("name", "coldplay") suggestions(
      term suggestion "suggestion-term" text "culdpaly" field "name" maxEdits 2,
      phrase suggestion "suggestion-phrase" text "aqualuck by jethro toll" field "name",
      completion suggestion "suggestion-completion" text "cold" field "ac"
      )
    req.show should matchJsonResource("/json/search/search_suggestions_multiple_suggesters.json")
  }

  it should "generate correct json for context queries" in {
    val req = search in "music" types "bands" suggestions (
      completion suggestion "my-suggestion-1" text "wildcats by ratatat" field "colors" context("genre", "electronic")
      )
    req.show should matchJsonResource("/json/search/search_suggestions_context.json")
  }

  it should "generate correct json for context queries with an Iterable argument" in {
    val req = search in "music" types "bands" suggestions (
      completion suggestion "my-suggestion-1" text "wildcats by ratatat" field "colors" context("genre", Seq(
        "electronic",
        "alternative rock"))
      )
    req.show should matchJsonResource("/json/search/search_suggestions_context_multiple.json")
  }

  it should "generate correct json for nested query" in {
    val req = search in "music" types "bands" query {
      nestedQuery("obj1") query {
        constantScoreQuery {
          termQuery("name", "sammy")
        }
      } scoreMode "avg" boost 14.5 queryName "namey"
    }
    req.show should matchJsonResource("/json/search/search_query_nested.json")
  }

  it should "generate correct json for a SpanTermQueryDefinition" in {
    val req = search in "*" types("users", "tweets") query {
      spanTermQuery("name", "coldplay").boost(123)
    }
    req.show should matchJsonResource("/json/search/search_query_span_term.json")
  }

  it should "generate correct json for a geo distance range filter" in {
    val req = search in "*" types("users", "tweets") postFilter {
      geoDistanceQuery("postcode").geohash("hash123").queryName("myfilter")
    }
    req.show should matchJsonResource("/json/search/search_filter_geo_range.json")
  }

  it should "generate correct json for a simple string query" in {
    val req = search in "*" types("users", "tweets") query {
      simpleStringQuery("coldplay")
        .analyzer("whitespace")
        .defaultOperator("AND")
        .field("name")
        .flags(SimpleQueryStringFlag.AND, SimpleQueryStringFlag.OR, SimpleQueryStringFlag.NOT)
    }
    req.show should matchJsonResource("/json/search/search_simple_string_query.json")
  }


  it should "generate correct json for default filtered query" in {
    val req = filteredQuery filter termQuery("singer", "lemmy")
    req.builder.toString should matchJsonResource("/json/search/search_default_query.json")
  }

  it should "generate correct json for global aggregation" in {
    val req = search in "music" types "bands" aggs {
      aggregation global "global_agg"
    }
    req.show should matchJsonResource("/json/search/search_aggregations_global.json")
  }

  it should "generate json for ignored field type sort" in {
    val req = search in "music" types "bands" sort {
      fieldSort("singer.weight") unmappedType "long" order SortOrder.DESC
    }
    req.show should matchJsonResource("/json/search/search_sort_unmapped_field_type.json")
  }

}
