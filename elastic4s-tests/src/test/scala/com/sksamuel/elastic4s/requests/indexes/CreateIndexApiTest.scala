package com.sksamuel.elastic4s.requests.indexes

import com.sksamuel.elastic4s.ElasticDsl.*
import com.sksamuel.elastic4s.JsonSugar
import com.sksamuel.elastic4s.analysis.{
  Analysis,
  CustomAnalyzer,
  CustomNormalizer,
  EdgeNGramTokenFilter,
  EdgeNGramTokenizer,
  LengthTokenFilter,
  LimitTokenCountTokenFilter,
  MappingCharFilter,
  NGramTokenizer,
  PatternAnalyzer,
  PatternReplaceCharFilter,
  PatternReplaceTokenFilter,
  SnowballTokenFilter,
  StandardAnalyzer,
  StandardTokenizer,
  StemmerOverrideTokenFilter,
  StemmerTokenFilter,
  StopTokenFilter,
  UniqueTokenFilter,
  WordDelimiterTokenFilter
}
import com.sksamuel.elastic4s.handlers.index
import com.sksamuel.elastic4s.handlers.index.CreateIndexContentBuilder
import com.sksamuel.elastic4s.requests.mappings.PrefixTree
import com.sksamuel.elastic4s.requests.mappings.dynamictemplate.DynamicMapping
import org.scalatest.OneInstancePerTest
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration.*
import scala.io.Source
import scala.util.Using

class CreateIndexApiTest extends AnyFlatSpec with JsonSugar with Matchers with OneInstancePerTest {

  "the index dsl" should "generate json to include mapping properties" in {
    val req = createIndex("users").mapping(
      properties(
        ipField("name").boost(1.0),
        intField("location").nullValue(0),
        binaryField("email"),
        floatField("age"),
        geoshapeField("area").tree(PrefixTree.Quadtree).precision("1m")
      ).all(true).analyzer("somefield").dateDetection(true).dynamicDateFormats("mm/yyyy", "dd-MM-yyyy")
    )
    CreateIndexContentBuilder(req).string should matchJsonResource("/json/createindex/createindex_mappings.json")
  }

  it should "allow provide mapping properties using rawSource" in {
    val tweetRawSource =
      """{"_all": {"enabled": false},"numeric_detection": true,"_boost": {"name": "myboost","null_value": 1.2},"_size": {"enabled": true},"properties": {"name": {"type": "geo_point"},"content": {"type": "date","null_value": "no content"}},"_meta": {"class": "com.sksamuel.User"}}"""
    val req            = createIndex("users").source(tweetRawSource)
    index.CreateIndexContentBuilder(req).string should matchJsonResource(
      "/json/createindex/createindex_raw_source.json"
    )
  }

  it should "support override built in analyzers" in {
    val req = createIndex("users").analysis(
      Analysis(
        analyzers = List(
          StandardAnalyzer("standard", stopwords = Seq("stop1", "stop2")),
          StandardAnalyzer("myAnalyzer1", stopwords = Seq("the", "and"), maxTokenLength = 400)
        )
      )
    )
    index.CreateIndexContentBuilder(req).string should matchJsonResource("/json/createindex/createindex_analyis.json")
  }

  it should "support refresh interval" in {
    createIndex("test").refreshInterval(4.seconds)
  }

