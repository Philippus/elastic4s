package com.sksamuel.elastic4s.requests.analyzers

trait AnalyzerApi {

  @deprecated("use new analysis package", "7.0.1")
  def stopAnalyzer(name: String): StopAnalyzerDefinition                      = StopAnalyzerDefinition(name)

  @deprecated("use new analysis package", "7.0.1")
  def standardAnalyzer(name: String): StandardAnalyzerDefinition              = StandardAnalyzerDefinition(name)

  @deprecated("use new analysis package", "7.0.1")
  def patternAnalyzer(name: String, regex: String): PatternAnalyzerDefinition = PatternAnalyzerDefinition(name, regex)

  @deprecated("use new analysis package", "7.0.1")
  def snowballAnalyzer(name: String): SnowballAnalyzerDefinition              = SnowballAnalyzerDefinition(name)

  @deprecated("use new analysis package", "7.0.1")
  def customAnalyzer(name: String, tokenizer: Tokenizer): CustomAnalyzerDefinition =
    CustomAnalyzerDefinition(name, tokenizer)

  @deprecated("use new analysis package", "7.0.1")
  def customAnalyzer(name: String,
                     tokenizer: Tokenizer,
                     filter: TokenFilter,
                     rest: TokenFilter*): CustomAnalyzerDefinition =
    CustomAnalyzerDefinition(name, tokenizer, filter +: rest)
}
