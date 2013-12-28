package com.sksamuel.elastic4s

/** @author Stephen Samuel */

trait AttributeBoost {
  val _builder: {def setBoost(boost: Float): Any}

  def boost(boost: Double): this.type = {
    _builder.setBoost(boost.toFloat)
    this
  }
}

trait AttributePreference {
  val _builder: {def setPreference(preference: String): Any}

  def preference(preference: String): this.type = {
    _builder.setPreference(preference)
    this
  }
}

trait AttributeRouting {
  val _builder: {def setRouting(preference: String): Any}

  def routing(routing: String): this.type = {
    _builder.setRouting(routing)
    this
  }
}