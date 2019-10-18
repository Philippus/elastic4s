package com.sksamuel.elastic4s.requests.security.roles.admin

case class ClearRolesCacheResponse(
	_nodes: NodesAcknowledgement,
	cluster_name: String,
	nodes: Map[String,NodeNameInfo]
)

case class NodesAcknowledgement(total: Int, successful: Int, failed: Int)

case class NodeNameInfo(name: String)