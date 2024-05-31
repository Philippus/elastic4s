package com.sksamuel.elastic4s.requests.synonyms

case class GetSynonymsSetResponse(count: Int, synonymsSet: Seq[SynonymRule])
