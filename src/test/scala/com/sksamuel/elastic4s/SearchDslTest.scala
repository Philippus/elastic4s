package com.sksamuel.elastic4s

import org.scalatest.{FlatSpec, OneInstancePerTest}
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.SearchDsl._
import com.fasterxml.jackson.databind.ObjectMapper
import org.elasticsearch.search.sort.SortOrder

/** @author Stephen Samuel */
class SearchDslTest extends FlatSpec with MockitoSugar with OneInstancePerTest {

    val mapper = new ObjectMapper()

    "the search dsl" should "accept wilcards for index and types" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_test1.json"))
        val req = search in "*" types "*" limit 10
        assert(json === mapper.readTree(req.builder.toString))
    }

    it should "accept sequences for indexes" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_test2.json"))
        val req = search in ("twitter", "other") types "*" limit 5 query "coldplay"
        assert(json === mapper.readTree(req.builder.toString))
    }

    it should "accept sequences for type" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_test3.json"))
        val req = search in "*" types ("users", "tweets") from 5 query "sammy"
        assert(json === mapper.readTree(req.builder.toString))
    }

    it should "use limit and and offset when specified" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_test4.json"))

        val req = search in "*" types ("users", "tweets") limit 6 from 9 query "coldplay"

        println(req.builder.toString)
        assert(json === mapper.readTree(req.builder.toString))
    }

    it should "generate json for a prefix query" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_test5.json"))
        val req = search in "*" types ("users", "tweets") limit 5 prefix "bands" -> "coldplay"
        assert(json === mapper.readTree(req.builder.toString))
    }

    it should "generate json for a term query" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_term.json"))
        val req = search in "*" types ("users", "tweets") limit 5 term "singer" -> "chris martin"
        assert(json === mapper.readTree(req.builder.toString))
    }

    it should "generate json for a range query" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_range.json"))
        val req = search in "*" types ("users", "tweets") limit 5 range "coldplay"
        assert(json === mapper.readTree(req.builder.toString))
    }

    it should "generate json for a regex query" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_regex.json"))
        val req = search in "*" types ("users", "tweets") limit 5 query {
            regex("drummmer" -> "will*") boost 5
        }
        assert(json === mapper.readTree(req.builder.toString))
    }

    it should "generate json for a boolean compound query" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_boolean.json"))

        val req = search in "*" types ("bands", "artists") limit 5 query {
            bool {
                must(
                    regex("drummmer" -> "will*") boost 5,
                    term("singer" -> "chris")
                ) should {
                    term("bassist" -> "berryman")
                } not {
                    term("singer" -> "anderson")
                }
            }
        }
        assert(json === mapper.readTree(req.builder.toString))
    }

    it should "generate json for term filter" in {

        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_term_filter.json"))

        val req = search in "music" types "bands" filter {
            termFilter("singer", "chris martin")
        }

        //  println(req.builder.toString)
        assert(json === mapper.readTree(req.builder.toString))
    }

    it should "generate json for regex filter" in {

        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_regex_filter.json"))

        val req = search in "music" types "bands" filter {
            regexFilter("singer", "chris martin")
        }

        //   println(req.builder.toString)
        assert(json === mapper.readTree(req.builder.toString))
    }

    it should "generate json for missing filter" in {

        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_missing_filter.json"))

        val req = search in "music" types "bands" filter {
            missingFilter("producer")
        }

        //  println(req.builder.toString)
        assert(json === mapper.readTree(req.builder.toString))
    }

    it should "generate json for field sort" in {

        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_sort_field.json"))

        val req = search in "music" types "bands" sort {
            by field "singer"
        }
        println(req.builder.toString)
        assert(json === mapper.readTree(req.builder.toString))
    }

    it should "generate correct json for score sort" in {

        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_sort_score.json"))

        val req = search in "music" types "bands" sort {
            by score
        }

        println(req.builder.toString)
        assert(json === mapper.readTree(req.builder.toString))
    }

    it should "generate correct json for script sort" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_sort_script.json"))
        val req = search in "music" types "bands" sort {
            by script "document.score" as "java" order SortOrder.DESC
        }
        println(req.builder.toString)
        assert(json === mapper.readTree(req.builder.toString))
    }

    it should "generate correct json for mutliple sorts" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_sort_multiple.json"))
        val req = search in "music" types "bands" sort (
          by script "document.score" as "java" order SortOrder.ASC,
          by.score order SortOrder.DESC,
          by field "dancer" order SortOrder.DESC
          )
        println(req.builder.toString)
        assert(json === mapper.readTree(req.builder.toString))
    }
}
