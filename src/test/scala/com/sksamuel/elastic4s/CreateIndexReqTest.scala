package com.sksamuel.elastic4s

import org.scalatest.{OneInstancePerTest, FunSuite}
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.FieldType.StringType
import com.sksamuel.elastic4s.Analyzer.{WhitespaceAnalyzer, KeywordAnalyzer}

/** @author Stephen Samuel */
class CreateIndexReqTest extends FunSuite with MockitoSugar with OneInstancePerTest {

    test("create index json is generated to spec") {

        val req = CreateIndexReq("users")
          .shards(3)
          .replicas(4)
          .mapping("users")
          .id
          .fieldType(StringType)
          .analyzer(KeywordAnalyzer)
          .store
          .field("name")
          .fieldType(StringType)
          .analyzer(WhitespaceAnalyzer)
          .mapping("tweets")
          .source(false)
          .id
          .fieldType(StringType)
          .analyzer(KeywordAnalyzer)
          .store
          .field("name")
          .fieldType(StringType)
          .analyzer(WhitespaceAnalyzer)
          .build

        assert(
            """{"mappings":{"tweets":{"_source":{"enabled":false},"properties":{"_id":{"type":"StringType","index":"KeywordAnalyzer","store":"true"},"name":{"type":"StringType","index":"WhitespaceAnalyzer","store":"false"}}}}}""" === req
              ._source
              .string)
    }
}
