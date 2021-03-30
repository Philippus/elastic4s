package com.sksamuel.elastic4s.requests.termvectors

case class MultiTermVectorsRequest(termVectorsRequests: Seq[TermVectorsRequest],
                                   realtime: Option[Boolean] = None) {

  def realtime(boolean: Boolean): MultiTermVectorsRequest = copy(realtime = Option(boolean))
}
