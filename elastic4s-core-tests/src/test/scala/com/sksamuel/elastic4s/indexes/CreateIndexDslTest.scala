package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.JsonSugar
import com.sksamuel.elastic4s.analyzers._
import com.sksamuel.elastic4s.mappings.FieldType._
import com.sksamuel.elastic4s.mappings.{DynamicMapping, PrefixTree}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers, OneInstancePerTest}

import scala.concurrent.duration._
import scala.io.Source

class CreateIndexDslTest extends FlatSpec with MockitoSugar with JsonSugar with Matchers with OneInstancePerTest {

  "the index dsl" should "generate json to include mapping properties" in {
    val req = createIndex("users").mappings(
      mapping("tweets") as(
        id typed StringType analyzer KeywordAnalyzer store true includeInAll true,
        field("name", GeoPointType) latLon true geohash true,
        field("content", DateType) nullValue "no content"
      ) all false size true numericDetection true boostNullValue 1.2 boost "myboost" meta Map("class" -> "com.sksamuel.User"),
      mapping("users").as(
        field("name", IpType) nullValue "127.0.0.1" boost 1.0,
        field("location", IntegerType) nullValue 0,
        field("email", BinaryType),
        field("picture", AttachmentType),
        field("age", FloatType) indexName "indexName",
        field("area", GeoShapeType) tree PrefixTree.Quadtree precision "1m"
      ) all true analyzer "somefield" dateDetection true dynamicDateFormats("mm/yyyy", "dd-MM-yyyy")
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

  it should "support refresh interval" in {
    create index "test" refreshInterval 4.seconds
  }

  it should "support the stopwords_path filter" in {
    val req = create.index("users").analysis(
      PatternAnalyzerDefinition("patternAnalyzer", regex = "[a-z]"),
      SnowballAnalyzerDefinition("mysnowball", lang = "english", stopwords = Seq("stop1", "stop2", "stop3")),
      CustomAnalyzerDefinition(
        "myAnalyzer2",
        StandardTokenizer("myTokenizer1", 900),
        LengthTokenFilter("myTokenFilter2", 0, max = 10),
        UniqueTokenFilter("myTokenFilter3", onlyOnSamePosition = true),
        stemmerTokenFilter("myFrenchStemmerTokenFilter").lang("french"),
        PatternReplaceTokenFilter("prTokenFilter", "pattern", "rep"),
        WordDelimiterTokenFilter("myWordDelimiterTokenFilter")
          .generateWordParts(true)
          .generateNumberParts(true)
          .catenateAll(true)
          .catenateNumbers(false)
          .catenateAll(false)
          .splitOnCaseChange(true)
          .preserveOriginal(false)
          .splitOnNumerics(true)
          .stemEnglishPossesive(true)
      ),
      CustomAnalyzerDefinition(
        "myAnalyzer3",
        LowercaseTokenizer,
        StopTokenFilterPath("myTokenFilter0", "stoplist.txt", enablePositionIncrements = true, ignoreCase = true),
        stopTokenFilter("myTokenFilter1").enablePositionIncrements(true).ignoreCase(true),
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
    req._source.string should matchJsonResource("/json/createindex/createindex_stop_path.json")
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
        WordDelimiterTokenFilter("myWordDelimiterTokenFilter")
          .generateWordParts(true)
          .generateNumberParts(true)
          .catenateAll(true)
      ),
      CustomAnalyzerDefinition(
        "myAnalyzer3",
        LowercaseTokenizer,
        StopTokenFilter("myTokenFilter1").enablePositionIncrements(true).ignoreCase(true),
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

  it should "supported nested fields" ignore {
    //    val req = create.index("users").mappings(
    //      mapping("tweets") as(
    //        id typed StringType analyzer KeywordAnalyzer,
    //        stringField("name") analyzer KeywordAnalyzer,
    //        field("locations") typed GeoPointType validate true normalize true,
    //        dateField("date") precisionStep 5,
    //        longField("size"),
    //        booleanField("read"),
    //        stringField("content"),
    //        "user" nested(
    //          "name" typed StringType,
    //          "email" typed StringType,
    //          "last" nested {
    //            "lastLogin" typed DateType
    //          }
    //          ) includeInRoot(true) includeInParent(true)
    //        ) size true numericDetection true boostNullValue 1.2 boost "myboost"
    //    )
    //    req._source.string should matchJsonResource("/json/createindex/mapping_nested.json")
  }

  it should "generate json to set index settings" in {
    val req = (create index "users"
      shards 3
      replicas 4
      refreshInterval "5s"
      indexSetting("compound_on_flush", false)
      indexSetting("compound_format", 0.5))
    req._source.string should matchJsonResource("/json/createindex/createindex_settings2.json")
  }

  it should "support inner objects" ignore {
    //    val req = create.index("tweets").mappings(
    //      "tweet" as(
    //        "person" inner(
    //          "name" inner(
    //            "first_name" typed StringType analyzer KeywordAnalyzer,
    //            "last_name" typed StringType analyzer KeywordAnalyzer,
    //            "byte" typed ByteType,
    //            "short" typed ShortType
    //            ),
    //          "sid" typed StringType index "not_analyzed"
    //          ),
    //        "message" typed StringType
    //        ) size true numericDetection true boostNullValue 1.2 boost "myboost"
    //    )
    //    req._source.string should matchJsonResource("/json/createindex/mapping_inner_object.json")
  }

  it should "support disabled inner objects" ignore {
    //    val req = create.index("tweets").mappings(
    //      "tweet" as(
    //        "person" inner(
    //          "name" typed ObjectType enabled false,
    //          field("sid", StringType) index "not_analyzed"
    //          ),
    //        field("message", StringType)
    //        ) size true numericDetection true boostNullValue 1.2 boost "myboost"
    //    )
    //    req._source.string should matchJsonResource("/json/createindex/mapping_inner_object_disabled.json")
  }

  it should "support multi field type" in {
    val req = create.index("tweets").mappings(
      mapping("tweet") as (
        field("name") multi(
          field("name") typed StringType index "analyzed",
          field("untouched") typed StringType index "not_analyzed"
        )
        ) size true numericDetection true boostNullValue 1.2 boost "myboost" dynamic DynamicMapping.False
    )
    req._source.string should matchJsonResource("/json/createindex/mapping_multi_field_type_1.json")
  }

  it should "support multi field type with path" in {
    val req = create.index("tweets").mappings(
      mapping("tweet") as(
        field("first_name") typed ObjectType as(
          field("first_name") typed TokenCountType index "analyzed",
          field("any_name") typed StringType index "analyzed"
        ),
          field("last_name") typed MultiFieldType path "just_name" as(
          field("last_name") typed StringType index "analyzed",
          field("any_name") typed StringType index "analyzed"
        )
      ) size true numericDetection true boostNullValue 1.2 boost "myboost"
    )
    req._source.string should matchJsonResource("/json/createindex/mapping_multi_field_type_2.json")
  }

  it should "support copy to a single field" in {
    val req = createIndex("tweets").mappings(
      mapping("tweet") as(
        field("first_name") typed TextType index "analyzed" copyTo "full_name",
        field("last_name") typed TextType index "analyzed" copyTo "full_name",
        field("full_name") typed TextType index "analyzed"
      ) size true numericDetection true boostNullValue 1.2 boost "myboost" dynamic DynamicMapping.Dynamic
    )
    req._source.string should matchJsonResource("/json/createindex/mapping_copy_to_single_field.json")
  }

  it should "support copy to multiple fields" in {
    val req = createIndex("tweets").mappings(
      mapping("tweet") as(
        field("title") typed TextType index "analyzed" copyTo("meta_data", "article_info"),
        field("meta_data") typed TextType index "analyzed",
        field("article_info") typed TextType index "analyzed"
      ) size true numericDetection true boostNullValue 1.2 boost "myboost" dynamic DynamicMapping.Strict
    )
    req._source.string should matchJsonResource("/json/createindex/mapping_copy_to_multiple_fields.json")
  }

  it should "support multi fields" in {
    val req = createIndex("tweets").mappings(
      mapping("tweet") as(
        field("title") typed TextType index "analyzed" fields (
          field("raw") typed TextType index "not_analyzed"),
        field("meta_data") typed TextType index "analyzed",
        field("article_info") typed TextType index "analyzed"
      ) size true numericDetection true boostNullValue 1.2 boost "myboost"
    )
    req._source.string should matchJsonResource("/json/createindex/mapping_multi_fields.json")
  }

  it should "support completion type" in {
    val req = createIndex("tweets").mappings(
      mapping("tweet") as(
        field("name") typed StringType index "analyzed",
        field("ac") typed CompletionType analyzer "simple" searchAnalyzer "simple"
          payloads true preserveSeparators false preservePositionIncrements false maxInputLen 10
      ) size true numericDetection true boostNullValue 1.2 boost "myboost"
    )
    req._source.string should matchJsonResource("/json/createindex/mapping_completion_type.json")
  }

  it should "support creating parent mappings" in {
    val req = createIndex("docsAndTags").mappings(
      mapping("tags") as (
        field("tag") typed StringType
        ) parent "docs" source true all false dynamic DynamicMapping.Strict
    )
    req._source.string should matchJsonResource("/json/createindex/create_parent_mappings.json")
  }

  it should "generate json to enable timestamp" in {
    val req = createIndex("tweets").mappings(
      mapping("tweet") as(
        id typed StringType analyzer KeywordAnalyzer store true includeInAll true,
        field("name") typed GeoPointType latLon true geohash true,
        field("content") typed DateType nullValue "no content"
      ) all true size true numericDetection true boostNullValue 1.2 boost "myboost" timestamp true
    )
    req._source.string should matchJsonResource("/json/createindex/createindex_timestamp_1.json")
  }

  it should "generate json to enable timestamp with path and format" in {
    val req = createIndex("tweets").mappings(
      mapping("tweet") as(
        id typed StringType analyzer KeywordAnalyzer store true includeInAll true,
        field("name") typed GeoPointType latLon true geohash true,
        field("content") typed DateType nullValue "no content"
      ) source false size true numericDetection true boostNullValue 1.2 boost "myboost" timestamp(true, path = Some(
        "post_date"), format = Some("YYYY-MM-dd"))
    )
    req._source.string should matchJsonResource("/json/createindex/createindex_timestamp_2.json")
  }

  it should "generate json to enable timestamp with path and format and default null" in {
    val req = createIndex("tweets").mappings(
      mapping("tweet") as(
        id typed StringType analyzer KeywordAnalyzer store true includeInAll true,
        field("name") typed GeoPointType latLon true geohash true,
        field("content") typed DateType nullValue "no content"
      ) size true numericDetection true boostNullValue 1.2 boost "myboost" timestamp(true, default = Some(null))
    )
    req._source.string should matchJsonResource("/json/createindex/createindex_timestamp_3.json")
  }

  it should "accept pre-built mapping JSON" in {
    val source = Source
      .fromInputStream(getClass.getResourceAsStream("/json/createindex/createindex_mappings.json"))
      .mkString

    val req = create.index("tweets").source(source).build

    req.mappings().size() should equal(2)
    req.mappings().containsKey("tweets") === true
    req.mappings().containsKey("users") === true
  }
}
