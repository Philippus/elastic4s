package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.indexes.analyze.AnalyzeRequest

trait AnalyzeApi {

  def analyze(text: String, other: String*): AnalyzeRequest = AnalyzeRequest.text(text, other: _*)

}
