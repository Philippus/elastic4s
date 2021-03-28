package com.sksamuel.elastic4s.requests.nodes

case class NodeStatsRequest(nodes: Seq[String], stats: Seq[String] = Seq.empty) {
  def stats(stats: Seq[String]): NodeStatsRequest = copy(stats = stats)
}
