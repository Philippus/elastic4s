package com.sksamuel.elastic4s

import org.scalatest.{FlatSpec, OneInstancePerTest}
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.ElasticDsl._
import com.fasterxml.jackson.databind.ObjectMapper
import org.elasticsearch.search.sort.SortOrder
import com.sksamuel.elastic4s.SuggestMode.{Missing, Popular}
import com.sksamuel.elastic4s.Analyzer.WhitespaceAnalyzer
import scala.Predef._
import org.elasticsearch.index.query.RegexpFlag

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
        val req = search in ("twitter", "other") types "*" limit 5 query "coldplay"
        assert(json === mapper.readTree(req._builder.toString))
    }

    it should "accept sequences for type" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_test3.json"))
        val req = search in "*" types ("users", "tweets") from 5 query "sammy"
        assert(json === mapper.readTree(req._builder.toString))
    }

    it should "use limit and and offset when specified" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_test4.json"))
        val req = search in "*" types ("users", "tweets") limit 6 from 9 query "coldplay"
        println(req._builder.toString)
        assert(json === mapper.readTree(req._builder.toString))
    }

    it should "use preference when specified" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_preference_primary_first.json"))
        val req = search in "*" types ("users", "tweets") query "coldplay" preference Preference.PrimaryFirst
        println(req._builder.toString)
        assert(json === mapper.readTree(req._builder.toString))
    }

    it should "use custom preference when specified" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_preference_custom.json"))
        val req = search in "*" types ("users", "tweets") query "coldplay" preference new Preference.Custom("custom")
        println(req._builder.toString)
        assert(json === mapper.readTree(req._builder.toString))
    }

    it should "generate json for a prefix query" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_test5.json"))
        val req = search in "*" types ("users", "tweets") limit 5 query {
            prefix("bands" -> "coldplay") boost 5 rewrite "yes"
        }
        assert(json === mapper.readTree(req._builder.toString))
    }

    it should "generate json for a term query" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_term.json"))
        val req = search in "*" types ("users", "tweets") limit 5 term "singer" -> "chris martin"
        assert(json === mapper.readTree(req._builder.toString))
    }

    it should "generate json for a range query" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_range.json"))
        val req = search in "*" types ("users", "tweets") limit 5 query {
            range("coldplay") includeLower true includeUpper true from 4 to 10
        }
        assert(json === mapper.readTree(req._builder.toString))
    }

    it should "generate json for a regex query" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_regex.json"))
        val req = search in "*" types ("users", "tweets") limit 5 query {
            regex("drummmer" -> "will*") boost 4 flags RegexpFlag.INTERSECTION
        }
        assert(json === mapper.readTree(req._builder.toString))
    }

    it should "generate json for a boolean compound query" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_boolean.json"))
        val req = search in "*" types ("bands", "artists") limit 5 query {
            bool {
                must(
                    regex("drummmer" -> "will*") boost 5,
                    term("singer" -> "chris")
                ) should term("bassist" -> "berryman") not term("singer" -> "anderson")
            }
        }
        assert(json === mapper.readTree(req._builder.toString))
    }

    it should "generate json for a field query" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_field.json"))
        val req = search in "*" types ("bands", "artists") limit 5 query {
            field("name", "coldplay") allowLeadingWildcard true analyzeWildcard false boost 5 fuzzyPrefixLength 5 phraseSlop 9
        }
        assert(json === mapper.readTree(req._builder.toString))
    }

    it should "generate json for term filter" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_term_filter.json"))
        val req = search in "music" types "bands" filter {
            termFilter("singer", "chris martin") cacheKey "band-singers" name "my-filter"
        }
        assert(json === mapper.readTree(req._builder.toString))
    }

    it should "generate json for regex filter" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_regex_filter.json"))
        val req = search in "music" types "bands" filter {
            regexFilter("singer", "chris martin") cache false name "my-filter2"
        }
        assert(json === mapper.readTree(req._builder.toString))
    }

    it should "generate json for prefix filter" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_prefix_filter.json"))
        val req = search in "music" types "bands" filter {
            prefixFilter("singer", "chris martin") cache true cacheKey "band-singers" name "my-filter3"
        }
        assert(json === mapper.readTree(req._builder.toString))
    }

    it should "generate json for missing filter" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_missing_filter.json"))
        val req = search in "music" types "bands" filter {
            missingFilter("producer")
        }
        assert(json === mapper.readTree(req._builder.toString))
    }

    it should "generate json for field sort" in {

        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_sort_field.json"))

        val req = search in "music" types "bands" sort {
            by field "singer" ignoreUnmapped true missing "no-singer" order SortOrder.DESC mode MultiMode.Sum
        }
        assert(json === mapper.readTree(req._builder.toString))
    }

    it should "generate correct json for score sort" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_sort_score.json"))
        val req = search in "music" types "bands" sort {
            by score
        }
        assert(json === mapper.readTree(req._builder.toString))
    }

    it should "generate correct json for script sort" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_sort_script.json"))
        val req = search in "music" types "bands" sort {
            by script "document.score" as "java" order SortOrder.DESC
        }
        assert(json === mapper.readTree(req._builder.toString))
    }

    it should "generate correct json for multiple sorts" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_sort_multiple.json"))
        val req = search in "music" types "bands" sort (
          by script "document.score" as "java" order SortOrder.ASC,
          by.score order SortOrder.DESC,
          by field "dancer" order SortOrder.DESC
          )
        assert(json === mapper.readTree(req._builder.toString))
    }

    it should "generate correct json for facets" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_facets.json"))
        val req = search in "music" types "bands" facets (
          facet terms "type" allTerms true exclude "pop" fields "type",
          facet range "years-active" field "year" range 10 -> 20 global true valueField "myvalue" keyField "mykey",
          facet geodistance "distance" field "location" range 20d -> 30d range 30d -> 40d point (45.4, 54d) facetFilter {
              termFilter("location", "europe") cache true cacheKey "cache-key"
          })
        assert(json === mapper.readTree(req._builder.toString))
    }

    it should "generate correct json for highlighting" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_highlighting.json"))
        val req = search in "music" types "bands" highlighting (
          highlight field "name" fragmentSize 100 number 3 offset 4,
          highlight field "type" size 100
          )
        assert(json === mapper.readTree(req._builder.toString))
    }

    it should "generate correct json for multiple suggestions" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_suggestions_multiple.json"))
        val req = search in "music" types "bands" query "coldplay" suggestions (
          suggest as "my-suggestion-1" on "clocks by culdpaly" from "names" maxEdits 4 mode Popular shardSize 2 accuracy 0.6,
          suggest as "my-suggestion-2" on "aqualuck by jethro toll" from "names" size 5 mode Missing minDocFreq 0.2 prefixLength 3,
          suggest as "my-suggestion-3" on "bountiful day by u22" from "names" analyzer WhitespaceAnalyzer maxInspections 3 stringDistance "levenstein"
          )
        // -- disabled due to bug in elastic search
        //   assert(json === mapper.readTree(req.builder.toString))
    }
}


