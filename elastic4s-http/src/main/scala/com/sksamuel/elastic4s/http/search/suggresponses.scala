package com.sksamuel.elastic4s.http.search

case class SuggestionEntry(term: String) {
  def options: Seq[String] = Nil
  def optionsText: String = ""
}

case class CompletionSuggestionResult(entries: Seq[SuggestionEntry]) {
  def entry(term: String): SuggestionEntry = entries.find(_.term == term).get
}

case class PhraseSuggestionResult(entries: Seq[SuggestionEntry]) {
  def entry(term: String): SuggestionEntry = entries.find(_.term == term).get
}

case class SuggestionOption(text: String, score: Double, freq: Int)

case class SuggestionResult(text: String,
                            offset: Int,
                            length: Int,
                            options: Seq[SuggestionOption]) {
  def toTerm: TermSuggestionResult = TermSuggestionResult(text, offset, length, options)
}

case class TermSuggestionResult(text: String,
                                offset: Int,
                                length: Int,
                                options: Seq[SuggestionOption]) {
  def optionsText: Seq[String] = options.map(_.text)
}
