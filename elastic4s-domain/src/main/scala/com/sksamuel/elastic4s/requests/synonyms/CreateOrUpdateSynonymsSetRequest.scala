package com.sksamuel.elastic4s.requests.synonyms

case class CreateOrUpdateSynonymsSetRequest(
    synonymsSet: String,
    synonymRules: Seq[SynonymRule],
    refresh: Option[Boolean] = None,
    append: Option[Boolean] = None
)
