package com.sksamuel.elastic4s.anaylzers

trait AnalyzerDsl {
  def stopAnalyzer(name: String): StopAnalyzerDefinition = StopAnalyzerDefinition(name)
}