  it should "support the stopwords_path filter" in {
    val req = createIndex("users").analysis(
      Analysis(
        analyzers = List(
          PatternAnalyzer("patternAnalyzer", regex = "[a-z]"),
          CustomAnalyzer(
            "mysnowball",
            tokenizer = "standard",
            tokenFilters = List("lowercase", "mysnowballStop", "mysnowballStemmer")
          ),
          CustomAnalyzer(
            "myAnalyzer2",
            tokenizer = "myTokenizer1",
            tokenFilters = List(
              "myTokenFilter2",
              "myTokenFilter3",
              "myFrenchStemmerTokenFilter",
              "prTokenFilter",
              "myWordDelimiterTokenFilter"
            )
          ),
          CustomAnalyzer(
            "myAnalyzer3",
            tokenizer = "lowercase",
            charFilters = List("html_strip", "mapping_charfilter", "pattern_replace_charfilter"),
            tokenFilters = List(
              "myTokenFilter0",
              "myTokenFilter1",
              "reverse",
              "myTokenFilter5",
              "myEdgeNGramTokenFilter",
              "stemmerTokenFilter"
            )
          ),
          CustomAnalyzer("myAnalyzer4", tokenizer = "myTokenizer4"),
          CustomAnalyzer("myAnalyzer5", tokenizer = "myTokenizer5")
        ),
        tokenizers = List(
          StandardTokenizer("myTokenizer1", maxTokenLength = 900),
          EdgeNGramTokenizer("myTokenizer4", minGram = 3, maxGram = 17, tokenChars = Seq("digit", "letter")),
          NGramTokenizer("myTokenizer5", minGram = 4, maxGram = 18, tokenChars = Seq("letter", "punctuation"))
        ),
        tokenFilters = List(
          StopTokenFilter("mysnowballStop", stopwords = Seq("stop1", "stop2", "stop3")),
          SnowballTokenFilter("mysnowballStemmer", language = "English"),
          LengthTokenFilter("myTokenFilter2", min = Some(0), max = Some(10)),
          UniqueTokenFilter("myTokenFilter3", onlyOnSamePosition = true),
          StemmerTokenFilter("myFrenchStemmerTokenFilter", lang = "french"),
          PatternReplaceTokenFilter("prTokenFilter", "pattern", "rep"),
          WordDelimiterTokenFilter(
            "myWordDelimiterTokenFilter",
            generateWordParts = Some(true),
            generateNumberParts = Some(true),
            catenateNumbers = Some(false),
            catenateAll = Some(false),
            splitOnCaseChange = Some(true),
            preserveOriginal = Some(false),
            splitOnNumerics = Some(true),
            stemEnglishPossesive = Some(true)
          ),
          StopTokenFilter("myTokenFilter0", stopwordsPath = Some("stoplist.txt"), ignoreCase = Some(true)),
          StopTokenFilter("myTokenFilter1", ignoreCase = Some(true)),
          LimitTokenCountTokenFilter("myTokenFilter5", maxTokenCount = Some(5), consumeAllTokens = Some(false)),
          EdgeNGramTokenFilter("myEdgeNGramTokenFilter", minGram = 3, maxGram = 50),
          StemmerOverrideTokenFilter("stemmerTokenFilter", Seq("rule1", "rule2"))
        ),
        charFilters = List(
          MappingCharFilter("mapping_charfilter", Map("ph" -> "f", "qu" -> "q")),
          PatternReplaceCharFilter(
            "pattern_replace_charfilter",
            pattern = "sample(.*)",
            replacement = "replacedSample $1"
          )
        )
      )
    )
    index.CreateIndexContentBuilder(req).string should matchJsonResource("/json/createindex/createindex_stop_path.json")
  }

