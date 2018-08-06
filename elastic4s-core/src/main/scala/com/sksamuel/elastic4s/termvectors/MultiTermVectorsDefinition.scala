package com.sksamuel.elastic4s.termvectors

case class MultiTermVectorsDefinition(termVectorsDefinitions: Seq[TermVectorsDefinition],
                                      realtime: Option[Boolean] = None) {

  def realtime(boolean: Boolean): MultiTermVectorsDefinition = copy(realtime = Option(boolean))
}
