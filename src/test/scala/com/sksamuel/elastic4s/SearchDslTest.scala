package com.sksamuel.elastic4s

import org.scalatest.{ FlatSpec, OneInstancePerTest }
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.search.sort.SortOrder
import com.sksamuel.elastic4s.SuggestMode.{ Missing, Popular }
import org.elasticsearch.index.query.{ MatchQueryBuilder, RegexpFlag, SimpleQueryStringFlag }
import org.elasticsearch.search.facet.histogram.HistogramFacet.ComparatorType
import org.elasticsearch.search.facet.terms.TermsFacet
import org.elasticsearch.common.geo.GeoDistance
import org.elasticsearch.common.unit.DistanceUnit
import com.sksamuel.elastic4s.Preference.Shards
import org.elasticsearch.index.query.MatchQueryBuilder.ZeroTermsQuery
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram
import org.elasticsearch.search.aggregations.bucket.terms.Terms

/** @author Stephen Samuel */
class SearchDslTest extends FlatSpec with MockitoSugar with JsonSugar with OneInstancePerTest {
  "the search dsl" should "accept wilcards for index and types" in {
    val req = search in "*" types "*" limit 10
    req._builder.toString should matchJsonResource("/json/search/search_test1.json")
  }

  it should "accept sequences for indexes" in {
    val req = search in ("twitter", "other") types "*" limit 5 query "coldplay"
    req._builder.toString should matchJsonResource("/json/search/search_test2.json")
  }

  it should "accept sequences for type" in {
    val req = search in "*" types ("users", "tweets") from 5 query "sammy"
    req._builder.toString should matchJsonResource("/json/search/search_test3.json")
  }

  it should "use limit and and offset when specified" in {
    val req = search in "*" types ("users", "tweets") limit 6 from 9 query "coldplay"
    req._builder.toString should matchJsonResource("/json/search/search_test4.json")
  }

  it should "use fetchSource when specified" in {
    val req = search in "*" types ("users", "tweets") fetchSource false query "coldplay"
    req._builder.toString should matchJsonResource("/json/search/search_test_fetch_source.json")
  }

  it should "use preference when specified" in {
    val req = search in "*" types ("users", "tweets") query "coldplay" preference Preference.PrimaryFirst
    req._builder.toString should matchJsonResource("/json/search/search_preference_primary_first.json")
  }

  it should "use custom preference when specified" in {
    val req = search in "*" types ("users", "tweets") query "coldplay" preference Preference.Custom("custom")
    req._builder.toString should matchJsonResource("/json/search/search_preference_custom.json")
  }

  it should "generate json for a raw query" in {
    val req = search in "*" types ("users", "tweets") limit 5 rawQuery {
      """{ "prefix": { "bands": { "prefix": "coldplay", "boost": 5.0, "rewrite": "yes" } } }"""
    } searchType SearchType.Scan
    req._builder.toString should matchJsonResource("/json/search/search_test5.json")
  }

  it should "generate json for a prefix query" in {
    val req = search in "*" types ("users", "tweets") limit 5 query {
      prefix("bands" -> "coldplay") boost 5 rewrite "yes"
    } searchType SearchType.Scan
    req._builder.toString should matchJsonResource("/json/search/search_test5.json")
  }

  it should "generate json for a term query" in {
    val req = search in "*" types ("users", "tweets") limit 5 query {
      term("singer", "chris martin") boost 1.6
    } searchType SearchType.DfsQueryAndFetch
    req._builder.toString should matchJsonResource("/json/search/search_term.json")
  }

  it should "generate json for a range query" in {
    val req = search in "*" types ("users", "tweets") limit 5 query {
      range("coldplay") includeLower true includeUpper true from 4 to 10 boost 1.2
    } searchType SearchType.QueryThenFetch
    req._builder.toString should matchJsonResource("/json/search/search_range.json")
  }

  it should "generate json for a wildcard query" in {
    val req = search in "*" types ("users", "tweets") limit 5 query {
      wildcard("name", "*coldplay") boost 7.6 rewrite "no"
    }
    req._builder.toString should matchJsonResource("/json/search/search_wildcard.json")
  }

  it should "generate json for a string query" in {
    val req = search in "*" types ("users", "tweets") limit 5 query {
      query("coldplay") allowLeadingWildcard true analyzeWildcard true analyzer WhitespaceAnalyzer autoGeneratePhraseQueries true defaultField "name" boost 6.5 enablePositionIncrements true fuzzyMaxExpansions 4 fuzzyPrefixLength 3 lenient true phraseSlop 10 tieBreaker 0.5 operator "OR" rewrite "writer"
    } searchType SearchType.DfsQueryThenFetch
    req._builder.toString should matchJsonResource("/json/search/search_string.json")
  }