  it should "support custom analyzers, normalizers, tokenizers and filters" in {
    val req = createIndex("users").analysis(
      Analysis(
        analyzers = List(
          PatternAnalyzer("patternAnalyzer", regex = "[a-z]"),
          CustomAnalyzer(
            "mysnowball",
            tokenizer = "standard",
            tokenFilters = List("lowercase", "mysnowballStop", "mysnowballStemmer")
          ),
          CustomAnalyzer(
            "myAnalyzer2",
            tokenizer = "myTokenizer1",
            tokenFilters = List(
              "myTokenFilter2",
              "myTokenFilter3",
              "myFrenchStemmerTokenFilter",
              "prTokenFilter",
              "myWordDelimiterTokenFilter"
            )
          ),
          CustomAnalyzer(
            "myAnalyzer3",
            tokenizer = "lowercase",
            charFilters = List("html_strip", "mapping_charfilter", "pattern_replace_charfilter"),
            tokenFilters = List(
              "myTokenFilter1",
              "reverse",
              "myTokenFilter5",
              "myEdgeNGramTokenFilter",
              "stemmerTokenFilter"
            )
          ),
          CustomAnalyzer("myAnalyzer4", tokenizer = "myTokenizer4"),
          CustomAnalyzer("myAnalyzer5", tokenizer = "myTokenizer5")
        ),
        tokenizers = List(
          StandardTokenizer("myTokenizer1", maxTokenLength = 900),
          EdgeNGramTokenizer("myTokenizer4", minGram = 3, maxGram = 17, tokenChars = Seq("digit", "letter")),
          NGramTokenizer("myTokenizer5", minGram = 4, maxGram = 18, tokenChars = Seq("letter", "punctuation"))
        ),
        tokenFilters = List(
          StopTokenFilter("mysnowballStop", stopwords = Seq("stop1", "stop2", "stop3")),
          SnowballTokenFilter("mysnowballStemmer", language = "English"),
          LengthTokenFilter("myTokenFilter2", min = Some(0), max = Some(10)),
          UniqueTokenFilter("myTokenFilter3", onlyOnSamePosition = true),
          StemmerTokenFilter("myFrenchStemmerTokenFilter", lang = "french"),
          PatternReplaceTokenFilter("prTokenFilter", "pattern", "rep"),
          WordDelimiterTokenFilter(
            "myWordDelimiterTokenFilter",
            generateWordParts = Some(true),
            generateNumberParts = Some(true),
            catenateAll = Some(true)
          ),
          StopTokenFilter("myTokenFilter1", ignoreCase = Some(true)),
          LimitTokenCountTokenFilter("myTokenFilter5", maxTokenCount = Some(5), consumeAllTokens = Some(false)),
          EdgeNGramTokenFilter("myEdgeNGramTokenFilter", minGram = 3, maxGram = 50),
          StemmerOverrideTokenFilter("stemmerTokenFilter", Seq("rule1", "rule2"))
        ),
        charFilters = List(
          MappingCharFilter("mapping_charfilter", Map("ph" -> "f", "qu" -> "q")),
          MappingCharFilter("mapping_charfilter2", Map("x" -> "y")),
          PatternReplaceCharFilter(
            "pattern_replace_charfilter",
            pattern = "sample(.*)",
            replacement = "replacedSample $1"
          )
        ),
        normalizers = List(
          CustomNormalizer("myNormalizer1", charFilters = Nil, tokenFilters = List("lowercase")),
          CustomNormalizer("myNormalizer2", charFilters = List("mapping_charfilter2"), tokenFilters = List("uppercase"))
        )
      )
    )
    CreateIndexContentBuilder(req).string should matchJsonResource("/json/createindex/createindex_analyis2.json")
  }

  it should "supported nested fields" in {
    val req = createIndex("users").mapping(
      properties(
        textField("_id").analyzer("keyword"),
        textField("name").analyzer("keyword"),
        geopointField("locations").docValues(true),
        dateField("date"),
        longField("size"),
        booleanField("read"),
        textField("content"),
        nestedField("user").fields(
          textField("name"),
          textField("email"),
          nestedField("last").fields(
            dateField("lastLogin")
          )
        )
      ).size(true).numericDetection(true).boostNullValue(1.2).boostName("myboost")
    )
    index.CreateIndexContentBuilder(req).string should matchJsonResource("/json/createindex/mapping_nested.json")
  }

  it should "generate json to set index settings" in {
    val req =
      (createIndex("users")
        `shards` 3
        `replicas` 4
        `refreshInterval` "5s"
        `indexSetting` ("compound_on_flush", false)
        `indexSetting` ("compound_format", 0.5))
    index.CreateIndexContentBuilder(req).string should matchJsonResource("/json/createindex/createindex_settings2.json")
  }

