package com.sksamuel.elastic4s.requests.indexes.analyze

trait AnalyzeApi {

  def analyze(text: String): AnalyzeRequest = AnalyzeRequest(Array(text))

  def analyze(text: Array[String]): AnalyzeRequest = AnalyzeRequest(text)

  def analyze(text: String, analyzer: String): AnalyzeRequest = AnalyzeRequest(Array(text), analyzer = Some(analyzer))

}
