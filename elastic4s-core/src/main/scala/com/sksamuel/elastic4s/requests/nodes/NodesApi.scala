package com.sksamuel.elastic4s.requests.nodes

trait NodesApi {

  def nodeInfo(names: Iterable[String]) = NodeInfoRequest(names.toSeq)
  def nodeInfo(names: String*)          = NodeInfoRequest(names)

  def nodeStats(): NodeStatsRequest                             = NodeStatsRequest(Seq.empty)
  def nodeStats(first: String, rest: String*): NodeStatsRequest = nodeStats(first +: rest)
  def nodeStats(nodes: Iterable[String]): NodeStatsRequest      = NodeStatsRequest(nodes.toSeq)

}

case class NodeStatsRequest(nodes: Seq[String], stats: Seq[String] = Seq.empty) {
  def stats(stats: Seq[String]): NodeStatsRequest = copy(stats = stats)
}

case class NodeInfoRequest(nodes: Seq[String])
