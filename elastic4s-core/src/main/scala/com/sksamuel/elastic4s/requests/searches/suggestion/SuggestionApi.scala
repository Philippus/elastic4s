package com.sksamuel.elastic4s.requests.searches.suggestion

import java.util.UUID

trait SuggestionApi {

  @deprecated("use completionSuggestion(name, field, text", "7.0")
  def completionSuggestion(): CompletionSuggExpectsField             = completionSuggestion(UUID.randomUUID.toString)

  def completionSuggestion(name: String, field: String): CompletionSuggestion = CompletionSuggestion(name, field)

  @deprecated("use completionSuggestion(name, field, text", "7.0")
  def completionSuggestion(name: String): CompletionSuggExpectsField = new CompletionSuggExpectsField(name)
  class CompletionSuggExpectsField(name: String) {
    def on(field: String) = CompletionSuggestion(name, field)
  }

  def termSuggestion(): TermSuggExpectsField                    = termSuggestion(UUID.randomUUID.toString)
  def termSuggestion(name: String, field: String, text: String) = TermSuggestion(name, field, Some(text))

  @deprecated("use termSuggestion(name, field, text", "7.0")
  def termSuggestion(name: String): TermSuggExpectsField = new TermSuggExpectsField(name)
  class TermSuggExpectsField(name: String) {
    def on(field: String) = TermSuggestion(name, field, Some(""))
  }

  @deprecated("use phraseSuggestion(name, field, text", "7.0")
  def phraseSuggestion(): PhraseSuggExpectsField             = phraseSuggestion(UUID.randomUUID.toString)

  def phraseSuggestion(name: String, field: String): PhraseSuggestion = PhraseSuggestion(name, field)

  @deprecated("use phraseSuggestion(name, field, text", "7.0")
  def phraseSuggestion(name: String): PhraseSuggExpectsField = new PhraseSuggExpectsField(name)
  class PhraseSuggExpectsField(name: String) {
    def on(field: String) = PhraseSuggestion(name, field)
  }
}
