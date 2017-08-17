//package com.sksamuel.elastic4s.analyzers
//
//import com.sksamuel.elastic4s.testkit.{DiscoveryLocalNodeProvider, ElasticSugar}
//import org.scalatest.{FreeSpec, Matchers}
//
//class NormalizerTest extends FreeSpec with Matchers with ElasticSugar with DiscoveryLocalNodeProvider {
//
//  client.execute {
//    createIndex("normalizer").mappings {
//      mapping("test") fields (
//        keywordField("keywordLowercase") normalizer "lowercaseNorm",
//        keywordField("keywordUppercase") normalizer "uppercaseNorm"
//        )
//    } normalizers(
//      customNormalizer("lowercaseNorm", LowercaseTokenFilter),
//      customNormalizer("uppercaseNorm", UppercaseTokenFilter)
//    )
//  }.await
//
//  client.execute {
//    indexInto("normalizer" / "test").fields(
//      "keywordLowercase" -> "VeryMuchMixedCASe",
//      "keywordUppercase" -> "I want to be UPPER"
//      )
//  }.await
//
//  refresh("normalizer")
//  blockUntilCount(1, "normalizer")
//
//  "custom Normalizer" - {
//    "should apply a lowercase filter " ignore {
//      client.execute {
//        // normalizers are applied on search as well
//        search("normalizer" / "test") query termQuery("keywordLowercase" -> "VERYMUCHMIXEDCASE")
//      }.await.totalHits shouldBe 1
//      client.execute {
//        search("normalizer" / "test") query termQuery("keywordLowercase" -> "verymuchmixedcase")
//      }.await.totalHits shouldBe 1
//    }
//
//    // todo add a test back in once (if?) elasticsearch 6.0 supports char filters again
//    "should apply a token filter and a character filter" ignore {
//      client.execute {
//        search("normalizer" / "test") query termQuery("keywordUppercase" -> "REPLACE XS WITH YS")
//      }.await.totalHits shouldBe 0
//      client.execute {
//        search("normalizer" / "test") query termQuery("keywordUppercase" -> "REPLACE YS WITH YS")
//      }.await.totalHits shouldBe 1
//    }
//  }
//
//
//}
