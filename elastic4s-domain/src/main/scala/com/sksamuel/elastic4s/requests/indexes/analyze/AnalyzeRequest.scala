package com.sksamuel.elastic4s.requests.indexes.analyze

import com.sksamuel.elastic4s.analysis.{Analyzer, Tokenizer}

case class AnalyzeRequest(text: Seq[String],
                          analyzer: Option[String] = None,
                          explain:Boolean = false,
                          index: Option[String] = None,
                          tokenizer:Option[String] = None,
                          filters:Seq[String] = Seq.empty,
                          charFilters:Seq[String] = Seq.empty,
                          attributes:Seq[String] = Seq.empty,
                          normalizer:Option[String] = None,
                          field:Option[String] = None,
                          rawFiltersFromAnalyzer:Seq[Analyzer] = Seq.empty) {

  def analyzer(name: String): AnalyzeRequest = copy(analyzer = Some(name))

  def analyzer(analyzer:Analyzer) : AnalyzeRequest = copy(analyzer = Some(analyzer.name))

  def index(name: String): AnalyzeRequest = copy(index = Some(name))

  def explain(_explain: Boolean): AnalyzeRequest = copy(explain = _explain)

  def tokenizer(tk:String): AnalyzeRequest = copy(tokenizer = Some(tk))
  def tokenizer(tk:Tokenizer): AnalyzeRequest = copy(tokenizer = Some(tk.name))

  def filters(filter:String,other:String*): AnalyzeRequest = copy(filters = filter +: other)
  def filters(analyzer: Analyzer*): AnalyzeRequest = copy(rawFiltersFromAnalyzer = analyzer)

  def charFilters(charFilter:String,other:String*): AnalyzeRequest = copy(charFilters = charFilter +: other)

  def field(name:String): AnalyzeRequest = copy(field = Some(name))

  def normalizer(name:String):AnalyzeRequest = copy(normalizer = Some(name))

  def attributes(attrs:String*): AnalyzeRequest = copy(attributes = attrs)
}

object AnalyzeRequest {

  def text(content:String,other:String*): AnalyzeRequest = AnalyzeRequest(content +: other)


}
