package com.sksamuel.elastic4s

import org.scalatest.{FlatSpec, OneInstancePerTest}
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.FieldType.{GeoPointType, StringType}
import com.sksamuel.elastic4s.Analyzer.{StopAnalyzer, KeywordAnalyzer, WhitespaceAnalyzer}
import com.sksamuel.elastic4s.CreateIndexDsl._
import com.fasterxml.jackson.databind.ObjectMapper

/** @author Stephen Samuel */
class CreateIndexReqTest extends FlatSpec with MockitoSugar with OneInstancePerTest {

    val mapper = new ObjectMapper()

    "the index dsl" should "generate json to include mapping properties" in {

        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/createindex_mappings.json"))
        val req = create index "users" mappings {

            "tweets" source true as {

                id fieldType StringType analyzer KeywordAnalyzer store true and
                  "name" fieldType StringType analyzer WhitespaceAnalyzer and
                  "content" fieldType StringType analyzer StopAnalyzer

            } and "users" source false as {

                "name" fieldType StringType analyzer WhitespaceAnalyzer and
                  "location" fieldType GeoPointType
            }
        }

        println(req._source.string)
        assert(json === mapper.readTree(req._source.string))
    }

    "the index dsl" should "generate json to override index settings when set" in {

        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/createindex_settings.json"))
        val req = create index "users" shards 3 replicas 4

        assert(json === mapper.readTree(req._source.string))
    }
}
