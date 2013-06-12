package com.sksamuel.elastic4s

/** @author Stephen Samuel */
sealed trait Analyzer
object Analyzer {
    case object NotAnalyzed extends Analyzer
    case object WhitespaceAnalyzer extends Analyzer
    case object StandardAnalyzer extends Analyzer
    case object SimpleAnalyzer extends Analyzer
    case object StopAnalyzer extends Analyzer
    case object KeywordAnalyzer extends Analyzer
    case object PatternAnalyzer extends Analyzer
    case object SnowballAnalyzer extends Analyzer
}