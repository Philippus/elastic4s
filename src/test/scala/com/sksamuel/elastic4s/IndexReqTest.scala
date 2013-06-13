package com.sksamuel.elastic4s

import org.scalatest.{OneInstancePerTest, FunSuite}
import org.scalatest.mock.MockitoSugar

/** @author Stephen Samuel */
class IndexReqTest extends FunSuite with MockitoSugar with OneInstancePerTest with IndexDsl {

    test("index dsl generates a request to json spec") {

        val req = index("twitter", "tweets", "34") {

            routing("kusers")
            version(4)
            timestamp("2009-11-15T14:12:12")

            "user" -> "sammy"
            "post_date" -> "2009-11-15T14:12:12"
            "message" -> "trying out Elastic Search Scala DSL"
        }

        assert(
            """{"_timstamp":"2009-11-15T14:12:12","_version":4,"user":"sammy","post_date":"2009-11-15T14:12:12","message":"trying out Elastic Search Scala DSL"}""" === req
              ._source
              .string)
    }
}
