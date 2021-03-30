package com.sksamuel.elastic4s.requests.searches.suggestion

case class PhraseSuggestionResult(text: String, offset: Int, length: Int, options: Seq[PhraseSuggestionOption]) {
  def optionsText: Seq[String] = options.map(_.text)
}
