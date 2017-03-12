---
layout: docs
title:  "Analyzers"
section: "docs"
---

# Analyzers

Analyzers are created from a single Tokenizer, zero or more TokenFilters and zero or more CharFilters and have a name that is used to refer to them.

Character filters are used to preprocess the string of characters before it is passed to the tokenizer. A character filter may be used to strip out HTML markup, or to convert ```&``` characters to the word ```and```.

Tokenizers split the incoming string into tokens. A very simple example (but common use case) is to split a string into tokens on whitespace. So "Tony Mowbray" would be split into "Tony" and "Mowbray".

TokenFilters apply after the tokenizer, and can modify the incoming tokens. So if we applied a reverse TokenFilter, then ["Tony", "Mowbray"] would become ["ynot", "yarbwom"].

By default Elasticsearch registers multiple built-in analyzers. These are

* [standard analyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-standard-analyzer.html)
* [simple analyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-simple-analyzer.html)
* [whitespace analyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-whitespace-analyzer.html)
* [stop analyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-stop-analyzer.html)
* [keyword analyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-keyword-analyzer.html)
* [pattern analyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-pattern-analyzer.html)
* [language analyzers](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-lang-analyzer.html)
* [snowball analyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-snowball-analyzer.html)

You can use these by simply using the case object for each analyzer. Eg, SimpleAnalyzer is a case object that you can pass as a parameter when you need to specify an analyzer. You don't have to do anything to explicity enable them as they are always there.

Here is an example of using a WhitespaceAnalyzer to split up a field on whitespace.

```scala
create.index("people").mappings(
  "scientists" as {
    "name" analyzer WhitespaceAnalyzer
  }
)
```

## Custom Analyzers

Sometimes the built in analyzers aren't suitable for your particular use case and you want to be able to specify your own analyzer. Elasticsearch allows you to create a [custom analyzer](http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/analysis-custom-analyzer.html) which gives you full control over how an analyzer is specified. Also, there are shortcuts that allow you to use one of the built-in analyzers but with your own options (for example, if you wanted to use the "stop analzyer" but with your own list of stop words). Not all built-in analyzers can be modified, eg the WhitespaceAnalyzer has no options.

To use a shortcut, the DSL makes it very easy. For example, lets create a mapping for "myindex/users", and we want to index each user's name by splitting up on commas. In this index we will register a pattern analyzer that will split up tokens on commas.

```scala
create.index("people").mappings(
  "scientists" as {
    "name" analyzer "pat1"
  }
).analysis(
  PatternAnalyzerDefinition("pat1", regex = ",")
)
```

Notice that we give the analyzer a name. This is so we can refer to it in the mapping part. You can use the same definition in multiple mappings for the same index, and you only need to specify the definition once. It's also possible to use the same name as one of the built in analyzers. If you do this, then you will override the built-in analyzer.

If the shortcut definitions are still not cutting it, then we can create a fully custom analyzer. Remember from the original definition of an analyzer, you specify a tokenizer, and optionally tokenfilters and charfilters.

Here is an example of registering a custom analyzer that uses a whitespace tokenizer, with lowercase and reverse token filters (just for fun).

```scala
create.index("people").mappings(
  "scientists" as {
    "name" analyzer "custom1"
  }
).analysis(
  CustomAnalyzerDefinition(
    "custom1",
    WhitespaceTokenizer,
    LowercaseTokenFilter,
    ReverseTokenFilter
  )
)
```

We can of course use as many analyzers as we want. Here is a nonsense but technically valid example used in one of the unit tests.

```scala
create.index("users").mappings(
  "tweets" as(
    id typed StringType analyzer KeywordAnalyzer store true includeInAll true,
    "name" typed GeoPointType analyzer SimpleAnalyzer boost 4 index "not_analyzed",
    "content" typed DateType analyzer "myAnalyzer3" nullValue "no content"
  ),
  map("users").as(
    "name" typed IpType analyzer WhitespaceAnalyzer omitNorms true,
    "location" typed IntegerType analyzer "myAnalyzer2" ignoreAbove 50,
    "email" typed BinaryType analyzer StandardAnalyzer,
    "picture" typed AttachmentType,
    "age" typed FloatType,
    "area" typed GeoShapeType
  )
).analysis(
  PatternAnalyzerDefinition("patternAnalyzer", regex = "[a-z]"),
  SnowballAnalyzerDefinition("mysnowball", stopwords = Seq("stop1", "stop2", "stop3")),
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
    StemmerOverrideTokenFilter("stemmerTokenFilter", Array("rule1", "rule2")),
    HtmlStripCharFilter,
    MappingCharFilter("mapping_charfilter", "ph" -> "f", "qu" -> "q"),
    PatternReplaceCharFilter(
      "pattern_replace_charfilter",
      pattern = "sample(.*)",
      replacement = "replacedSample $1"
    )
  )
)
```
