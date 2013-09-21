package com.sksamuel.elastic4s

import org.elasticsearch.common.xcontent.{XContentFactory, XContentBuilder}

/** @author Stephen Samuel */
trait Analyzer {
  def elastic: String
}
abstract class ElasticAnalyzer(string: String) extends Analyzer {
  def elastic = string
}
class CustomAnalyzer(string: String) extends Analyzer {
  def elastic = XContentFactory.jsonBuilder().value(string).string()
}
object Analyzer {
  case object NotAnalyzed extends ElasticAnalyzer("notindexed")
  case object WhitespaceAnalyzer extends ElasticAnalyzer("whitespace")
  case object StandardAnalyzer extends ElasticAnalyzer("standard")
  case object SimpleAnalyzer extends ElasticAnalyzer("simple")
  case object StopAnalyzer extends ElasticAnalyzer("stop")
  case object KeywordAnalyzer extends ElasticAnalyzer("keyword")
  case object PatternAnalyzer extends ElasticAnalyzer("pattern")
  case object SnowballAnalyzer extends ElasticAnalyzer("snowball")

  //language analyzers
  case object ArabicAnalyzer extends ElasticAnalyzer("arabic")
  case object ArmenianAnalyzer extends ElasticAnalyzer("armenian")
  case object BasqueAnalyzer extends ElasticAnalyzer("basque")
  case object BrazilianAnalyzer extends ElasticAnalyzer("brazilian")
  case object BulgarianAnalyzer extends ElasticAnalyzer("bulgarian")
  case object CatalanAnalyzer extends ElasticAnalyzer("catalan")
  case object ChineseAnalyzer extends ElasticAnalyzer("chinese")
  case object CjkAnalyzer extends ElasticAnalyzer("cjk")
  case object CzechAnalyzer extends ElasticAnalyzer("czech")
  case object DanishAnalyzer extends ElasticAnalyzer("danish")
  case object DutchAnalyzer extends ElasticAnalyzer("dutch")
  case object EnglishAnalyzer extends ElasticAnalyzer("english")
  case object FinnishAnalyzer extends ElasticAnalyzer("finnish")
  case object FrenchAnalyzer extends ElasticAnalyzer("french")
  case object GalicianAnalyzer extends ElasticAnalyzer("galician")
  case object GermanAnalyzer extends ElasticAnalyzer("german")
  case object GreekAnalyzer extends ElasticAnalyzer("greek")
  case object HindiAnalyzer extends ElasticAnalyzer("hindi")
  case object HungarianAnalyzer extends ElasticAnalyzer("hungarian")
  case object IndonesianAnalyzer extends ElasticAnalyzer("indonesian")
  case object ItalianAnalyzer extends ElasticAnalyzer("italian")
  case object LatvianAnalyzer extends ElasticAnalyzer("latvian")
  case object NorwegianAnalyzer extends ElasticAnalyzer("norwegian")
  case object PersianAnalyzer extends ElasticAnalyzer("persian")
  case object PortugueseAnalyzer extends ElasticAnalyzer("portuguese")
  case object RomanianAnalyzer extends ElasticAnalyzer("romanian")
  case object RussianAnalyzer extends ElasticAnalyzer("russian")
  case object SpanishAnalyzer extends ElasticAnalyzer("spanish")
  case object SwedishAnalyzer extends ElasticAnalyzer("swedish")
  case object TurkishAnalyzer extends ElasticAnalyzer("turkish")
  case object ThaiAnalyzer extends ElasticAnalyzer("thai")
}