package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.nodes.{NodeInfoRequest, NodeStatsRequest}

trait NodesApi {

  def nodeInfo(names: Iterable[String]) = NodeInfoRequest(names.toSeq)
  def nodeInfo(names: String*) = NodeInfoRequest(names)

  def nodeStats(): NodeStatsRequest = NodeStatsRequest(Seq.empty)
  def nodeStats(first: String, rest: String*): NodeStatsRequest = nodeStats(first +: rest)
  def nodeStats(nodes: Iterable[String]): NodeStatsRequest = NodeStatsRequest(nodes.toSeq)

}
