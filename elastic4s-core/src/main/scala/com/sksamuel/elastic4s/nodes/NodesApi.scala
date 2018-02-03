package com.sksamuel.elastic4s.nodes

trait NodesApi {

  def nodeInfo(names: Iterable[String]) = NodeInfoRequest(names.toSeq)
  def nodeInfo(names: String*)          = NodeInfoRequest(names)

  def nodeStats(): NodeStatsDefinition                             = NodeStatsDefinition(Seq.empty)
  def nodeStats(first: String, rest: String*): NodeStatsDefinition = nodeStats(first +: rest)
  def nodeStats(nodes: Iterable[String]): NodeStatsDefinition      = NodeStatsDefinition(nodes.toSeq)

}

case class NodeStatsDefinition(nodes: Seq[String], stats: Seq[String] = Seq.empty) {
  def stats(stats: Seq[String]): NodeStatsDefinition = copy(stats = stats)
}

case class NodeInfoRequest(nodes: Seq[String])
