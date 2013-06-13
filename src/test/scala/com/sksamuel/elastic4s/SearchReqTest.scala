package com.sksamuel.elastic4s

import org.scalatest.{OneInstancePerTest, FunSuite}
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.Analyzer.WhitespaceAnalyzer

/** @author Stephen Samuel */
class SearchReqTest extends FunSuite with MockitoSugar with OneInstancePerTest with SearchDsl {

    test("search dsl generates a request to json spec") {

        val req = search("twitter", "tweets") {

            routing("allusers")
            searchType(SearchType.QueryThenFetch)

            bool {
                query("I love searching").boost(2.5).operator("AND").analyzer(WhitespaceAnalyzer)
                prefix("users", "sam").boost(4.0)
                term("location", "London").boost(1.0)
                matches("job", "developer").operator("OR")
            }

            facets {

            }

            highlight {
                preTags("<strong>")
                postTags("</strong>")
                highlightField("users").fragmentSize(150).numberOfFragments(3)
                highlightField("location").fragmentSize(150).numberOfFragments(3)
            }
        }

        assert(
            """""" === req
              ._source
              .string)
    }
}
