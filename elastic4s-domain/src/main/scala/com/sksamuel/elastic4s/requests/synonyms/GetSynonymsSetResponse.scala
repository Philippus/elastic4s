package com.sksamuel.elastic4s.requests.synonyms

import com.fasterxml.jackson.annotation.JsonProperty

case class GetSynonymsSetResponse(count: Int, @JsonProperty("synonyms_set") synonymsSet: Seq[SynonymRule])
