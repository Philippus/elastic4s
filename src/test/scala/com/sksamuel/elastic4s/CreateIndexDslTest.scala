package com.sksamuel.elastic4s

import org.scalatest.{FlatSpec, OneInstancePerTest}
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.mapping.FieldType._
import com.fasterxml.jackson.databind.ObjectMapper
import ElasticDsl._

/** @author Stephen Samuel */
class CreateIndexDslTest extends FlatSpec with MockitoSugar with OneInstancePerTest {

  val mapper = new ObjectMapper()

  "the index dsl" should "generate json to include mapping properties" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/createindex_mappings.json"))
    val req = create.index("users").shards(2).mappings(
      "tweets" as(
        id typed StringType analyzer KeywordAnalyzer store true includeInAll true,
        "name" typed GeoPointType latLon true geohash true,
        "content" typed DateType nullValue "no content"
        ) size true numericDetection true boostNullValue 1.2 boost "myboost" meta Map("class" -> "com.sksamuel.User"),
      map("users").as(
        "name" typed IpType nullValue "127.0.0.1" boost 1.0,
        "location" typed IntegerType nullValue 0,
        "email" typed BinaryType,
        "picture" typed AttachmentType,
        "age" typed FloatType,
        "area" typed GeoShapeType
      ) analyzer "somefield" dateDetection true dynamicDateFormats("mm/yyyy", "dd-MM-yyyy")
    )
    assert(json === mapper.readTree(req._source.string))
  }

  it should "support override built in analyzers" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/createindex_analyis.json"))
    val req = create.index("users").analysis(
      StandardAnalyzerDefinition("standard", stopwords = Seq("stop1", "stop2")),
      StandardAnalyzerDefinition("myAnalyzer1", stopwords = Seq("the", "and"), maxTokenLength = 400)
    )
    assert(json === mapper.readTree(req._source.string))
  }

  it should "support custom analyzers, tokenizers and filters" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/createindex_analyis2.json"))
    val req = create.index("users").analysis(
      PatternAnalyzerDefinition("patternAnalyzer", regex = "[a-z]"),
      SnowballAnalyzerDefinition("mysnowball", lang = "english", stopwords = Seq("stop1", "stop2", "stop3")),
      CustomAnalyzerDefinition(
        "myAnalyzer2",
        StandardTokenizer("myTokenizer1", 900),
        LengthTokenFilter("myTokenFilter2", 0, max = 10),
        UniqueTokenFilter("myTokenFilter3", onlyOnSamePosition = true),
        PatternReplaceTokenFilter("prTokenFilter", "pattern", "rep")
      ),
      CustomAnalyzerDefinition(
        "myAnalyzer3",
        LowercaseTokenizer,
        StopTokenFilter("myTokenFilter1", enablePositionIncrements = true, ignoreCase = true),
        ReverseTokenFilter,
        LimitTokenFilter("myTokenFilter5", 5, consumeAllTokens = false),
        StemmerOverrideTokenFilter("stemmerTokenFilter", Array("rule1", "rule2"))
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
        "locations" typed GeoPointType validate true normalize true,
        "date" typed DateType precisionStep 5,
        "size" typed LongType,
        "read" typed BooleanType,
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

  it should "support inner objects" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/mapping_inner_object.json"))
    val req = create.index("tweets").shards(2).mappings(
      "tweet" as(
        "person" inner(
          "name" inner(
            "first_name" typed StringType analyzer KeywordAnalyzer,
            "last_name" typed StringType analyzer KeywordAnalyzer,
            "byte" typed ByteType,
            "short" typed ShortType
            ),
          "sid" typed StringType index "not_analyzed"
          ),
        "message" typed StringType
        ) size true numericDetection true boostNullValue 1.2 boost "myboost"
    )
    assert(json === mapper.readTree(req._source.string))
  }

  it should "support disabled inner objects" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/mapping_inner_object_disabled.json"))
    val req = create.index("tweets").shards(2).mappings(
      "tweet" as(
        "person" inner(
          "name" typed ObjectType enabled false,
          "sid" typed StringType index "not_analyzed"
          ),
        "message" typed StringType
        ) size true numericDetection true boostNullValue 1.2 boost "myboost"
    )
    assert(json === mapper.readTree(req._source.string))
  }

  it should "support multi field type" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/mapping/types/multi_field_type_1.json"))
    val req = create.index("tweets").shards(2).mappings(
      "tweet" as (
        "name" multi(
          "name" typed StringType index "analyzed",
          "untouched" typed StringType index "not_analyzed"
          )
        ) size true numericDetection true boostNullValue 1.2 boost "myboost"
    )
    assert(json === mapper.readTree(req._source.string))
  }

  it should "support multi field type with path" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/mapping/types/multi_field_type_2.json"))
    val req = create.index("tweets").shards(2).mappings(
      "tweet" as(
        "first_name" typed ObjectType as(
          "first_name" typed TokenCountType index "analyzed",
          "any_name" typed StringType index "analyzed"
          ),
        "last_name" typed MultiFieldType path "just_name" as(
          "last_name" typed StringType index "analyzed",
          "any_name" typed StringType index "analyzed"
          )
        ) size true numericDetection true boostNullValue 1.2 boost "myboost"
    )
    assert(json === mapper.readTree(req._source.string))
  }

  it should "support copy to a single field" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/mapping/types/copy_to_single_field.json"))
    val req = create.index("tweets").shards(2).mappings(
      "tweet" as(
        "first_name" typed StringType index "analyzed" copyTo "full_name",
        "last_name" typed StringType index "analyzed" copyTo "full_name",
        "full_name" typed StringType index "analyzed"
        ) size true numericDetection true boostNullValue 1.2 boost "myboost"
    )
    assert(json === mapper.readTree(req._source.string))
  }

  it should "support copy to multiple fields" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/mapping/types/copy_to_multiple_fields.json"))
    val req = create.index("tweets").shards(2).mappings(
      "tweet" as(
        "title" typed StringType index "analyzed" copyTo ("meta_data", "article_info"),
        "meta_data" typed StringType index "analyzed",
        "article_info" typed StringType index "analyzed"
        ) size true numericDetection true boostNullValue 1.2 boost "myboost"
    )
    assert(json === mapper.readTree(req._source.string))
  }

  it should "support completion type" in {
    val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/mapping/types/completion_type_1.json"))
    val req = create.index("tweets").shards(2).mappings(
      "tweet" as (
          "name" typed StringType index "analyzed",
          "ac" typed CompletionType indexAnalyzer "simple" searchAnalyzer "simple"
            payloads true preserveSeparators false preservePositionIncrements false maxInputLen 10
        ) size true numericDetection true boostNullValue 1.2 boost "myboost"
    )
    assert(json === mapper.readTree(req._source.string))
  }

}
