package com.sksamuel.elastic4s

import org.scalatest.{FlatSpec, OneInstancePerTest}
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.FieldType._
import com.fasterxml.jackson.databind.ObjectMapper
import ElasticDsl._
import com.sksamuel.elastic4s.TokenFilter._
import com.sksamuel.elastic4s.Tokenizer.KeywordTokenizer
import com.sksamuel.elastic4s.Tokenizer.StandardTokenizer

/** @author Stephen Samuel */
class CreateIndexDslTest extends FlatSpec with MockitoSugar with OneInstancePerTest {

  val mapper = new ObjectMapper()

  "the index dsl" should "generate json to include mapping properties" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/createindex_mappings.json"))
    val req = create.index("users").shards(2).mappings(
      "tweets" as(
        id typed StringType analyzer KeywordAnalyzer store true includeInAll true,
        "name" typed GeoPointType analyzer SimpleAnalyzer boost 4 index "not_analyzed",
        "content" typed DateType analyzer StopAnalyzer nullValue "no content"
        ) size true numericDetection true boostNullValue 1.2 boost "myboost" meta Map("class" -> "com.sksamuel.User"),
      map("users").as(
        "name" typed IpType analyzer WhitespaceAnalyzer omitNorms true,
        "location" typed IntegerType analyzer SnowballAnalyzer ignoreAbove 50,
        "email" typed BinaryType analyzer StandardAnalyzer,
        "picture" typed AttachmentType analyzer NotAnalyzed,
        "age" typed FloatType,
        "area" typed GeoShapeType
      ) analyzer "somefield" dateDetection true dynamicDateFormats("mm/yyyy", "dd-MM-yyyy")
    )
    assert(json === mapper.readTree(req._source.string))
  }

  it should "support custom analyzers" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/createindex_analyis.json"))
    val req = create.index("users").analysis(
      StandardAnalyzerDefinition("standard", stopwords = Seq("stop1", "stop2")),
      StandardAnalyzerDefinition("myAnalyzer1", stopwords = Seq("the", "and"), maxTokenLength = 400),
      CustomAnalyzerDefinition(
        "myAnalyzer2",
        "standard",
        Seq("standard", "lowercase", "myTokenFilter4"))
    )
    assert(json === mapper.readTree(req._source.string))
  }

  it should "support custom analyzers, tokenizers and filters" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/createindex_analyis2.json"))
    val req = create.index("users").analysis(
      analyzers(
        StandardAnalyzerDefinition("standard", stopwords = Seq("stop1", "stop2")),
        StandardAnalyzerDefinition("myAnalyzer1", stopwords = Seq("the", "and"), maxTokenLength = 400),
        CustomAnalyzerDefinition(
          "myAnalyzer2",
          "standard",
          Seq("standard", "lowercase", "myTokenFilter4"))
      ),
      tokenizers(
        StandardTokenizer("myTokenizer1", 900),
        KeywordTokenizer("myTokenizer2", 256)
      ),
      filters(
        StopTokenFilter("myTokenFilter1", enablePositionIncrements = true, ignoreCase = true),
        LengthTokenFilter("myTokenFilter2", 0, max = 10),
        UniqueTokenFilter("myTokenFilter3", onlyOnSamePosition = true),
        ReverseTokenFilter("myTokenFilter4"),
        LimitTokenFilter("myTokenFilter5", 5, consumeAllTokens = false)
      )
    )
    assert(json === mapper.readTree(req._source.string))
  }

  it should "supported nested fields" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/mapping_nested.json"))
    val req = create.index("users").shards(2).mappings(
      "tweets" as(
        id typed StringType analyzer KeywordAnalyzer,
        "name" typed StringType analyzer KeywordAnalyzer,
        "locations" typed GeoPointType analyzer SimpleAnalyzer boost 4,
        "date" typed DateType,
        "content" typed StringType,
        "user" nested(
          "name" typed StringType,
          "email" typed StringType,
          "last" nested {
            "lastLogin" typed DateType
          }
          )
        ) size true numericDetection true boostNullValue 1.2 boost "myboost"
    )
    assert(json === mapper.readTree(req._source.string))
  }

  it should "generate json to override index settings when set" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/createindex_settings.json"))
    val req = create index "users" shards 3 replicas 4
    assert(json === mapper.readTree(req._source.string))
  }
}
