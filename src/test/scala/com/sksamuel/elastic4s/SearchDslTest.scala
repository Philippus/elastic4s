package com.sksamuel.elastic4s

import org.scalatest.{FlatSpec, OneInstancePerTest}
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.SearchDsl._
import com.fasterxml.jackson.databind.ObjectMapper

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

        println(req.builder.toString)
        assert(json === mapper.readTree(req.builder.toString))
    }

    it should "generate json for a term query" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_term.json"))

        val req = search in "*" types ("users", "tweets") limit 5 term "singer" -> "chris martin"

        println(req.builder.toString)
        assert(json === mapper.readTree(req.builder.toString))
    }

    it should "generate json for a range query" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_range.json"))

        val req = search in "*" types ("users", "tweets") limit 5 range "coldplay"

        println(req.builder.toString)
        assert(json === mapper.readTree(req.builder.toString))
    }

    it should "generate json for a regex query" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_regex.json"))

        val req = search in "*" types ("users", "tweets") limit 5 query {
            regex("drummmer" -> "will*") boost 5
        }

        println(req.builder.toString)
        assert(json === mapper.readTree(req.builder.toString))
    }

    it should "generate json for a boolean compound query" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/search_regex.json"))

        val req = search in "*" types ("users", "tweets") limit 5 query {
            bool {
                must(
                    regex("drummmer" -> "will*") boost 5,
                    term("singer" -> "chris")
                ) should (
                  regex("drummmer" -> "will*") boost 5,
                  term("singer" -> "chris")
                  )
            }
        }

        println(req.builder.toString)
        assert(json === mapper.readTree(req.builder.toString))
    }


    //    search in "twitter" types "tweets" limit 10 start 3 query {
    //
    //        string query "I love searching" boost 2.5 operator "and" anaylyzer WhitespaceAnalyzer
    //        prefix query "users:sam" boost 4.5
    //        term query "location" -> "london" boost 4
    //        matches query "job" -> "developer" operator "OR"
    //        range query "age" from 12 to 4
    //        regex query "name" -> "sam.*"
    //
    //    }

    //            fields("user" fragmentSize 150 numberOfFragments 3,
    //                "location" fragmentSize 150 numberOfFragments 3,
    //                "age")
    //        }
    //
    //
    //        qqqqq.fields("user" fragmentSize 150 numberOfFragments 3,
    //            "location" fragmentSize 150 numberOfFragments 3,
    //            "age")
    //
    //        fields(
    //            "user" fragmentSize 150 numberOfFragments 3,
    //            "location" fragmentSize 150 numberOfFragments 3,
    //            "age"

}
