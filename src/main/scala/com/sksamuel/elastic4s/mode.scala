package com.sksamuel.elastic4s

/** @author Stephen Samuel */
sealed trait MultiMode
case object MultiMode {
    case object Min extends MultiMode
    case object Max extends MultiMode
    case object Sum extends MultiMode
    case object Avg extends MultiMode
}
