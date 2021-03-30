package com.sksamuel.elastic4s.requests.searches.suggestion

case class TermSuggestionResult(text: String, offset: Int, length: Int, options: Seq[TermSuggestionOption]) {
  def optionsText: Seq[String] = options.map(_.text)
}
