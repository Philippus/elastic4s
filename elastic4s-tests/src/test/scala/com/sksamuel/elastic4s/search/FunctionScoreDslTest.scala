package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.ElasticDsl

class FunctionScoreDslTest extends ElasticDsl {

  functionScoreQuery(matchAllQuery) scoreFuncs(
    weightScore(1.3).filter(termQuery("is_comment", "false")),
    weightScore(1.3).filter(termQuery("postdate", "now/d-3y"))
  )
}
