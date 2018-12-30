package com.sksamuel.elastic4s.requests.searches.suggestion

import java.util.UUID

trait SuggestionApi {

  def completionSuggestion(): CompletionSuggExpectsField             = completionSuggestion(UUID.randomUUID.toString)
  def completionSuggestion(name: String): CompletionSuggExpectsField = new CompletionSuggExpectsField(name)
  class CompletionSuggExpectsField(name: String) {
    def on(field: String) = CompletionSuggestion(name, field)
  }

  def termSuggestion(): TermSuggExpectsField                    = termSuggestion(UUID.randomUUID.toString)
  def termSuggestion(name: String, field: String, text: String) = TermSuggestion(name, field, Some(text))

  def termSuggestion(name: String): TermSuggExpectsField = new TermSuggExpectsField(name)
  class TermSuggExpectsField(name: String) {
    def on(field: String) = TermSuggestion(name, field, Some(""))
  }

  def phraseSuggestion(): PhraseSuggExpectsField             = phraseSuggestion(UUID.randomUUID.toString)
  def phraseSuggestion(name: String): PhraseSuggExpectsField = new PhraseSuggExpectsField(name)
  class PhraseSuggExpectsField(name: String) {
    def on(field: String) = PhraseSuggestion(name, field)
  }
}