  it should "generate json for a regex query" in {
    val req = search in "*" types ("users", "tweets") limit 5 query {
      ElasticDsl.regex("drummmer" -> "will*") boost 4 flags RegexpFlag.INTERSECTION rewrite "rewrite-to"
    } searchType SearchType.DfsQueryAndFetch
    req._builder.toString should matchJsonResource("/json/search/search_regex.json")
  }

  it should "generate json for a min score" in {
    val req = search in "*" types ("users", "tweets") query "coldplay" minScore 0.5
    req._builder.toString should matchJsonResource("/json/search/search_minscore.json")
  }

  it should "generate json for an index boost" in {
    val req = search in "*" types ("users", "tweets") query "coldplay" indexBoost ("index1" -> 1.4, "index2" -> 1.3)
    req._builder.toString should matchJsonResource("/json/search/search_indexboost.json")
  }

  it should "generate json for a bpoosting query" in {
    val req = search in "*" types ("users", "tweets") limit 5 query {
      boosting positive {
        query("coldplay")
      } negative {
        query("jethro tull")
      } negativeBoost 5.6 positiveBoost 7.6
    } searchType SearchType.DfsQueryAndFetch
    req._builder.toString should matchJsonResource("/json/search/search_boosting.json")
  }

  it should "generate json for a id query" in {
    val req = search in "*" types ("users", "tweets") limit 5 query {
      ids("1", "2", "3") boost 1.6 types ("a", "b")
    }
    req._builder.toString should matchJsonResource("/json/search/search_id.json")
  }

  it should "generate json for a match query" in {
    val req = search in "*" types ("users", "tweets") limit 5 query {
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
    } searchType SearchType.Count
    req._builder.toString should matchJsonResource("/json/search/search_match.json")
  }

  it should "generate json for a match query with default as or" in {
    val req = search in "*" types ("users", "tweets") limit 5 query {
      matches("drummmer" -> "will") boost 4 operator "OR"
    }
    req._builder.toString should matchJsonResource("/json/search/search_match_or.json")
  }

  it should "generate json for a fuzzy query" in {
    val req = search in "*" types ("users", "tweets") limit 5 query {
      fuzzy("drummmer", "will") boost 4 maxExpansions 10 prefixLength 10 transpositions true
    } searchType SearchType.Count
    req._builder.toString should matchJsonResource("/json/search/search_fuzzy.json")
  }

  it should "generate json for a filtered query" in {
    val req = search in "music" types "bands" query {
      filteredQuery query {
        "coldplay"
      } filter {
        termFilter("location", "uk")
      } boost 1.2
    } preference Preference.Primary
    req._builder.toString should matchJsonResource("/json/search/search_query_filteredquery.json")
  }

  it should "generate json for a match all query" in {
    val req = search in "*" types ("users", "tweets") limit 5 query {
      matchall boost 4 normsField "norm-field"
    } searchType SearchType.QueryAndFetch
    req._builder.toString should matchJsonResource("/json/search/search_match_all.json")
  }

  it should "generate json for a hasChild query" in {
    val req = search in "*" types ("users", "tweets") limit 5 query {
      hasChildQuery("sometype") query {
        "coldplay"
      } boost 1.2 scoreType "type"
    } searchType SearchType.QueryThenFetch
    req._builder.toString should matchJsonResource("/json/search/search_haschild_query.json")
  }

  it should "generate json for a topChildren query" in {
    val req = search in "*" types ("users", "tweets") limit 5 query {
      topChildren("sometype") query {
        "coldplay"
      } boost 1.2 factor 3 incrementalFactor 2 score "max"
    } searchType SearchType.QueryThenFetch
    req._builder.toString should matchJsonResource("/json/search/search_topchildren_query.json")
  }

  it should "generate json for a hasParent query" in {
    val req = search in "*" types ("users", "tweets") limit 5 query {
      hasParentQuery("sometype") query {
        "coldplay"
      } boost 1.2 scoreType "type"
    } searchType SearchType.Count preference new Preference.Custom("custompref")
    req._builder.toString should matchJsonResource("/json/search/search_hasparent_query.json")
  }

  it should "generate json for a boolean compound query" in {
    val req = search in "*" types ("bands", "artists") limit 5 query {
      bool {
        must(
          ElasticDsl.regex("drummmer" -> "will*") boost 5,
          term("singer" -> "chris")
        ) should term("bassist" -> "berryman") not term("singer" -> "anderson")
      }
    } preference Preference.Local
    req._builder.toString should matchJsonResource("/json/search/search_boolean.json")
  }

