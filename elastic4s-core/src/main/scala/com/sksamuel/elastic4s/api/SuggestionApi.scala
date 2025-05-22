package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.handlers.searches.suggestion.PhraseSuggestion
import com.sksamuel.elastic4s.requests.searches.suggestion.{CompletionSuggestion, TermSuggestion}

trait SuggestionApi {
  def completionSuggestion(name: String, field: String): CompletionSuggestion = CompletionSuggestion(name, field)

  def termSuggestion(name: String, field: String, text: String): TermSuggestion =
    TermSuggestion(name, field, Some(text))

  def phraseSuggestion(name: String, field: String): PhraseSuggestion = PhraseSuggestion(name, field)
}
