package com.sksamuel.elastic4s.searches.suggestions

import java.util.UUID

trait SuggestionDsl {

  def completionSuggestion(): CompletionSuggExpectsField = completionSuggestion(UUID.randomUUID.toString)
  def completionSuggestion(name: String): CompletionSuggExpectsField = new CompletionSuggExpectsField(name)
  class CompletionSuggExpectsField(name: String) {
    def on(field: String) = CompletionSuggestionDefinition(name, field)
    @deprecated("use on(field)", "5.0.0")
    def field(field: String) = CompletionSuggestionDefinition(name, field)
  }

  def termSuggestion(): TermSuggExpectsField = termSuggestion(UUID.randomUUID.toString)
  def termSuggestion(name: String): TermSuggExpectsField = new TermSuggExpectsField(name)
  class TermSuggExpectsField(name: String) {
    def on(field: String) = TermSuggestionDefinition(name, field)

    @deprecated("use on(field)", "5.0.0")
    def field(field: String) = TermSuggestionDefinition(name, field)
  }

  def phraseSuggestion(): PhraseSuggExpectsField = phraseSuggestion(UUID.randomUUID.toString)
  def phraseSuggestion(name: String): PhraseSuggExpectsField = new PhraseSuggExpectsField(name)
  class PhraseSuggExpectsField(name: String) {
    def on(field: String) = PhraseSuggestionDefinition(name, field)

    @deprecated("use on(field)", "5.0.0")
    def field(field: String) = PhraseSuggestionDefinition(name, field)
  }
}

