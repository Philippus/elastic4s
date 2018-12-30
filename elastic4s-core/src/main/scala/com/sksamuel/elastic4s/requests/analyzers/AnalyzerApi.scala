package com.sksamuel.elastic4s.requests.analyzers

trait AnalyzerApi {

  def stopAnalyzer(name: String): StopAnalyzerDefinition                      = StopAnalyzerDefinition(name)
  def standardAnalyzer(name: String): StandardAnalyzerDefinition              = StandardAnalyzerDefinition(name)
  def patternAnalyzer(name: String, regex: String): PatternAnalyzerDefinition = PatternAnalyzerDefinition(name, regex)
  def snowballAnalyzer(name: String): SnowballAnalyzerDefinition              = SnowballAnalyzerDefinition(name)
  def customAnalyzer(name: String, tokenizer: Tokenizer): CustomAnalyzerDefinition =
    CustomAnalyzerDefinition(name, tokenizer)

  def customAnalyzer(name: String,
                     tokenizer: Tokenizer,
                     filter: TokenFilter,
                     rest: TokenFilter*): CustomAnalyzerDefinition =
    CustomAnalyzerDefinition(name, tokenizer, filter +: rest)
}
