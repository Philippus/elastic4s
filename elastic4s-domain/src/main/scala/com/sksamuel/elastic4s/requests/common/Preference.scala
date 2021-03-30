package com.sksamuel.elastic4s.requests.common

abstract class Preference(val value: String)
object Preference {
  case class PreferNode(id: String)             extends Preference("_prefer_node:" + id)
  case class PreferNodes(ids: List[String])     extends Preference("_prefer_nodes:" + ids.mkString(","))
  case object Local                             extends Preference("_local")
  case object OnlyLocal                         extends Preference("_only_local")
  case class OnlyNode(id: String)               extends Preference("_only_node:" + id)
  case class OnlyNodes(ids: List[String])       extends Preference("_only_nodes:" + ids.mkString(","))
  case class Shards(ids: List[String])          extends Preference("_shards:" + ids.mkString(","))
  case class Custom(override val value: String) extends Preference(value)
}
