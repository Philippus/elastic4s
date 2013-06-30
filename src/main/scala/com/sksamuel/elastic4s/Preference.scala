package com.sksamuel.elastic4s

/** @author Stephen Samuel */
sealed abstract class Preference(val elastic: String)
object Preference {
    case object Primary extends Preference("_primary")
    case object PrimaryFirst extends Preference("_primary_first")
    case object Local extends Preference("_local")
    case class PreferNode(id: String) extends Preference("_prefer_node:" + id)
    case class OnlyNode(id: String) extends Preference("_only_node:" + id)
    case class Shards(ids: String*) extends Preference("_shards:" + ids.mkString(","))
    case class Custom(value: String) extends Preference(value)
}