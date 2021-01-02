package com.sksamuel.elastic4s.requests.indexes.analyze

import com.sksamuel.elastic4s.analysis.{Analyzer, CharFilter, Tokenizer}

case class AnalyzeRequest(text: Array[String],
                          analyzer: Option[String] = None,
                          explain:Boolean = false,
                          index: Option[String] = None,
                          tokenizer:Option[String] = None,
                          filters:Seq[String] = Seq.empty,
                          charFilters:Seq[String] = Seq.empty,
                          filtersFromAnalyzers:Seq[Analyzer] = Seq.empty) {

  def text(content:String): AnalyzeRequest = copy(Array(content))

  def analyzer(name: String): AnalyzeRequest = copy(analyzer = Some(name))

  def analyzer(analyzer:Analyzer) : AnalyzeRequest = copy(analyzer = Some(analyzer.name))

  def index(name: String): AnalyzeRequest = copy(index = Some(name))

  def explain(_explain: Boolean): AnalyzeRequest = copy(explain = _explain)

  def tokenizer(tk:String): AnalyzeRequest = copy(tokenizer = Some(tk))
  def tokenizer(tk:Tokenizer): AnalyzeRequest = copy(tokenizer = Some(tk.name))

  def filters(filter:String,other:String*): AnalyzeRequest = copy(filters = filter +: other)
  def filters(analyzer: Analyzer*): AnalyzeRequest = copy(filtersFromAnalyzers = analyzer)

  def charFilters(charFilter:String,other:String*): AnalyzeRequest = copy(charFilters = charFilter +: other)
  def charFilters(charFilter:CharFilter*): AnalyzeRequest = copy(charFilters = charFilter.map(_.name))

}
