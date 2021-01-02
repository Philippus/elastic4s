package com.sksamuel.elastic4s.requests.indexes.analyze

case class AnalyzeRequest(text: Array[String],
                          analyzer: Option[String] = None,
                          explain:Boolean = false,
                          index: Option[String] = None) {

  def analyzer(name: String): AnalyzeRequest = copy(analyzer = Some(name))

  def index(name: String): AnalyzeRequest = copy(index = Some(name))

  //  def attributes(attrs:Seq[String]): AnalyzeRequest = copy(attributes = attrs)

  def explain(expl: Boolean): AnalyzeRequest = copy(explain = expl)

}
