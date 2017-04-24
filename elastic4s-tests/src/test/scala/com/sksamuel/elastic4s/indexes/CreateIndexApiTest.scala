package com.sksamuel.elastic4s.indexes

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.JsonSugar
import com.sksamuel.elastic4s.analyzers._
import com.sksamuel.elastic4s.mappings.PrefixTree
import com.sksamuel.elastic4s.mappings.dynamictemplate.DynamicMapping
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers, OneInstancePerTest}

import scala.concurrent.duration._
import scala.io.Source

class CreateIndexApiTest extends FlatSpec with MockitoSugar with JsonSugar with Matchers with OneInstancePerTest {

  "the index dsl" should "generate json to include mapping properties" in {
    val req = createIndex("users").mappings(
      mapping("tweets") as(
        geopointField("name"),
        dateField("content") nullValue "no content"
      ) all false size true numericDetection true boostNullValue 1.2 boostName "myboost" meta Map("class" -> "com.sksamuel.User"),
      mapping("users").as(
        ipField("name") nullValue "127.0.0.1" boost 1.0,
        intField("location") nullValue 0,
        binaryField("email"),
        floatField("age"),
        geoshapeField("area") tree PrefixTree.Quadtree precision "1m"
      ) all true analyzer "somefield" dateDetection true dynamicDateFormats("mm/yyyy", "dd-MM-yyyy")
    )
    CreateIndexContentBuilder(req).string() should matchJsonResource("/json/createindex/createindex_mappings.json")
  }

  it should "support override built in analyzers" in {
    val req = createIndex("users").analysis(
      StandardAnalyzerDefinition("standard", stopwords = Seq("stop1", "stop2")),
      StandardAnalyzerDefinition("myAnalyzer1", stopwords = Seq("the", "and"), maxTokenLength = 400)
    )
    CreateIndexContentBuilder(req).string() should matchJsonResource("/json/createindex/createindex_analyis.json")
  }

  it should "support refresh interval" in {
    createIndex("test").refreshInterval(4.seconds)
  }

