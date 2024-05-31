package com.sksamuel.elastic4s.requests.synonyms

import com.fasterxml.jackson.annotation.JsonProperty

case class ListSynonymsResult(@JsonProperty("synonyms_set") synonymsSet: String, count: Int)
case class ListSynonymsSetResponse(count: Int, results: Seq[ListSynonymsResult])
