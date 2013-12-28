package com.sksamuel.elastic4s

/** @author Stephen Samuel */
object DefinitionAttributes {

  trait DefinitionAttributeBoost {
    val _builder: {def setBoost(boost: Float): Any}

    def boost(boost: Double): this.type = {
      _builder.setBoost(boost.toFloat)
      this
    }
  }

  trait DefinitionAttributePreference {
    val _builder: {def setPreference(preference: String): Any}

    def preference(preference: String): this.type = {
      _builder.setPreference(preference)
      this
    }
  }

  trait DefinitionAttributeRouting {
    val _builder: {def setRouting(preference: String): Any}

    def routing(routing: String): this.type = {
      _builder.setRouting(routing)
      this
    }
  }
}