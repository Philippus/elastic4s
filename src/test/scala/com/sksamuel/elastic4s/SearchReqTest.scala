package com.sksamuel.elastic4s

import org.scalatest.{OneInstancePerTest, FunSuite}
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.Analyzer.WhitespaceAnalyzer
import SearchDsl._

/** @author Stephen Samuel */
class SearchReqTest extends FunSuite with MockitoSugar with OneInstancePerTest {

    val client: ScalaClient = null

    test("search dsl generates a request to json spec") {

        "twitter/tweets" query "singer:chris" limit 5

        client search "twitter/tweets" query "bands:coldplay" limit 5

        client search "twitter/tweets" limit 10 start 3 routing "allusers" searchType SearchType.QueryThenFetch query {

            string query "I love searching" boost 2.5 operator "and" anaylyzer WhitespaceAnalyzer
            prefix query "users:sam" boost 4.5
            term query "location" -> "london" boost 4
            matches query "job" -> "developer" operator "OR"
            range query "age" from 12 to 4
            regex query "name" -> "sam.*"

        } highlighting {

            highlight field "user" fragmentSize 150 numberOfFragments 3
            highlight field "location" size 150 number 3

        }
    }
}
