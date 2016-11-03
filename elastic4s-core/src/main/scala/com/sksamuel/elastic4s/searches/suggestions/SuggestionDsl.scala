package com.sksamuel.elastic4s.searches.suggestions

import java.util.UUID

trait SuggestionDsl {

  def completionSuggestion(): CompletionSuggestionDefinition = completionSuggestion(UUID.randomUUID.toString)

  def completionSuggestion(fieldname: String): CompletionSuggestionDefinition =
    CompletionSuggestionDefinition(fieldname)

  def termSuggestion(): TermSuggestionDefinition = termSuggestion(UUID.randomUUID.toString)
  def termSuggestion(fieldname: String): TermSuggestionDefinition = TermSuggestionDefinition(fieldname)

  def phraseSuggestion(): PhraseSuggestionDefinition = phraseSuggestion(UUID.randomUUID.toString)
  def phraseSuggestion(fieldname: String): PhraseSuggestionDefinition = PhraseSuggestionDefinition(fieldname)
}

