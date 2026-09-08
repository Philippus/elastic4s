package com.sksamuel.elastic4s.requests.synonyms

case class DeleteSynonymRuleRequest(synonymsSet: String, synonymRule: String, refresh: Option[Boolean] = None)