  it should "support inner objects" ignore {
//    val req = createIndex("tweets").mappings(
//      mapping("tweet") as(
    //        objectField("person").inner(
    //          objectField("name").inner(
    //            textField("first_name") analyzer KeywordAnalyzer,
    //            textField("last_name") analyzer KeywordAnalyzer,
//            byteField("byte"),
//            shortField("short")
//          ),
    //          textField("sid").index(true)
//        ),
    //        textField("message")
//      ) size true numericDetection true boostNullValue 1.2 boostName "myboost"
//    )
//    CreateIndexContentBuilder(req).string should matchJsonResource("/json/createindex/mapping_inner_object.json")
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
//    CreateIndexContentBuilder(req).string should matchJsonResource("/json/createindex/mapping_inner_object_disabled.json")
  }

  it should "support nested multi fields" in {
    val req = createIndex("tweets").mapping(
      properties(
        textField("name").fields(
          keywordField("username"),
          keywordField("principal")
        )
      )
    )
    index.CreateIndexContentBuilder(req).string should matchJsonResource(
      "/json/createindex/mapping_multi_field_type_1.json"
    )
  }

  it should "support copy to a single field" in {
    val req = createIndex("tweets").mapping(
      properties(
        textField("first_name").copyTo("full_name"),
        textField("last_name").copyTo("full_name"),
        textField("full_name")
      ).size(true).numericDetection(true).boostNullValue(1.2).boostName("myboost").dynamic(DynamicMapping.Dynamic)
    )
    index.CreateIndexContentBuilder(req).string should matchJsonResource(
      "/json/createindex/mapping_copy_to_single_field.json"
    )
  }

  it should "support copy to multiple fields" in {
    val req = createIndex("tweets").mapping(
      properties(
        textField("title").copyTo("meta_data", "article_info"),
        textField("meta_data"),
        textField("article_info")
      ).size(true).numericDetection(true).boostNullValue(1.2).boostName("myboost").dynamic(DynamicMapping.Strict)
    )
    index.CreateIndexContentBuilder(req).string should matchJsonResource(
      "/json/createindex/mapping_copy_to_multiple_fields.json"
    )
  }

  it should "support multi fields" in {
    val req = createIndex("tweets").mapping(
      properties(
        textField("title").fields(textField("raw")),
        textField("meta_data"),
        textField("article_info")
      ).size(true).numericDetection(true).boostNullValue(1.2).boostName("myboost")
    )
    index.CreateIndexContentBuilder(req).string should matchJsonResource("/json/createindex/mapping_multi_fields.json")
  }

  it should "support completion type" in {
    val req = createIndex("tweets").mapping(
      properties(
        textField("name"),
        completionField("ac").analyzer("simple").searchAnalyzer("simple")
          .preserveSeparators(false).preservePositionIncrements(false).maxInputLength(10)
      ).size(true).numericDetection(true).boostNullValue(1.2).boostName("myboost")
    )
    index.CreateIndexContentBuilder(req).string should matchJsonResource(
      "/json/createindex/mapping_completion_type.json"
    )
  }

//  it should "support creating parent mappings" in {
//    val req = createIndex("docsAndTags").mappings(
//      mapping(
//        textField("tag") parent "docs" source true all false dynamic DynamicMapping.Strict
//      )
//    )
//    CreateIndexContentBuilder(req).string should matchJsonResource("/json/createindex/create_parent_mappings.json")
//  }

  it should "accept pre-built mapping JSON" in {
    val source = Using.resource(
      Source.fromInputStream(getClass.getResourceAsStream("/json/createindex/createindex_mappings.json"))
    )(_.mkString)
    val req    = createIndex("tweets").source(source)
    index.CreateIndexContentBuilder(req)
  }

  it should "support wildcard fields" in {
    val req = createIndex("logs").mapping(
      properties(
        wildcardField("message")
          .ignoreAbove(100)
          .nullValue("<empty message>")
      )
    )
    index.CreateIndexContentBuilder(req).string should matchJsonResource("/json/createindex/mapping_wildcard.json")
  }
}
