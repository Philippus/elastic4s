package com.sksamuel.elastic4s

import org.scalatest.{OneInstancePerTest, FunSuite}
import org.scalatest.mock.MockitoSugar

/** @author Stephen Samuel */
class IndexReqTest extends FunSuite with MockitoSugar with OneInstancePerTest with IndexDsl {

    test("index dsl generates a request to json spec") {

        index("twitter", "tweets", "34") {

            routing("kusers")
            version(4)
            timestamp("2009-11-15T14:12:12")

            "user" -> "sammy"
            "post_date" -> "2009-11-15T14:12:12"
            "message" -> "trying out Elastic Search Scala DSL"
        }



        index.into("twitter").mapping("tweet").id("thisid")

        index into "twitter/tweet" id 1234 routing "superusers" fields {
            "user" -> "sammy"
            "post_date" -> "2009-11-15T14:12:12"
            "message" -> "trying out Elastic Search Scala DSL"
        }

        insert {
            "user" -> "sammy"
            "post_date" -> "2009-11-15T14:12:12"
            "message" -> "trying out Elastic Search Scala DSL"
        } into "twitter/tweet" id 12345 routing "superusers" version 4 ttl 45
    }
}
