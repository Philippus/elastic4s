package com.sksamuel.elastic4s

import org.scalatest.{FlatSpec, OneInstancePerTest}
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.FieldType._
import com.sksamuel.elastic4s.Analyzer._
import com.fasterxml.jackson.databind.ObjectMapper
import ElasticDsl._

/** @author Stephen Samuel */
class CreateIndexDslTest extends FlatSpec with MockitoSugar with OneInstancePerTest {

    val mapper = new ObjectMapper()

    "the index dsl" should "generate json to include mapping properties" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/createindex_mappings.json"))
        val req = create.index("users").shards(2).mappings(
            "tweets" as (
                id typed StringType analyzer KeywordAnalyzer store true includeInAll true,
                "name" typed GeoPointType analyzer SimpleAnalyzer boost 4 index "not_analyzed",
                "content" typed DateType analyzer StopAnalyzer nullValue "no content"
            ) size true,
            map("users").as(
                "name" fieldType IpType analyzer WhitespaceAnalyzer omitNorms true,
                "location" fieldType IntegerType analyzer SnowballAnalyzer ignoreAbove 50
            ) analyzer "somefield" meta Map("class" -> "com.sksamuel.User")
        )
        assert(json === mapper.readTree(req._source.string))
    }

    "the index dsl" should "generate json to override index settings when set" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/createindex_settings.json"))
        val req = create index "users" shards 3 replicas 4
        assert(json === mapper.readTree(req._source.string))
    }
}
