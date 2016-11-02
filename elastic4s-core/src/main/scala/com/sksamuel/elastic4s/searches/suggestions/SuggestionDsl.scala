package com.sksamuel.elastic4s.searches.suggestions

import java.util.UUID

trait SuggestionDsl {

  def completionSuggestion(): CompletionSuggestionDefinition = completionSuggestion(UUID.randomUUID.toString)

  def completionSuggestion(name: String): CompletionSuggestionDefinition =
    CompletionSuggestionDefinition(name)

  def termSuggestion(): TermSuggestionDefinition = termSuggestion(UUID.randomUUID.toString)
  def termSuggestion(name: String): TermSuggestionDefinition = TermSuggestionDefinition(name)

  def phraseSuggestion(): PhraseSuggestionDefinition = phraseSuggestion(UUID.randomUUID.toString)
  def phraseSuggestion(name: String): PhraseSuggestionDefinition = PhraseSuggestionDefinition(name)
}

