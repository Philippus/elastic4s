package com.sksamuel.elastic4s.requests.searches.suggestion

case class CompletionSuggestionResult(
    text: String,
    offset: Int,
    length: Int,
    options: Seq[CompletionSuggestionOption]
) {
  def optionsText: Seq[String] = options.map(_.text)
}
