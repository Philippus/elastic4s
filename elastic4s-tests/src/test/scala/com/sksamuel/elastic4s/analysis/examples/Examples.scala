package com.sksamuel.elastic4s.analysis.examples

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.analysis.{
  Analysis,
  CustomAnalyzer,
  EdgeNGramTokenFilter,
  LanguageAnalyzers,
  WhitespaceTokenizer
}
import com.sksamuel.elastic4s.fields.TextField

class Examples extends ElasticDsl {

  // using a built in analyzer
  createIndex("people").mapping(
    properties(
      TextField("name", analyzer = Some("standard"))
    )
  )

  // using a built in language analzyer
  createIndex("people").mapping(
    properties(
      TextField("name", analyzer = Some("standard")),
      TextField("bio", analyzer = Some(LanguageAnalyzers.brazilian))
    )
  )

  // this example uses seperate analzyers for indexing and searching
  createIndex("people").mapping(
    properties(
      TextField("name", analyzer = Some("standard"), searchAnalyzer = Some("snowball"))
    )
  )

  createIndex("people").mapping(
    properties(
      TextField("name", analyzer = Some("custom1"))
    )
  ).analysis(
    Analysis(
      CustomAnalyzer(
        "custom1",
        tokenizer = "whitespace",
        tokenFilters = List("lowercase", "reverse")
      )
    )
  )

  // with custom tokenizer
  createIndex("people").mapping(
    properties(
      TextField("name", analyzer = Some("custom2"))
    )
  ).analysis(
    Analysis(
      analyzers = List(
        CustomAnalyzer(
          "custom2",
          tokenizer = "my_whitespace",
          tokenFilters = List("lowercase")
        )
      ),
      tokenizers = List(
        WhitespaceTokenizer("my_whitespace", maxTokenLength = 5)
      )
    )
  )

  // this example uses the same custom analzyer for multiple fields
  createIndex("people").mapping(
    properties(
      TextField("name", analyzer = Some("super_analyzer")),
      TextField("bio", analyzer = Some("super_analyzer")),
      TextField("address", analyzer = Some("super_analyzer"))
    )
  ).analysis(
    Analysis(
      analyzers = List(
        CustomAnalyzer(
          "super_analyzer",                           // names the analzyer
          tokenizer = "my_whitespace",                // uses the custom tokenizer
          tokenFilters = List("lowercase", "my_edge") // uses a built in token filter and a custom one
        )
      ),
      tokenizers = List(
        WhitespaceTokenizer("my_whitespace", maxTokenLength = 5)  // custom tokenizer
      ),
      tokenFilters = List(
        EdgeNGramTokenFilter("my_edge", minGram = 3, maxGram = 4) // custom token filter
      )
    )
  )
}
