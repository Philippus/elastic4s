package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.JsonSugar
import com.sksamuel.elastic4s.http.index.CreateIndexTemplateBodyFn
import com.sksamuel.elastic4s.mappings.PrefixTree
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers, OneInstancePerTest}

class CreateIndexTemplateApiTest extends FlatSpec with MockitoSugar with JsonSugar with Matchers with OneInstancePerTest {


  "the index template dsl" should "allow provide mapping properties using rawSource" in {
    val tweetRawSource = """{"_all": {"enabled": false},"numeric_detection": true,"_boost": {"name": "myboost","null_value": 1.2},"_size": {"enabled": true},"properties": {"name": {"type": "geo_point"},"content": {"type": "date","null_value": "no content"}},"_meta": {"class": "com.sksamuel.User"}}"""
    val req = createIndexTemplate("usersTemplate", "users").mappings(
      mapping("tweets").rawSource(tweetRawSource),
      mapping("users").as(
        ipField("name") nullValue "127.0.0.1" boost 1.0,
        intField("location") nullValue 0,
        binaryField("email"),
        floatField("age"),
        geoshapeField("area") tree PrefixTree.Quadtree precision "1m"
      ) all true analyzer "somefield" dateDetection true dynamicDateFormats("mm/yyyy", "dd-MM-yyyy")
    )

    CreateIndexTemplateBodyFn(req).string() should matchJsonResource("/json/createindextemplate/createindextemplate_body.json")
  }

}
