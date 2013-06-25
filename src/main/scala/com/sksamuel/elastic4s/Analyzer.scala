package com.sksamuel.elastic4s

/** @author Stephen Samuel */
abstract class Analyzer(val elastic: String)
object Analyzer {
    case object NotAnalyzed extends Analyzer("notindexed")
    case object WhitespaceAnalyzer extends Analyzer("whitespace")
    case object StandardAnalyzer extends Analyzer("standard")
    case object SimpleAnalyzer extends Analyzer("simple")
    case object StopAnalyzer extends Analyzer("stop")
    case object KeywordAnalyzer extends Analyzer("keyword")
    case object PatternAnalyzer extends Analyzer("pattern")
    case object SnowballAnalyzer extends Analyzer("snowball")
}