  it should "generate json for a boolean query" in {
    val req = search in "space" -> "planets" limit 5 query {
      bool {
        must(
          ElasticDsl.regex("drummmer" -> "will*") boost 5,
          term("singer" -> "chris")
        ) should {
            term("bassist" -> "berryman")
          } not {
            term("singer" -> "anderson")
          }
      } boost 2.4 minimumShouldMatch 2 adjustPureNegative false disableCoord true queryName "booly"
    } preference Preference.Local
    req._builder.toString should matchJsonResource("/json/search/search_boolean2.json")
  }

  it should "generate json for a match phrase query" in {
    val req = search("*").types("bands", "artists").limit(5).query {
      matchPhrase("name", "coldplay")
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
    req._builder.toString should matchJsonResource("/json/search/search_match_phrase.json")
  }

  it should "generate json for a match phrase prefix query" in {
    val req = search("*").types("bands", "artists").limit(5).query {
      matchPhrasePrefix("name", "coldplay")
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
    req._builder.toString should matchJsonResource("/json/search/search_match_phrase_prefix.json")
  }

  it should "generate json for term filter" in {
    val req = search in "music" types "bands" filter {
      termFilter("singer", "chris martin") cacheKey "band-singers" name "my-filter"
    } preference Preference.Shards("a")
    req._builder.toString should matchJsonResource("/json/search/search_term_filter.json")
  }

  it should "generate json for terms filter" in {
    val req = search in "music" types "bands" filter {
      termsFilter("singer", "chris", "martin") cacheKey "band-singers" name "my-filter" execution ("fielddata")
    } preference Preference.Shards("a")
    req._builder.toString should matchJsonResource("/json/search/search_terms_filter.json")
  }

  it should "generate json for terms lookup filter" in {
    val req = search in "music" types "bands" filter {
      termsLookupFilter("user") index "users" lookupType "user" id "2" path "followers" routing "user-2" lookupCache true cache true cacheKey "user-lookup-for-id-2" name "users-lookup"
    }
    req._builder.toString should matchJsonResource("/json/search/search_terms_lookup_filter.json")
  }

  it should "generate json for regex filter" in {
    val req = search in "music" types "bands" filter {
      regexFilter("singer", "chris martin") cache false name "my-filter2" cacheKey "mykey"
    } preference Preference.PreferNode("a")
    req._builder.toString should matchJsonResource("/json/search/search_regex_filter.json")
  }

  it should "generate json for prefix filter" in {
    val req = search in "music" types "bands" filter {
      prefixFilter("singer", "chris martin") cache true cacheKey "band-singers" name "my-filter3"
    } preference Preference.Primary
    req._builder.toString should matchJsonResource("/json/search/search_prefix_filter.json")
  }

  it should "generate json for has child filter with filter" in {
    val req = search in "music" types "bands" filter {
      hasChildFilter("singer") filter {
        termFilter("name", "chris")
      } name "my-filter4"
    } preference Preference.Primary
    req._builder.toString should matchJsonResource("/json/search/search_haschild_filter.json")
  }

  it should "generate json for has parent filter with filter" in {
    val req = search in "music" types "bands" filter {
      hasParentFilter("singer") filter {
        termFilter("name", "chris")
      } name "my-filter5"
    } preference Preference.Primary
    req._builder.toString should matchJsonResource("/json/search/search_hasparent_filter.json")
  }

  it should "generate json for nested filter with filter" in {
    val req = search in "music" types "bands" filter {
      nestedFilter("singer") filter {
        termFilter("name", "chris")
      } filterName "my-filter6" join true
    } preference Preference.Primary
    req._builder.toString should matchJsonResource("/json/search/search_nested_filter.json")
  }

  it should "generate json for has child filter with query" in {
    val req = search in "music" types "bands" filter {
      hasChildFilter("singer") query {
        termQuery("name", "chris")
      }
    } preference Preference.Primary
    req._builder.toString should matchJsonResource("/json/search/search_haschild_filter_query.json")
  }

  it should "generate json for has parent filter with query" in {
    val req = search in "music" types "bands" filter {
      hasParentFilter("singer") query {
        termQuery("name", "chris")
      }
    } preference Preference.Primary
    req._builder.toString should matchJsonResource("/json/search/search_hasparent_filter_query.json")
  }

  it should "generate json for nested filter with query" in {
    val req = search in "music" types "bands" filter {
      nestedFilter("singer") query {
        termQuery("name", "chris")
      }
    } preference Preference.Primary
    req._builder.toString should matchJsonResource("/json/search/search_nested_filter_query.json")
  }

  it should "generate json for id filter" in {
    val req = search in "music" types "bands" filter {
      idsFilter("a", "b", "c") withIds ("q", "r") filterName "some-name"
    } preference Preference.PrimaryFirst
    req._builder.toString should matchJsonResource("/json/search/search_id_filter.json")
  }

  it should "generate json for type filter" in {
    val req = search in "music" types "bands" filter {
      typeFilter("sometype")
    } preference new Shards("5", "7")
    req._builder.toString should matchJsonResource("/json/search/search_type_filter.json")
  }

  it should "generate json for type numeric filter" in {
    val req = search in "music" types "bands" filter {
      numericRangeFilter("years") cache true cacheKey "key" includeLower true includeUpper true gte 1900 lte 2100
    } preference new Shards("5", "7")
    req._builder.toString should matchJsonResource("/json/search/search_numeric_filter.json")
  }

  it should "generate json for type numeric filter2" in {
    val req = search in "music" types "bands" filter {
      numericRangeFilter("years") cache true cacheKey "key" gte 12.4 lte 45.5
    } preference new Shards("5", "7")
    req._builder.toString should matchJsonResource("/json/search/search_numeric_filter2.json")
  }

  it should "generate json for type numeric filter3" in {
    val req = search in "music" types "bands" filter {
      numericRangeFilter("years") cache true cacheKey "key" gt 12.4 lt 45.5 filterName "superfilter"
    }
    req._builder.toString should matchJsonResource("/json/search/search_numeric_filter3.json")
  }

  it should "generate json for type range filter" in {
    val req = search in "music" types "bands" filter {
      rangeFilter("released") cache true cacheKey "key" includeLower true includeUpper true gte "2010-01-01" lte "2012-12-12" execution ("fielddata")
    } preference new Shards("5", "7")
    req._builder.toString should matchJsonResource("/json/search/search_range_filter.json")
  }

  it should "generate json for missing filter" in {
    val req = search in "music" types "bands" filter {
      missingFilter("producer") existence true filterName "named" includeNull true
    } preference Preference.PrimaryFirst
    req._builder.toString should matchJsonResource("/json/search/search_missing_filter.json")
  }

  it should "generate json for field sort" in {
    val req = search in "music" types "bands" sort {
      by field "singer" ignoreUnmapped true missing "no-singer" order SortOrder.DESC mode MultiMode
        .Avg nestedPath "nest"
    }
    req._builder.toString should matchJsonResource("/json/search/search_sort_field.json")
  }

  it should "generate json for nested field sort" in {
    val req = search in "music" types "bands" sort {
      by field "singer.weight" ignoreUnmapped true order SortOrder.DESC mode MultiMode
        .Sum nestedFilter termFilter("singer.name", "coldplay")
    }
    req._builder.toString should matchJsonResource("/json/search/search_sort_nested_field.json")
  }

  it should "generate correct json for score sort" in {
    val req = search in "music" types "bands" sort {
      by.score.missing("213").order(SortOrder.ASC)
    }
    req._builder.toString should matchJsonResource("/json/search/search_sort_score.json")
  }

  it should "generate correct json for script sort" in {
    val req = search in "music" types "bands" sort {
      by script "document.score" typed "number" lang "java" order SortOrder.DESC nestedPath "nested.path" sortMode "min"
    } preference new Preference.Custom("custom-node")
    req._builder.toString should matchJsonResource("/json/search/search_sort_script.json")
  }

  it should "generate correct json for script sort with params" in {
    val req = search in "music" types "bands" sort {
      by script "doc.score" typed "number" order SortOrder.DESC params Map("param1" -> "value1", "param2" -> "value2")
    } preference new Preference.Custom("custom-node")
    req._builder.toString should matchJsonResource("/json/search/search_sort_script_params.json")
  }

  it should "generate correct json for geo sort" in {
    val req = search in "music" types "bands" sort {
      by geo "location" geohash "ABCDEFG" missing "567.8889" order SortOrder.DESC mode
        MultiMode.Sum point (56.6, 78.8) nested "nested-path" mode MultiMode.Max geoDistance GeoDistance.ARC
    }
    req._builder.toString should matchJsonResource("/json/search/search_sort_geo.json")
  }

  it should "generate correct json for multiple sorts" in {
    val req = search in "music" types "bands" sort (
      by script "document.score" as "java" order SortOrder.ASC,
      by.score order SortOrder.DESC,
      by field "dancer" order SortOrder.DESC
    )
    req._builder.toString should matchJsonResource("/json/search/search_sort_multiple.json")
  }

  it should "generate json for field sort with score tracking enabled" in {
    val req = search in "music" types "bands" trackScores true sort {
      by field "singer" order SortOrder.DESC
    }
    req._builder.toString should matchJsonResource("/json/search/search_sort_track_scores.json")
  }

  it should "generate correct json for facets" in {
    val req = search in "music" types "bands" facets (
      facet terms "type" allTerms true exclude "pop" fields "type" executionHint "hinty" global true order TermsFacet
      .ComparatorType.REVERSE_TERM size 10 regex "qwer" script "some-script" nested "nested-path" lang "french",
      facet range "years-active" field "year" range 10 -> 20 global true valueField "myvalue" keyField "mykey" nested "some-nested",
      facet statistical "sales-biz" field "sales" global true nested "if-nested",
      facet termsStats "sales-by-artist" keyField "artist" valueField "sales" global true,
      facet geodistance "distance" field "location" geoDistance GeoDistance
      .FACTOR range 20d -> 30d range 30d -> 40d point (45.4, 54d) valueField "myvalue" global true facetFilter {
        termFilter("location", "europe") cache true cacheKey "cache-key"
      } addUnboundedFrom 100 addUnboundedTo 900 geohash "ABC" valueScript "some.script" lang "java" geoDistance GeoDistance
      .PLANE)
    req._builder.toString should matchJsonResource("/json/search/search_facets.json")
  }

  it should "generate correct json for filter facets" in {
    val req = search in "music" types "bands" facets {
      facet filter "filter-facet" facetFilter {
        prefixFilter("field", "prefixvalue")
      } filter {
        regexFilter("field", "value.*")
      } global true nested "some.path"
    } preference new Preference.OnlyNode("a")
    req._builder.toString should matchJsonResource("/json/search/search_filter_facets.json")
  }

  it should "generate correct json for query facets" in {
    val req = search in "music" types "bands" facets {
      facet query "query-facet" query {
        query("coldplay")
      } facetFilter {
        termFilter("name", "coldplay")
      } global true nested "path.nested"
    }
    req._builder.toString should matchJsonResource("/json/search/search_query_facets.json")
  }

  it should "generate correct json for statistical facets" in {
    val req = search in "school" types "student" query "teacher:Smith" facets {
      facet statistical "class-grades" field "grade" global false nested "nested-path"
    }
    req._builder.toString should matchJsonResource("/json/search/search_statistical_facets.json")
  }

  it should "generate correct json for terms_stats facets" in {
    val req = search in "school" types "student" facets {
      facet termsStats "grades-by-teacher" keyField "teacher" valueField "grade" global true
    }
    req._builder.toString should matchJsonResource("/json/search/search_terms_stats_facets.json")
  }

  it should "generate correct json for geo bounding box filter" in {
    val req = search in "music" types "bands" filter {
      geoboxFilter("box") left 40.6 top 56.5 right 45.5 bottom 12.55 cache true cacheKey "somecachekey"
    }
    req._builder.toString should matchJsonResource("/json/search/search_filter_geo_boundingbox.json")
  }

  it should "generate correct json for dismax query" in {
    val req = search in "music" types "bands" query {
      dismax boost 4.5 query "coldplay" query "london" tieBreaker 1.2
    }
    req._builder.toString should matchJsonResource("/json/search/search_query_dismax.json")
  }

  it should "generate correct json for common terms query" in {
    val req = search in "music" types "bands" query {
      commonQuery("name") text "some text here" analyzer WhitespaceAnalyzer boost 12.3 cutoffFrequency 14.4 highFreqOperator "AND" lowFreqOperator "OR" lowFreqMinimumShouldMatch 45.2 highFreqMinimumShouldMatch 1.2
    }
    req._builder.toString should matchJsonResource("/json/search/search_query_commonterms.json")
  }

  it should "generate correct json for constant score query" in {
    val req = search in "music" types "bands" query {
      constantScore query {
        term("name", "sammy")
      } boost 14.5
    }
    req._builder.toString should matchJsonResource("/json/search/search_query_constantscore.json")
  }

  it should "generate correct json for flt query" in {
    val req = search in "music" types "bands" query {
      fuzzylikethis text "text like this one" fields ("name", "location") analyzer WhitespaceAnalyzer ignoreTF true prefixLength 4 maxQueryTerms 2 boost 1.2
    }
    req._builder.toString should matchJsonResource("/json/search/search_query_flt.json")
  }

  it should "generate correct json for terms query" in {
    val req = search in "music" types "bands" query {
      termsQuery("name", "chris", "will", "johnny", "guy") boost 1.2 minimumShouldMatch 4 disableCoord true
    }
    req._builder.toString should matchJsonResource("/json/search/search_query_terms.json")
  }

  it should "generate correct json for multi match query" in {
    val req = search in "music" types "bands" query {
      multiMatchQuery("this is my query") fields ("name", "location", "genre") analyzer WhitespaceAnalyzer boost 3.4 cutoffFrequency 1.7 fuzziness "something" prefixLength 4 minimumShouldMatch 2 useDisMax true tieBreaker 4.5 zeroTermsQuery
        MatchQueryBuilder.ZeroTermsQuery.ALL fuzzyRewrite "some-rewrite" maxExpansions 4 lenient true prefixLength 4
    }
    req._builder.toString should matchJsonResource("/json/search/search_query_multi_match.json")
  }

  it should "generate correct json for geo distance filter" in {
    val req = search in "music" types "bands" filter {
      bool(
        should(
          geoDistance("distance") point (10.5d, 35.0d) method GeoDistance
            .FACTOR cache true cacheKey "mycache" geohash "geo1234" distance "120mi"
        ) not (
            geoDistance("distance") lat 45.4d lon 76.6d distance (45, DistanceUnit.YARD)
          )
      )
    }
    req._builder.toString should matchJsonResource("/json/search/search_filter_geo_distance.json")
  }

  it should "generate correct json for a rescore query" in {
    val req = search in "music" types "bands" rescore {
      rescore("coldplay").originalQueryWeight(1.4).rescoreQueryWeight(5.4).scoreMode("modey").window(14)
    }
    req._builder.toString should matchJsonResource("/json/search/search_rescore.json")
  }

  it should "generate correct json for function score query" in {
    val req = search in "music" types "bands" query {
      functionScoreQuery("coldplay").boost(1.4).maxBoost(1.9).scoreMode("multiply").boostMode("max").scorers(
        randomScore(1234),
        scriptScore("some script here"),
        gaussianScore("field1", "1m", "2m").filter(termFilter("band", "coldplay")),
        factorScore(1.2).filter(termFilter("band", "taylor swift"))
      )
    }
    req._builder.toString should matchJsonResource("/json/search/search_function_score.json")
  }

  it should "generate correct json for geo polygon filter" in {
    val req = search in "music" types "bands" filter {
      geoPolygon("distance") point (10, 10) point (20, 20) point (30, 30) cache true cacheKey "key" point "123456"
    }
    req._builder.toString should matchJsonResource("/json/search/search_filter_geo_polygon.json")
  }

  it should "generate correct json for a boolean filter" in {
    val req = search in "music" types "bands" filter {
      bool {
        must {
          termFilter("name", "sammy")
        } should {
          termFilter("location", "oxford")
        } not {
          termFilter("type", "rap")
        }
      }
    }
    req._builder.toString should matchJsonResource("/json/search/search_filter_bool.json")
  }

  it should "generate correct json for range facet" in {
    val req = search in "music" types "bands" facets {
      facet range "year" field "years" range (100 -> 160) to (100) from (160)
    }
    req._builder.toString should matchJsonResource("/json/search/search_facets_range.json")
  }

  it should "generate correct json for histogram facet" in {
    val req = search in "music" types "bands" facets {
      facet histogram "years" interval 100 comparator
        ComparatorType.COUNT valueField "myvalue" keyField "mykey" global true nested "nested-path"
    }
    req._builder.toString should matchJsonResource("/json/search/search_facets_histogram.json")
  }

  it should "generate correct json for datehistogram facet" in {
    val req = search in "music" types "bands" facets {
      facet datehistogram "years" interval "year" comparator
        org.elasticsearch.search.facet.datehistogram.DateHistogramFacet.ComparatorType.COUNT valueField
        "myvalue" keyField "mykey" global true nested "nested-path"
    }
    req._builder.toString should matchJsonResource("/json/search/search_facets_datehistogram.json")
  }

  it should "generate correct json for datehistogram aggregation" in {
    val req = search in "music" types "bands" aggs {
      aggregation datehistogram "years" field "date" interval DateHistogram.Interval.YEAR minDocCount 0
    }
    req._builder.toString should matchJsonResource("/json/search/search_aggregations_datehistogram.json")
  }

  it should "generate correct json for range aggregation" in {
    val req = search in "music" types "bands" aggs {
      aggregation range "range_agg" field "score" range (10.0, 15.0)
    }
    req._builder.toString should matchJsonResource("/json/search/search_aggregations_range.json")
  }

  it should "generate correct json for date range aggregation" in {
    val req = search in "music" types "bands" aggs {
      aggregation daterange "daterange_agg" field "date" range ("now-1Y", "now")
    }
    req._builder.toString should matchJsonResource("/json/search/search_aggregations_daterange.json")
  }

  it should "generate correct json for histogram aggregation" in {
    val req = search in "music" types "bands" aggs {
      aggregation histogram "score_histogram" field "score" interval 2
    }
    req._builder.toString should matchJsonResource("/json/search/search_aggregations_histogram.json")
  }

  it should "generate correct json for filter aggregation" in {
    val req = search in "music" types "bands" aggs {
      aggregation filter "my_filter_agg" filter {
        bool {
          must {
            termFilter("name", "sammy")
          } should {
            termFilter("location", "oxford")
          } not {
            termFilter("type", "rap")
          }
        }
      }
    }
    req._builder.toString should matchJsonResource("/json/search/search_aggregations_filter.json")
  }

  it should "generate correct json for terms aggregation" in {
    val req = search in "music" types "bands" aggs {
      aggregation terms "my_terms_agg" field "keyword" size 10 order Terms.Order.count(false)
    }
    req._builder.toString should matchJsonResource("/json/search/search_aggregations_terms.json")
  }

  it should "generate correct json for geodistance aggregation" in {
    val req = search in "music" types "bands" aggs {
      aggregation geodistance "geo_agg" field "geo_point" point (45.0, 27.0) geoDistance GeoDistance.ARC range (1.0, 1.0)
    }
    req._builder.toString should matchJsonResource("/json/search/search_aggregations_geodistance.json")
  }

  it should "generate correct json for sub aggregation" in {
    val req = search in "music" types "bands" aggs {
      aggregation datehistogram "days" field "date" interval DateHistogram.Interval.DAY aggs (
        aggregation terms "keywords" field "keyword" size 5,
        aggregation terms "countries" field "country")
    }
    req._builder.toString should matchJsonResource("/json/search/search_aggregations_datehistogram_subs.json")
  }

  it should "generate correct json for min aggregation" in {
    val req = search in "school" types "student" aggs {
      aggregation min "grades_min" field "grade" script "doc['grade'].value" lang "lua" param ("apple", "bad")
    }
    req._builder.toString should matchJsonResource("/json/search/search_aggregations_min.json")
  }

  it should "generate correct json for max aggregation" in {
    val req = search in "school" types "student" aggs {
      aggregation max "grades_max" field "grade" script "doc['grade'].value" lang "lua"
    }
    req._builder.toString should matchJsonResource("/json/search/search_aggregations_max.json")
  }

  it should "generate correct json for sum aggregation" in {
    val req = search in "school" types "student" aggs {
      aggregation sum "grades_sum" field "grade" script "doc['grade'].value" lang "lua" params (Map("classsize" -> 30, "room" -> "101A"))
    }
    req._builder.toString should matchJsonResource("/json/search/search_aggregations_sum.json")
  }

  it should "generate correct json for avg aggregation" in {
    val req = search in "school" types "student" aggs {
      aggregation avg "grades_avg" field "grade" script "doc['grade'].value" lang "lua"
    }
    req._builder.toString should matchJsonResource("/json/search/search_aggregations_avg.json")
  }

  it should "generate correct json for stats aggregation" in {
    val req = search in "school" types "student" aggs {
      aggregation stats "grades_stats" field "grade" script "doc['grade'].value" lang "lua"
    }
    req._builder.toString should matchJsonResource("/json/search/search_aggregations_stats.json")
  }

  it should "generate correct json for extendedstats aggregation" in {
    val req = search in "school" types "student" aggs {
      aggregation extendedstats "grades_extendedstats" field "grade" script "doc['grade'].value" lang "lua"
    }
    req._builder.toString should matchJsonResource("/json/search/search_aggregations_extendedstats.json")
  }

  it should "generate correct json for value count aggregation" in {
    val req = search in "school" types "student" aggs {
      aggregation count "grades_count" field "grade" script "doc['grade'].value" lang "lua"
    }
    req._builder.toString should matchJsonResource("/json/search/search_aggregations_count.json")
  }

  it should "generate correct json for cardinality aggregation" in {
    val req = search in "school" types "student" aggs {
      aggregation cardinality "grades_cardinality" field "grade" rehash true precisionThreshold 40000
    }
    req._builder.toString should matchJsonResource("/json/search/search_aggregations_cardinality.json")
  }

  it should "generate correct json for highlighting" in {
    val req = search in "music" types "bands" highlighting (
      options tagSchema TagSchema.Styled boundaryChars "\\b" boundaryMaxScan 4 order HighlightOrder
      .Score preTags "<b>" postTags "</b>" encoder HighlightEncoder.Html,
      "name" fragmentSize 100 numberOfFragments 3 fragmentOffset 4,
      "type" numberOfFragments 100 fragmentSize 44 highlighterType "some-type"
    )
    req._builder.toString should matchJsonResource("/json/search/search_highlighting.json")
  }

  it should "generate correct json for multiple suggestions" in {
    val req = search in "music" types "bands" query "coldplay" suggestions (
      suggest as "my-suggestion-1" on "clocks by culdpaly" from "names" maxEdits 4 mode Popular shardSize 2 accuracy 0.6,
      suggest as "my-suggestion-2" on "aqualuck by jethro toll" from "names" size 5 mode Missing minDocFreq 0.2 prefixLength 3,
      suggest as "my-suggestion-3" on "bountiful day by u22" from "names" analyzer StandardAnalyzer maxInspections 3 stringDistance "levenstein",
      suggest as "my-suggestion-4" on "whatever some text" from "names" maxTermFreq 0.5 minWordLength 5 mode SuggestMode
      .Always
    )

    // -- disabled due to bug in elastic search
    // req._builder.toString should matchJsonResource("/json/search/search_suggestions_multiple.json")
  }

  // for backwards compatibility default suggester is the term suggester
  it should "generate correct json for suggestions" in {
    val req = search in "music" types "bands" query termQuery("name", "coldplay") suggestions (
      suggest as "suggestion-1" on "clocks by culdpaly" from "name" maxEdits 2,
      suggest as "suggestion-2" on "aqualuck by jethro toll" from "name"
    )
    req._builder.toString should matchJsonResource("/json/search/search_suggestions.json")
  }

  it should "generate correct json for suggestions of multiple suggesters" in {
    val req = search in "music" types "bands" query termQuery("name", "coldplay") suggestions (
      suggest using term as "suggestion-term" on "culdpaly" field "name" maxEdits 2,
      suggest using phrase as "suggestion-phrase" on "aqualuck by jethro toll" field "name",
      suggest using completion as "suggestion-completion" on "cold" field "ac"
    )
    req._builder.toString should matchJsonResource("/json/search/search_suggestions_multiple_suggesters.json")
  }

  it should "generate correct json for nested query" in {
    val req = search in "music" types "bands" query {
      nested("obj1") query {
        constantScore query {
          term("name", "sammy")
        }
      } scoreMode "avg" boost 14.5
    }
    req._builder.toString should matchJsonResource("/json/search/search_query_nested.json")
  }

  it should "generate correct json for a query filter" in {
    val req = search in "*" types ("users", "tweets") filter {
      queryFilter("coldplay").cache(true).filterName("sammysfilter")
    }
    req._builder.toString should matchJsonResource("/json/search/search_query_filter.json")
  }

  it should "generate correct json for a SpanTermQueryDefinition" in {
    val req = search in "*" types ("users", "tweets") query {
      spanTermQuery("name", "coldplay").boost(123)
    }
    req._builder.toString should matchJsonResource("/json/search/search_query_span_term.json")
  }

  it should "generate correct json for a geo distance range filter" in {
    val req = search in "*" types ("users", "tweets") filter {
      geoDistanceRangeFilter("postcode")
        .cache(true)
        .cacheKey("cacheybaby")
        .geohash("hash123")
        .gte(123).lte(123)
        .includeLower(true)
        .includeUpper(true)
        .name("myfilter")
        .to(12).from(560)
    }
    req._builder.toString should matchJsonResource("/json/search/search_filter_geo_range.json")
  }

  it should "generate correct json for a simple string query" in {
    val req = search in "*" types ("users", "tweets") query {
      simpleStringQuery("coldplay")
        .analyzer("whitespace")
        .defaultOperator("AND")
        .field("name")
        .flags(SimpleQueryStringFlag.AND, SimpleQueryStringFlag.OR, SimpleQueryStringFlag.NOT)
    }
    req._builder.toString should matchJsonResource("/json/search/search_simple_string_query.json")
  }

  it should "generate json for or filter" in {
    val req = search in "music" types "bands" filter {
      or(
        termFilter("singer", "chris"),
        termFilter("singer", "sammy")
      ) cache (true) cacheKey "chris-or-sammy" name "my-filter"

    }
    req._builder.toString should matchJsonResource("/json/search/search_or_filter.json")
  }

  it should "generate json for and filter" in {
    val req = search in "music" types "bands" filter {
      and(
        termFilter("singer", "chris"),
        termFilter("singer", "sammy")
      ) cache (true) cacheKey "chris-and-sammy" name "my-filter"

    }
    req._builder.toString should matchJsonResource("/json/search/search_and_filter.json")
  }
}