  it should "support the stopwords_path filter" in {
    val req = createIndex("users").analysis(
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
    CreateIndexContentBuilder(req).string() should matchJsonResource("/json/createindex/createindex_stop_path.json")
  }

  it should "support custom analyzers, normalizers, tokenizers and filters" in {
    val req = createIndex("users").analysis(
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
    ).normalizers(
      CustomNormalizerDefinition("myNormalizer1", LowercaseTokenFilter),
      CustomNormalizerDefinition("myNormalizer2", UppercaseTokenFilter, MappingCharFilter("mapping_charfilter2", "x" -> "y"))
    )
    CreateIndexContentBuilder(req).string() should matchJsonResource("/json/createindex/createindex_analyis2.json")
  }

  it should "supported nested fields" ignore {
//    val req = createIndex("users").mappings(
//      mapping("tweets") as(
//        stringField("_id") analyzer KeywordAnalyzer,
//        stringField("name") analyzer KeywordAnalyzer,
//        geopointField("locations") validate true normalize true,
//        dateField("date"),
//        longField("size"),
//        booleanField("read"),
//        stringField("content"),
//        nestedField("user").fields(
//          stringField("name"),
//          stringField("email"),
//          nestedField("last").fields(
//            dateField("lastLogin")
//          )
//        ) includeInRoot true includeInParent true
//      ) size true numericDetection true boostNullValue 1.2 boostName "myboost"
//    )
//    CreateIndexContentBuilder(req).string() should matchJsonResource("/json/createindex/mapping_nested.json")
  }

  it should "generate json to set index settings" in {
    val req = (createIndex("users")
      shards 3
      replicas 4
      refreshInterval "5s"
      indexSetting("compound_on_flush", false)
      indexSetting("compound_format", 0.5))
    CreateIndexContentBuilder(req).string() should matchJsonResource("/json/createindex/createindex_settings2.json")
  }

  it should "support inner objects" ignore {
//    val req = createIndex("tweets").mappings(
//      mapping("tweet") as(
//        objectField("person") inner(
//          objectField("name") inner(
//            stringField("first_name") analyzer KeywordAnalyzer,
//            stringField("last_name") analyzer KeywordAnalyzer,
//            byteField("byte"),
//            shortField("short")
//          ),
//          stringField("sid") index "not_analyzed"
//        ),
//        stringField("message")
//      ) size true numericDetection true boostNullValue 1.2 boostName "myboost"
//    )
//    CreateIndexContentBuilder(req).string() should matchJsonResource("/json/createindex/mapping_inner_object.json")
  }

  it should "support disabled inner objects" ignore {
//    val req = createIndex("tweets").mappings(
//      mapping("tweet") as(
//        objectField("person") inner(
//          objectField("name").enabled(false),
//          stringField("sid") index "not_analyzed"
//        ),
//        stringField("message")
//      ) size true numericDetection true boostNullValue 1.2 boostName "myboost"
//    )
//    CreateIndexContentBuilder(req).string() should matchJsonResource("/json/createindex/mapping_inner_object_disabled.json")
  }

  it should "support nested multi fields" in {
    val req = createIndex("tweets").mappings(
      mapping("tweet").fields(
        textField("name").fields(
          keywordField("username"),
          keywordField("principal")
        )
      )
    )
    CreateIndexContentBuilder(req).string() should matchJsonResource("/json/createindex/mapping_multi_field_type_1.json")
  }

  it should "support copy to a single field" in {
    val req = createIndex("tweets").mappings(
      mapping("tweet") as(
        textField("first_name") copyTo "full_name",
        textField("last_name") copyTo "full_name",
        textField("full_name")
      ) size true numericDetection true boostNullValue 1.2 boostName "myboost" dynamic DynamicMapping.Dynamic
    )
    CreateIndexContentBuilder(req).string() should matchJsonResource("/json/createindex/mapping_copy_to_single_field.json")
  }

  it should "support copy to multiple fields" in {
    val req = createIndex("tweets").mappings(
      mapping("tweet") as(
        textField("title") copyTo("meta_data", "article_info"),
        textField("meta_data"),
        textField("article_info")
      ) size true numericDetection true boostNullValue 1.2 boostName "myboost" dynamic DynamicMapping.Strict
    )
    CreateIndexContentBuilder(req).string() should matchJsonResource("/json/createindex/mapping_copy_to_multiple_fields.json")
  }

  it should "support multi fields" in {
    val req = createIndex("tweets").mappings(
      mapping("tweet") as(
        textField("title") fields textField("raw"),
        textField("meta_data"),
        textField("article_info")
      ) size true numericDetection true boostNullValue 1.2 boostName "myboost"
    )
    CreateIndexContentBuilder(req).string() should matchJsonResource("/json/createindex/mapping_multi_fields.json")
  }

  it should "support completion type" in {
    val req = createIndex("tweets").mappings(
      mapping("tweet") as(
        textField("name"),
        completionField("ac") analyzer "simple" searchAnalyzer "simple"
          preserveSeparators false preservePositionIncrements false maxInputLength 10
      ) size true numericDetection true boostNullValue 1.2 boostName "myboost"
    )
    CreateIndexContentBuilder(req).string() should matchJsonResource("/json/createindex/mapping_completion_type.json")
  }

  it should "support creating parent mappings" in {
    val req = createIndex("docsAndTags").mappings(
      mapping("tags") as stringField("tag") parent "docs" source true all false dynamic DynamicMapping.Strict
    )
    CreateIndexContentBuilder(req).string() should matchJsonResource("/json/createindex/create_parent_mappings.json")
  }

  it should "generate json to enable timestamp" in {
    val req = createIndex("tweets").mappings(
      mapping("tweet") as(
        geopointField("name"),
        dateField("content") nullValue "no content"
      ) all true size true numericDetection true boostNullValue 1.2 boostName "myboost" timestamp true
    )
    CreateIndexContentBuilder(req).string() should matchJsonResource("/json/createindex/createindex_timestamp_1.json")
  }

  it should "generate json to enable timestamp with path and format" in {
    val req = createIndex("tweets").mappings(
      mapping("tweet") as(
        geopointField("name"),
        dateField("content") nullValue "no content"
      ) source false size true numericDetection true boostNullValue 1.2 boostName "myboost" timestamp(true, path = Some(
        "post_date"), format = Some("YYYY-MM-dd"))
    )
    CreateIndexContentBuilder(req).string() should matchJsonResource("/json/createindex/createindex_timestamp_2.json")
  }

  it should "generate json to enable timestamp with path and format and default null" in {
    val req = createIndex("tweets").mappings(
      mapping("tweet") as(
        geopointField("name"),
        dateField("content") nullValue "no content"
      ) size true numericDetection true boostNullValue 1.2 boostName "myboost" timestamp(true, default = Some(null))
    )
    CreateIndexContentBuilder(req).string() should matchJsonResource("/json/createindex/createindex_timestamp_3.json")
  }

  it should "accept pre-built mapping JSON" ignore {
    val source = Source
      .fromInputStream(getClass.getResourceAsStream("/json/createindex/createindex_mappings.json"))
      .mkString

    val req = createIndex("tweets").source(source)
    val content = CreateIndexContentBuilder(req)
  }
}
