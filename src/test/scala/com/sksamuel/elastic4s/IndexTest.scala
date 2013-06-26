package com.sksamuel.elastic4s

import org.scalatest.{FlatSpec, OneInstancePerTest}
import org.scalatest.mock.MockitoSugar
import IndexDsl._
import com.fasterxml.jackson.databind.ObjectMapper

/** @author Stephen Samuel */
class IndexTest extends FlatSpec with MockitoSugar with OneInstancePerTest {

    val mapper = new ObjectMapper()

    "an index dsl" should "accept index and type as a / delimited string" in {

        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/index_test1.json"))

        val req = index.into("twitter/tweets").id("thisid").fields {
            "name" -> "sksamuel"
        }

        println(req._source.string)
        assert(json === mapper.readTree(req._source.string))
    }

    it should "accept index and type as a tuple" in {

        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/index_test2.json"))

        val req = index into "twitter" -> "tweets" fields {
            "name" -> "sksamuel"
        }

        println(req._source.string)
        assert(json === mapper.readTree(req._source.string))
    }

    it should "generate json for all fields" in {

        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/index_test3.json"))

        val req = index into "twitter/tweet" id 1234 fields (
          "user" -> "sammy",
          "post_date" -> "2009-11-15T14:12:12",
          "message" -> "trying out Elastic Search Scala DSL"
          )

        println(req._source.string)
        assert(json === mapper.readTree(req._source.string))
    }

    it should "generate json for fields when using a map" in {

        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/index_test4.json"))

        val req = index into "twitter/tweet" id 1234 fields Map("user" -> "sammy",
            "post_date" -> "2009-11-15T14:12:12",
            "message" -> "trying out Elastic Search Scala DSL")

        println(req._source.string)
        assert(json === mapper.readTree(req._source.string))
    }

    it should "not include id when id is not specified" in {

        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/index_test5.json"))

        val req = index into "twitter/tweet" fields (
          "user" -> "sammy",
          "post_date" -> "2009-11-15T14:12:12",
          "message" -> "trying out Elastic Search Scala DSL"
          )

        println(req._source.string)
        assert(json === mapper.readTree(req._source.string))
    }

    it should "include id when id is specified" in {

        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/index_test6.json"))

        val req = index into "twitter/tweet" id 9999 fields (
          "user" -> "sammy",
          "post_date" -> "2011-11-15T14:12:12",
          "message" -> "I have an ID"
          ) routing "users" ttl 100000

        println(req._source.string)
        assert(json === mapper.readTree(req._source.string))
    }
}
