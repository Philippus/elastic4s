package com.sksamuel.elastic4s

import org.scalatest.{FlatSpec, OneInstancePerTest}
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.ElasticDsl._
import com.fasterxml.jackson.databind.ObjectMapper
import org.elasticsearch.search.sort.SortOrder
import com.sksamuel.elastic4s.SuggestMode.{Missing, Popular}
import org.elasticsearch.index.query.{MatchQueryBuilder, RegexpFlag}
import org.elasticsearch.search.facet.histogram.HistogramFacet.ComparatorType
import org.elasticsearch.search.facet.terms.TermsFacet
import org.elasticsearch.common.geo.GeoDistance
import org.elasticsearch.common.unit.DistanceUnit
import com.sksamuel.elastic4s.Preference.Shards
import org.elasticsearch.index.query.MatchQueryBuilder.ZeroTermsQuery

/** @author Stephen Samuel */
class SearchDslTest extends FlatSpec with MockitoSugar with OneInstancePerTest {

  val mapper = new ObjectMapper()

  "the search dsl" should "accept wilcards for index and types" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_test1.json"))
    val req = search in "*" types "*" limit 10
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "accept sequences for indexes" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_test2.json"))
    val req = search in("twitter", "other") types "*" limit 5 query "coldplay"
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "accept sequences for type" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_test3.json"))
    val req = search in "*" types("users", "tweets") from 5 query "sammy"
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "use limit and and offset when specified" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_test4.json"))
    val req = search in "*" types("users", "tweets") limit 6 from 9 query "coldplay"
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "use preference when specified" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_preference_primary_first.json"))
    val req = search in "*" types("users", "tweets") query "coldplay" preference Preference.PrimaryFirst
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "use custom preference when specified" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_preference_custom.json"))
    val req = search in "*" types("users", "tweets") query "coldplay" preference Preference.Custom("custom")
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for a prefix query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_test5.json"))
    val req = search in "*" types("users", "tweets") limit 5 query {
      prefix("bands" -> "coldplay") boost 5 rewrite "yes"
    } searchType SearchType.Scan
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for a term query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_term.json"))
    val req = search in "*" types("users", "tweets") limit 5 query {
      term("singer", "chris martin") boost 1.6
    } searchType SearchType.DfsQueryAndFetch
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for a range query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_range.json"))
    val req = search in "*" types("users", "tweets") limit 5 query {
      range("coldplay") includeLower true includeUpper true from 4 to 10 boost 1.2
    } searchType SearchType.QueryThenFetch
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for a wildcard query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_wildcard.json"))
    val req = search in "*" types("users", "tweets") limit 5 query {
      wildcard("name", "*coldplay") boost 7.6 rewrite "no"
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for a string query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_string.json"))
    val req = search in "*" types("users", "tweets") limit 5 query {
      query("coldplay") allowLeadingWildcard true analyzeWildcard true anaylyzer WhitespaceAnalyzer autoGeneratePhraseQueries true defaultField "name" boost 6.5 enablePositionIncrements true fuzzyMaxExpansions 4 fuzzyMinSim 0.9 fuzzyPrefixLength 3 lenient true phraseSlop 10 tieBreaker 0.5 operator "OR" rewrite "writer"
    } searchType SearchType.DfsQueryThenFetch
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for a regex query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_regex.json"))
    val req = search in "*" types("users", "tweets") limit 5 query {
      regex("drummmer" -> "will*") boost 4 flags RegexpFlag.INTERSECTION rewrite "rewrite-to"
    } searchType SearchType.DfsQueryAndFetch
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for a min score" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_minscore.json"))
    val req = search in "*" types("users", "tweets") query "coldplay" minScore 0.5
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for an index boost" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_indexboost.json"))
    val req = search in "*" types("users", "tweets") query "coldplay" indexBoost("index1" -> 1.4, "index2" -> 1.3)
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for a bpoosting query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_boosting.json"))
    val req = search in "*" types("users", "tweets") limit 5 query {
      boosting positive {
        query("coldplay")
      } negative {
        query("jethro tull")
      } negativeBoost 5.6 positiveBoost 7.6
    } searchType SearchType.DfsQueryAndFetch
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for a id query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_id.json"))
    val req = search in "*" types("users", "tweets") limit 5 query {
      ids("1", "2", "3") boost 1.6 types("a", "b")
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for a match query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_match.json"))
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
    } searchType SearchType.Count
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for a match query with default as or" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_match_or.json"))
    val req = search in "*" types("users", "tweets") limit 5 query {
      matches("drummmer" -> "will") boost 4 operator "OR"
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for a fuzzy query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_fuzzy.json"))
    val req = search in "*" types("users", "tweets") limit 5 query {
      fuzzy("drummmer", "will") boost 4 maxExpansions 10 prefixLength 10 transpositions true minSimilarity 2.2
    } searchType SearchType.Count
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for a filtered query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_query_filteredquery.json"))
    val req = search in "music" types "bands" query {
      filteredQuery query {
        "coldplay"
      } filter {
        termFilter("location", "uk")
      } boost 1.2
    } preference Preference.Primary
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for a match all query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_match_all.json"))
    val req = search in "*" types("users", "tweets") limit 5 query {
      matchall boost 4 normsField "norm-field"
    } searchType SearchType.QueryAndFetch
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for a hasChild query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_haschild_query.json"))
    val req = search in "*" types("users", "tweets") limit 5 query {
      hasChildQuery("sometype") query {
        "coldplay"
      } boost 1.2 scoreType "type"
    } searchType SearchType.QueryThenFetch
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for a topChildren query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_topchildren_query.json"))
    val req = search in "*" types("users", "tweets") limit 5 query {
      topChildren("sometype") query {
        "coldplay"
      } boost 1.2 factor 3 incrementalFactor 2 score "max"
    } searchType SearchType.QueryThenFetch
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for a hasParent query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_hasparent_query.json"))
    val req = search in "*" types("users", "tweets") limit 5 query {
      hasParentQuery("sometype") query {
        "coldplay"
      } boost 1.2 scoreType "type"
    } searchType SearchType.Count preference new Preference.Custom("custompref")
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for a boolean compound query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_boolean.json"))
    val req = search in "*" types("bands", "artists") limit 5 query {
      bool {
        must(
          regex("drummmer" -> "will*") boost 5,
          term("singer" -> "chris")
        ) should term("bassist" -> "berryman") not term("singer" -> "anderson")
      }
    } preference Preference.Local
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for a field query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_field.json"))
    val req = search("*").types("bands", "artists").limit(5).bool(
      must(
        field("name",
          "coldplay") allowLeadingWildcard true analyzeWildcard false boost 5 fuzzyPrefixLength 5 phraseSlop 9,
        field("status",
          "awesome") analyzer PatternAnalyzer autoGeneratePhraseQueries true enablePositionIncrements true,
        field("location", "oxford") fuzzyMinSim 0.5 fuzzyMaxExpansions 7 rewrite "rewrite"
      )
    ) preference Preference.OnlyNode("a")
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for a match phrase query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_match_phrase.json"))
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
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for a match phrase prefix query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_match_phrase_prefix.json"))
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
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for term filter" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_term_filter.json"))
    val req = search in "music" types "bands" filter {
      termFilter("singer", "chris martin") cacheKey "band-singers" name "my-filter"
    } preference Preference.Shards("a")
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for terms filter" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_terms_filter.json"))
    val req = search in "music" types "bands" filter {
      termsFilter("singer", "chris", "martin") cacheKey "band-singers" name "my-filter"
    } preference Preference.Shards("a")
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for regex filter" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_regex_filter.json"))
    val req = search in "music" types "bands" filter {
      regexFilter("singer", "chris martin") cache false name "my-filter2" cacheKey "mykey"
    } preference Preference.PreferNode("a")
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for prefix filter" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_prefix_filter.json"))
    val req = search in "music" types "bands" filter {
      prefixFilter("singer", "chris martin") cache true cacheKey "band-singers" name "my-filter3"
    } preference Preference.Primary
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for has child filter with filter" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_haschild_filter.json"))
    val req = search in "music" types "bands" filter {
      hasChildFilter("singer") filter {
        termFilter("name", "chris")
      } cache true cacheKey "band-singers" name "my-filter4"
    } preference Preference.Primary
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for has parent filter with filter" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_hasparent_filter.json"))
    val req = search in "music" types "bands" filter {
      hasParentFilter("singer") filter {
        termFilter("name", "chris")
      } cache true cacheKey "band-singers" name "my-filter5"
    } preference Preference.Primary
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for has child filter with query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_haschild_filter_query.json"))
    val req = search in "music" types "bands" filter {
      hasChildFilter("singer") query {
        termQuery("name", "chris")
      }
    } preference Preference.Primary
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for has parent filter with query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_hasparent_filter_query.json"))
    val req = search in "music" types "bands" filter {
      hasParentFilter("singer") query {
        termQuery("name", "chris")
      }
    } preference Preference.Primary
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for id filter" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_id_filter.json"))
    val req = search in "music" types "bands" filter {
      idsFilter("a", "b", "c") withIds("q", "r") filterName "some-name"
    } preference Preference.PrimaryFirst
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for type filter" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_type_filter.json"))
    val req = search in "music" types "bands" filter {
      typeFilter("sometype")
    } preference new Shards("5", "7")
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for type numeric filter" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_numeric_filter.json"))
    val req = search in "music" types "bands" filter {
      numericRangeFilter("years") cache true cacheKey "key" includeLower true includeUpper true gte 1900 lte 2100
    } preference new Shards("5", "7")
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for type numeric filter2" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_numeric_filter2.json"))
    val req = search in "music" types "bands" filter {
      numericRangeFilter("years") cache true cacheKey "key" gte 12.4 lte 45.5
    } preference new Shards("5", "7")
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for type numeric filter3" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_numeric_filter3.json"))
    val req = search in "music" types "bands" filter {
      numericRangeFilter("years") cache true cacheKey "key" gt 12.4 lt 45.5 filterName "superfilter"
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for type range filter" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_range_filter.json"))
    val req = search in "music" types "bands" filter {
      rangeFilter("released") cache true cacheKey "key" includeLower true includeUpper true gte "2010-01-01" lte "2012-12-12"
    } preference new Shards("5", "7")
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for missing filter" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_missing_filter.json"))
    val req = search in "music" types "bands" filter {
      missingFilter("producer") existence true filterName "named" includeNull true
    } preference Preference.PrimaryFirst
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate json for field sort" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_sort_field.json"))
    val req = search in "music" types "bands" sort {
      by field "singer" ignoreUnmapped true missing "no-singer" order SortOrder.DESC mode MultiMode
        .Avg nestedPath "nest"
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for score sort" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_sort_score.json"))
    val req = search in "music" types "bands" sort {
      by.score.missing("213").order(SortOrder.ASC)
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for script sort" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_sort_script.json"))
    val req = search in "music" types "bands" sort {
      by script "document.score" typed "number" lang "java" order SortOrder.DESC nestedPath "nested.path" sortMode "min"
    } preference new Preference.Custom("custom-node")
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for geo sort" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_sort_geo.json"))
    val req = search in "music" types "bands" sort {
      by geo "location" geohash "ABCDEFG" missing "567.8889" order SortOrder.DESC mode
        MultiMode.Sum point(56.6, 78.8) nested "nested-path" mode MultiMode.Max geoDistance GeoDistance.ARC
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for multiple sorts" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_sort_multiple.json"))
    val req = search in "music" types "bands" sort(
      by script "document.score" as "java" order SortOrder.ASC,
      by.score order SortOrder.DESC,
      by field "dancer" order SortOrder.DESC
      )
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for facets" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_facets.json"))
    val req = search in "music" types "bands" facets(
      facet terms "type" allTerms true exclude "pop" fields "type" executionHint "hinty" global true order TermsFacet
        .ComparatorType.REVERSE_TERM size 10 regex "qwer" script "some-script" nested "nested-path" lang "french",
      facet range "years-active" field "year" range 10 -> 20 global true valueField "myvalue" keyField "mykey" nested "some-nested",
      facet geodistance "distance" field "location" geoDistance GeoDistance
        .FACTOR range 20d -> 30d range 30d -> 40d point(45.4, 54d) valueField "myvalue" global true facetFilter {
        termFilter("location", "europe") cache true cacheKey "cache-key"
      } addUnboundedFrom 100 addUnboundedTo 900 geohash "ABC" valueScript "some.script" lang "java" geoDistance GeoDistance
        .PLANE)
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for filter facets" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_filter_facets.json"))
    val req = search in "music" types "bands" facets {
      facet filter "filter-facet" facetFilter {
        prefixFilter("field", "prefixvalue")
      } filter {
        regexFilter("field", "value.*")
      } global true nested "some.path"
    } preference new Preference.OnlyNode("a")
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for query facets" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_query_facets.json"))
    val req = search in "music" types "bands" facets {
      facet query "query-facet" query {
        query("coldplay")
      } facetFilter {
        termFilter("name", "coldplay")
      } global true nested "path.nested"
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for geo bounding box filter" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_filter_geo_boundingbox.json"))
    val req = search in "music" types "bands" filter {
      geoboxFilter("box") left 40.6 top 56.5 right 45.5 bottom 12.55 cache true cacheKey "somecachekey"
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for dismax query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_query_dismax.json"))
    val req = search in "music" types "bands" query {
      dismax boost 4.5 query "coldplay" query "london" tieBreaker 1.2
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for common terms query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_query_commonterms.json"))
    val req = search in "music" types "bands" query {
      commonQuery("name") text "some text here" analyzer WhitespaceAnalyzer boost 12.3 cutoffFrequency 14.4 highFreqOperator "AND" lowFreqOperator "OR" lowFreqMinimumShouldMatch 45.2 highFreqMinimumShouldMatch 1.2
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for constant score query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_query_constantscore.json"))
    val req = search in "music" types "bands" query {
      constantScore query {
        term("name", "sammy")
      } boost 14.5
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for flt query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_query_flt.json"))
    val req = search in "music" types "bands" query {
      fuzzylikethis text "text like this one" fields("name", "location") analyzer WhitespaceAnalyzer ignoreTF true prefixLength 4 maxQueryTerms 2 minSimilarity 0.4 boost 1.2
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for terms query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_query_terms.json"))
    val req = search in "music" types "bands" query {
      termsQuery("name", "chris", "will", "johnny", "guy") boost 1.2 minimumShouldMatch 4 disableCoord true
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for custom boost query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_query_customboost.json"))
    val req = search in "music" types "bands" query {
      customBoost query {
        regex("place", "Lon.*")
      } boostFactor 10.0
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for custom score query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_query_custom_score.json"))
    val req = search in "music" types "bands" query {
      customScore script "somescript" lang "java" query "coldplay" boost 45.4
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for multi match query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_query_multi_match.json"))
    val req = search in "music" types "bands" query {
      multiMatchQuery("this is my query") fields("name", "location", "genre") analyzer WhitespaceAnalyzer boost 3.4 cutoffFrequency 1.7 fuzziness "something" prefixLength 4 minimumShouldMatch 2 useDisMax true tieBreaker 4.5 zeroTermsQuery
        MatchQueryBuilder.ZeroTermsQuery.ALL fuzzyRewrite "some-rewrite" maxExpansions 4 lenient true prefixLength 4
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for geo distance filter" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_filter_geo_distance.json"))
    val req = search in "music" types "bands" filter {
      bool(
        should(
          geoDistance("distance") point(10.5d, 35.0d) method GeoDistance
            .FACTOR cache true cacheKey "mycache" geohash "geo1234" distance "120mi"
        ) not (
          geoDistance("distance") lat 45.4d lon 76.6d distance(45, DistanceUnit.YARD)
          )
      )
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for a rescore query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_rescore.json"))
    val req = search in "music" types "bands" rescore {
      rescore("coldplay").originalQueryWeight(1.4).rescoreQueryWeight(5.4).scoreMode("modey").window(14)
    }
    assert(json === mapper.readTree(req._builder.toString))
  }


  it should "generate correct json for function score query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_function_score.json"))
    val req = search in "music" types "bands" query {
      functionScoreQuery("coldplay").boost(1.4).maxBoost(1.9).scoreMode("multiply").boostMode("max").scorers(
        randomScore(1234),
        scriptScore("some script here"),
        gaussianScore("field1", "1m", "2m").filter(termFilter("band", "coldplay")),
        factorScore(1.2).filter(termFilter("band", "taylor swift"))
      )
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for geo polygon filter" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_filter_geo_polygon.json"))
    val req = search in "music" types "bands" filter {
      geoPolygon("distance") point(10, 10) point(20, 20) point(30, 30) cache true cacheKey "key" point "123456"
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for a boolean filter" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_filter_bool.json"))
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
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for histogram facet" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_facets_histogram.json"))
    val req = search in "music" types "bands" facets {
      facet histogram "years" interval 100 comparator
        ComparatorType.COUNT valueField "myvalue" keyField "mykey" global true nested "nested-path"
    }
    assert(json === mapper.readTree(req._builder.toString))
  }


    it should "generate correct json for datehistogram facet" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_facets_datehistogram.json"))
    val req = search in "music" types "bands" facets {
      facet datehistogram "years" interval "year" comparator
      org.elasticsearch.search.facet.datehistogram.DateHistogramFacet.ComparatorType.COUNT valueField
        "myvalue" keyField "mykey" global true nested "nested-path"
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for highlighting" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_highlighting.json"))
    val req = search in "music" types "bands" highlighting(
      options tagSchema TagSchema.Styled boundaryChars "\\b" boundaryMaxScan 4 order HighlightOrder
        .Score preTags "<b>" postTags "</b>" encoder HighlightEncoder.Html,
      "name" fragmentSize 100 numberOfFragments 3 fragmentOffset 4,
      "type" numberOfFragments 100 fragmentSize 44 highlighterType "some-type"
      )
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for multiple suggestions" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_suggestions_multiple.json"))
    val req = search in "music" types "bands" query "coldplay" suggestions(
      suggest as "my-suggestion-1" on "clocks by culdpaly" from "names" maxEdits 4 mode Popular shardSize 2 accuracy 0.6,
      suggest as "my-suggestion-2" on "aqualuck by jethro toll" from "names" size 5 mode Missing minDocFreq 0.2 prefixLength 3,
      suggest as "my-suggestion-3" on "bountiful day by u22" from "names" analyzer StandardAnalyzer maxInspections 3 stringDistance "levenstein",
      suggest as "my-suggestion-4" on "whatever some text" from "names" maxTermFreq 0.5 minWordLength 5 mode SuggestMode
        .Always
      )
    // -- disabled due to bug in elastic search
    //   assert(json === mapper.readTree(req.builder.toString))
  }

  // for backwards compatibility default suggester is the term suggester
  it should "generate correct json for suggestions" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_suggestions.json"))
    val req = search in "music" types "bands" query termQuery("name", "coldplay") suggestions(
      suggest as "suggestion-1" on "clocks by culdpaly" from "name" maxEdits 2,
      suggest as "suggestion-2" on "aqualuck by jethro toll" from "name"
    )
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for suggestions of multiple suggesters" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_suggestions_multiple_suggesters.json"))
    val req = search in "music" types "bands" query termQuery("name", "coldplay") suggestions(
      suggest using term as "suggestion-term" on "culdpaly" field "name" maxEdits 2,
      suggest using phrase as "suggestion-phrase" on "aqualuck by jethro toll" field "name",
      suggest using completion as "suggestion-completion" on "cold" field "ac"
    )
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for nested query" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_query_nested.json"))
    val req = search in "music" types "bands" query {
      nested("obj1") query {
        constantScore query {
          term("name", "sammy")
        }
      } scoreMode "avg" boost 14.5
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for a query filter" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_query_filter.json"))
    val req = search in "*" types("users", "tweets") filter {
      queryFilter("coldplay").cache(true).filterName("sammysfilter")
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for a SpanTermQueryDefinition" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_query_span_term.json"))
    val req = search in "*" types("users", "tweets") query {
      spanTermQuery("name", "coldplay").boost(123)
    }
    assert(json === mapper.readTree(req._builder.toString))
  }

  it should "generate correct json for a geo distance range filter" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_filter_geo_range.json"))
    val req = search in "*" types("users", "tweets") filter {
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
    assert(json === mapper.readTree(req._builder.toString))
  }
}


