package com.sksamuel.elastic4s.requests.synonyms

case class CreateOrUpdateSynonymRuleRequest(synonymsSet: String, synonymRule: String, synonyms: String)
