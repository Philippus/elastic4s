package com.sksamuel.elastic4s.analyzers

import com.sksamuel.elastic4s.testkit.{ClassloaderLocalNodeProvider, ElasticSugar}
import org.scalatest.{FreeSpec, Matchers}

class NormalizerTest extends FreeSpec with Matchers with ElasticSugar with ClassloaderLocalNodeProvider {

  client.execute {
    createIndex("normalizer").mappings {
      mapping("test") fields (
        keywordField("keywordLowercase") normalizer "lowercaseNorm",
        keywordField("keywordUppercaseMappingChar") normalizer "uppercaseMappingCharNorm"
        )
    } normalizers(
      customNormalizer("lowercaseNorm", LowercaseTokenFilter),
      customNormalizer("uppercaseMappingCharNorm", UppercaseTokenFilter, PatternReplaceCharFilter("xtoy", "x", "y"))
    )
  }.await

  client.execute {
    indexInto("normalizer" / "test").fields(
      "keywordLowercase" -> "VeryMuchMixedCASe",
      "keywordUppercaseMappingChar" -> "Replace xs with ys"
      )
  }.await

  refresh("normalizer")
  blockUntilCount(1, "normalizer")

  "custom Normalizer" - {
    "should apply a lowercase filter " in {
      client.execute {
        // normalizers are applied on search as well
        search("normalizer" / "test") query termQuery("keywordLowercase" -> "VERYMUCHMIXEDCASE")
      }.await.totalHits shouldBe 1
      client.execute {
        search("normalizer" / "test") query termQuery("keywordLowercase" -> "verymuchmixedcase")
      }.await.totalHits shouldBe 1
    }

    "should apply a token filter and a character filter" in {
      client.execute {
        search("normalizer" / "test") query termQuery("keywordUppercaseMappingChar" -> "REPLACE XS WITH YS")
      }.await.totalHits shouldBe 0
      client.execute {
        search("normalizer" / "test") query termQuery("keywordUppercaseMappingChar" -> "REPLACE YS WITH YS")
      }.await.totalHits shouldBe 1
    }
  }


}
