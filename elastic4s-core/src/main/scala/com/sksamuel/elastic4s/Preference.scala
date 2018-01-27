package com.sksamuel.elastic4s

abstract class Preference(val value: String)
object Preference {
  case class PreferNode(id: String)             extends Preference("_prefer_node:" + id)
  case object Local                             extends Preference("_local")
  case object OnlyLocal                         extends Preference("_only_local")
  case class OnlyNode(id: String)               extends Preference("_only_node:" + id)
  case class OnlyNodes(attr: String)            extends Preference("_only_nodes:" + attr)
  case class Shards(ids: String*)               extends Preference("_shards:" + ids.mkString(","))
  case class Custom(override val value: String) extends Preference(value)
  case object Primary                           extends Preference("_primary")
  case object PrimaryFirst                      extends Preference("_primary_first")
  case object Replica                           extends Preference("_replica")
  case object ReplicaFirst                      extends Preference("_replica_first")
}
