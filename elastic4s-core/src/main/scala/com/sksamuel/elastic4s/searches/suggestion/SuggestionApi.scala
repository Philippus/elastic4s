package com.sksamuel.elastic4s.searches.suggestion

import java.util.UUID

trait SuggestionApi {

  def completionSuggestion(): CompletionSuggExpectsField = completionSuggestion(UUID.randomUUID.toString)
  def completionSuggestion(name: String): CompletionSuggExpectsField = new CompletionSuggExpectsField(name)
  class CompletionSuggExpectsField(name: String) {
    @deprecated("use on(field)", "5.0.0")
    def field(field: String): CompletionSuggestionDefinition = on(name)
    def on(field: String) = CompletionSuggestionDefinition(name, field)
  }

  def termSuggestion(): TermSuggExpectsField = termSuggestion(UUID.randomUUID.toString)
  def termSuggestion(name: String, field: String, text: String) = TermSuggestionDefinition(name, field, Some(text))

  def termSuggestion(name: String): TermSuggExpectsField = new TermSuggExpectsField(name)
  class TermSuggExpectsField(name: String) {
    @deprecated("use on(field)", "5.0.0")
    def field(field: String): TermSuggestionDefinition = on(field)
    def on(field: String) = TermSuggestionDefinition(name, field, Some(""))
  }

  def phraseSuggestion(): PhraseSuggExpectsField = phraseSuggestion(UUID.randomUUID.toString)
  def phraseSuggestion(name: String): PhraseSuggExpectsField = new PhraseSuggExpectsField(name)
  class PhraseSuggExpectsField(name: String) {
    @deprecated("use on(field)", "5.0.0")
    def field(field: String): PhraseSuggestionDefinition = on(name)
    def on(field: String) = PhraseSuggestionDefinition(name, field)
  }
}
