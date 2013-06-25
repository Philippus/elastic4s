package com.sksamuel.elastic4s

import org.scalatest.{OneInstancePerTest, FunSuite}
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.FieldType.{GeoPointType, StringType}
import com.sksamuel.elastic4s.Analyzer.{StopAnalyzer, KeywordAnalyzer, WhitespaceAnalyzer}
import com.sksamuel.elastic4s.CreateIndexDsl._

/** @author Stephen Samuel */
class CreateIndexReqTest extends FunSuite with MockitoSugar with OneInstancePerTest {

    test("create index dsl generates request to json spec") {

        create index "users" shards 3 replicas 4 mappings {

            "tweets" source true as {

                id fieldType StringType analyzer KeywordAnalyzer store true and
                  "name" fieldType StringType analyzer WhitespaceAnalyzer and
                  "content" fieldType StringType analyzer StopAnalyzer

            } and "users" source false as {

                "name" fieldType StringType analyzer WhitespaceAnalyzer and
                  "location" fieldType GeoPointType
            }
        }
    }
}
