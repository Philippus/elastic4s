package com.sksamuel.elastic4s

import org.scalatest.{ Matchers, Assertions, FlatSpec, OneInstancePerTest }
import org.scalatest.mock.MockitoSugar
import com.sksamuel.elastic4s.mappings.FieldType._
import ElasticDsl._

/** @author Stephen Samuel */
class CreateIndexDslTest extends FlatSpec with MockitoSugar with JsonSugar with Matchers with OneInstancePerTest {
  "the index dsl" should "generate json to include mapping properties" in {
    val req = create.index("users").mappings(
      "tweets" as (
        id typed StringType analyzer KeywordAnalyzer store true includeInAll true,
        "name" typed GeoPointType latLon true geohash true,
        "content" typed DateType nullValue "no content"
      ) all true size true numericDetection true boostNullValue 1.2 boost "myboost" meta Map("class" -> "com.sksamuel.User"),
      map("users").as(
        "name" typed IpType nullValue "127.0.0.1" boost 1.0,
        "location" typed IntegerType nullValue 0,
        "email" typed BinaryType,
        "picture" typed AttachmentType,
        "age" typed FloatType,
        "area" typed GeoShapeType
      ) all false analyzer "somefield" dateDetection true dynamicDateFormats ("mm/yyyy", "dd-MM-yyyy")
    )
    req._source.string should matchJsonResource("/json/createindex/createindex_mappings.json")
  }

  it should "support override built in analyzers" in {
    val req = create.index("users").analysis(
      StandardAnalyzerDefinition("standard", stopwords = Seq("stop1", "stop2")),
      StandardAnalyzerDefinition("myAnalyzer1", stopwords = Seq("the", "and"), maxTokenLength = 400)
    )
    req._source.string should matchJsonResource("/json/createindex/createindex_analyis.json")
  }

  it should "support custom analyzers, tokenizers and filters" in {
    val req = create.index("users").analysis(
      PatternAnalyzerDefinition("patternAnalyzer", regex = "[a-z]"),
      SnowballAnalyzerDefinition("mysnowball", lang = "english", stopwords = Seq("stop1", "stop2", "stop3")),
      CustomAnalyzerDefinition(
        "myAnalyzer2",
        StandardTokenizer("myTokenizer1", 900),
        LengthTokenFilter("myTokenFilter2", 0, max = 10),
        UniqueTokenFilter("myTokenFilter3", onlyOnSamePosition = true),
        StemmerTokenFilter("myFrenchStemmerTokenFilter", lang = "french"),
        PatternReplaceTokenFilter("prTokenFilter", "pattern", "rep"),
        WordDelimiterTokenFilter(
          "myWordDelimiterTokenFilter",
          generateWordParts = true,
          generateNumberParts = true,
          catenateWords = false,
          catenateNumbers = false,
          catenateAll = false,
          splitOnCaseChange = true,
          preserveOriginal = false,
          splitOnNumerics = true,
          stemEnglishPossesive = true
        )
      ),
      CustomAnalyzerDefinition(
        "myAnalyzer3",
        LowercaseTokenizer,
        StopTokenFilter("myTokenFilter1", enablePositionIncrements = true, ignoreCase = true),
        ReverseTokenFilter,
        LimitTokenFilter("myTokenFilter5", 5, consumeAllTokens = false),
        EdgeNGramTokenFilter("myEdgeNGramTokenFilter", minGram = 3, maxGram = 50),
        StemmerOverrideTokenFilter("stemmerTokenFilter", Array("rule1", "rule2")),
        HtmlStripCharFilter,
        MappingCharFilter("mapping_charfilter", "ph" -> "f", "qu" -> "q"),
        PatternReplaceCharFilter(
          "pattern_replace_charfilter",
          pattern = "sample(.*)",
          replacement = "replacedSample $1"
        )
      ),
      CustomAnalyzerDefinition(
        "myAnalyzer4",
        EdgeNGramTokenizer("myTokenizer4", minGram = 3, maxGram = 17, tokenChars = Seq("digit", "letter"))
      ),
      CustomAnalyzerDefinition(
        "myAnalyzer5",
        NGramTokenizer("myTokenizer5", minGram = 4, maxGram = 18, tokenChars = Seq("letter", "punctuation")))
    )
    req._source.string should matchJsonResource("/json/createindex/createindex_analyis2.json")
  }

  it should "supported nested fields" in {
    val req = create.index("users").mappings(
      "tweets" as (
        id typed StringType analyzer KeywordAnalyzer,
        "name" typed StringType analyzer KeywordAnalyzer,
        "locations" typed GeoPointType validate true normalize true,
        "date" typed DateType precisionStep 5,
        "size" typed LongType,
        "read" typed BooleanType,
        "content" typed StringType,
        "user" nested (
          "name" typed StringType,
          "email" typed StringType,
          "last" nested {
            "lastLogin" typed DateType
          }
        )
      ) size true numericDetection true boostNullValue 1.2 boost "myboost"
    )
    req._source.string should matchJsonResource("/json/createindex/mapping_nested.json")
  }

