package com.sksamuel.elastic4s

import org.scalatest.{OneInstancePerTest, FunSuite}
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.FieldType.StringType
import com.sksamuel.elastic4s.Analyzer.{KeywordAnalyzer, WhitespaceAnalyzer}

/** @author Stephen Samuel */
class CreateIndexReqTest extends FunSuite with MockitoSugar with OneInstancePerTest with CreateIndexDsl {

    test("create index dsl generates request to json spec") {

        val req = createIndex("users") {
            shards(3)
            replicas(4)
            mappings {
                mapping("users") {
                    id.fieldType(StringType).analyzer(KeywordAnalyzer).store
                    field("name").fieldType(StringType).analyzer(WhitespaceAnalyzer)
                }
                mapping("tweets") {
                    source(true)
                }
                mapping("locations") {
                    id.fieldType(StringType).analyzer(KeywordAnalyzer).store
                    field("name").fieldType(StringType).analyzer(WhitespaceAnalyzer)
                }
            }
        }

        assert(
            """{"settings":{"number_of_shards":3,"number_of_replicas":4},"mappings":{"users":{"_source":{"enabled":false},"properties":{"_id":{"type":"StringType","index":"KeywordAnalyzer","store":"true"},"name":{"type":"StringType","index":"WhitespaceAnalyzer","store":"false"}}},"tweets":{"_source":{"enabled":true},"properties":{}},"locations":{"_source":{"enabled":false},"properties":{"_id":{"type":"StringType","index":"KeywordAnalyzer","store":"true"},"name":{"type":"StringType","index":"WhitespaceAnalyzer","store":"false"}}}}}""" === req
              ._source
              .string)
    }
}
