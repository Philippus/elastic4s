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
    def preference(pref: Preference): this.type = preference(pref.elastic)
  }

  trait DefinitionAttributeRouting {
    val _builder: {def setRouting(preference: String): Any}

    def routing(routing: String): this.type = {
      _builder.setRouting(routing)
      this
    }
  }

  trait DefinitionAttributeRefresh {
    val _builder: {def setRefresh(refresh: Boolean): Any}

    def refresh(refresh: Boolean): this.type = {
      _builder.setRefresh(refresh)
      this
    }
  }

  trait DefinitionAttributeRealtime {
    val _builder: {def setRealtime(realtime: Boolean): Any}

    def realtime(realtime: Boolean): this.type = {
      _builder.setRealtime(realtime)
      this
    }
  }
}