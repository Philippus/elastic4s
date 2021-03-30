package com.sksamuel.elastic4s.requests.searches.suggestion

case class SuggestionResult(text: String, offset: Int, length: Int, options: Seq[Map[String, Any]]) {
  def toCompletion: CompletionSuggestionResult =
    CompletionSuggestionResult(text, offset, length, options.map(CompletionSuggestionOption))
  def toTerm: TermSuggestionResult = TermSuggestionResult(text, offset, length, options.map(TermSuggestionOption))
  def toPhrase: PhraseSuggestionResult =
    PhraseSuggestionResult(text, offset, length, options.map(PhraseSuggestionOption))
}