  it should "generate json to set index settings" in {
    val req = (create index "users"
      shards 3
      replicas 4
      refreshInterval "5s"
      indexSetting ("compound_on_flush", false)
      indexSetting ("compound_format", 0.5))
    req._source.string should matchJsonResource("/json/createindex/createindex_settings2.json")
  }

  it should "support inner objects" in {
    val req = create.index("tweets").mappings(
      "tweet" as (
        "person" inner (
          "name" inner (
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
    req._source.string should matchJsonResource("/json/createindex/mapping_inner_object.json")
  }

  it should "support disabled inner objects" in {
    val req = create.index("tweets").mappings(
      "tweet" as (
        "person" inner (
          "name" typed ObjectType enabled false,
          "sid" typed StringType index "not_analyzed"
        ),
          "message" typed StringType
      ) size true numericDetection true boostNullValue 1.2 boost "myboost"
    )
    req._source.string should matchJsonResource("/json/createindex/mapping_inner_object_disabled.json")
  }

  it should "support multi field type" in {
    val req = create.index("tweets").mappings(
      "tweet" as (
        "name" multi (
          "name" typed StringType index "analyzed",
          "untouched" typed StringType index "not_analyzed"
        )
      ) size true numericDetection true boostNullValue 1.2 boost "myboost"
    )
    req._source.string should matchJsonResource("/json/createindex/mapping_multi_field_type_1.json")
  }

  it should "support multi field type with path" in {
    val req = create.index("tweets").mappings(
      "tweet" as (
        "first_name" typed ObjectType as (
          "first_name" typed TokenCountType index "analyzed",
          "any_name" typed StringType index "analyzed"
        ),
          "last_name" typed MultiFieldType path "just_name" as (
            "last_name" typed StringType index "analyzed",
            "any_name" typed StringType index "analyzed"
          )
      ) size true numericDetection true boostNullValue 1.2 boost "myboost"
    )
    req._source.string should matchJsonResource("/json/createindex/mapping_multi_field_type_2.json")
  }

  it should "support copy to a single field" in {
    val req = create.index("tweets").mappings(
      "tweet" as (
        "first_name" typed StringType index "analyzed" copyTo "full_name",
        "last_name" typed StringType index "analyzed" copyTo "full_name",
        "full_name" typed StringType index "analyzed"
      ) size true numericDetection true boostNullValue 1.2 boost "myboost"
    )
    req._source.string should matchJsonResource("/json/createindex/mapping_copy_to_single_field.json")
  }

  it should "support copy to multiple fields" in {
    val req = create.index("tweets").mappings(
      "tweet" as (
        "title" typed StringType index "analyzed" copyTo ("meta_data", "article_info"),
        "meta_data" typed StringType index "analyzed",
        "article_info" typed StringType index "analyzed"
      ) size true numericDetection true boostNullValue 1.2 boost "myboost"
    )
    req._source.string should matchJsonResource("/json/createindex/mapping_copy_to_multiple_fields.json")
  }

  it should "support completion type" in {
    val req = create.index("tweets").mappings(
      "tweet" as (
        "name" typed StringType index "analyzed",
        "ac" typed CompletionType indexAnalyzer "simple" searchAnalyzer "simple"
        payloads true preserveSeparators false preservePositionIncrements false maxInputLen 10
      ) size true numericDetection true boostNullValue 1.2 boost "myboost"
    )
    req._source.string should matchJsonResource("/json/createindex/mapping_completion_type.json")
  }

  it should "support creating parent mappings" in {
    val req = create.index("docsAndTags").mappings(
      "tags" as (
        "tag" typed StringType
      ) all false parent "docs"
    )
    req._source.string should matchJsonResource("/json/createindex/create_parent_mappings.json")
  }

  it should "generate json to enable timestamp" in {
    val req = create.index("tweets").mappings(
      "tweet" as (
        id typed StringType analyzer KeywordAnalyzer store true includeInAll true,
        "name" typed GeoPointType latLon true geohash true,
        "content" typed DateType nullValue "no content"
      ) all true size true numericDetection true boostNullValue 1.2 boost "myboost" timestamp true
    )
    req._source.string should matchJsonResource("/json/createindex/createindex_timestamp_1.json")
  }

  it should "generate json to enable timestamp with path and format" in {
    val req = create.index("tweets").mappings(
      "tweet" as (
        id typed StringType analyzer KeywordAnalyzer store true includeInAll true,
        "name" typed GeoPointType latLon true geohash true,
        "content" typed DateType nullValue "no content"
      ) all true size true numericDetection true boostNullValue 1.2 boost "myboost" timestamp (true, path = Some("post_date"), format = Some("YYYY-MM-dd"))
    )
    req._source.string should matchJsonResource("/json/createindex/createindex_timestamp_2.json")
  }

  it should "generate json to enable timestamp with path and format and default null" in {
    val req = create.index("tweets").mappings(
      "tweet" as (
        id typed StringType analyzer KeywordAnalyzer store true includeInAll true,
        "name" typed GeoPointType latLon true geohash true,
        "content" typed DateType nullValue "no content"
      ) all true size true numericDetection true boostNullValue 1.2 boost "myboost" timestamp (true, default = Some(null))
    )
    req._source.string should matchJsonResource("/json/createindex/createindex_timestamp_3.json")
  }

}
