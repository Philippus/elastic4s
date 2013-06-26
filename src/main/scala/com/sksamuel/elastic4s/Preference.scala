package com.sksamuel.elastic4s

/** @author Stephen Samuel */
sealed abstract class Preference
object Preference {
    case object Primary extends Preference
    case object PrimaryFirst extends Preference
    case object Local extends Preference
    case class OnlyNode(id: String) extends Preference
    case class PreferNode(id: String) extends Preference
    case class Shards(ids: String*) extends Preference
    case class Custom(value: String) extends Preference
}