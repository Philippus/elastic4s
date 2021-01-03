package com.sksamuel.elastic4s.requests.indexes.analyze

trait AnalyzeApi {

  def analyze(text: String,other:String*): AnalyzeRequest = AnalyzeRequest.text(text,other:_*)

}
