package com.sksamuel.elastic4s.definitions

import scala.language.reflectiveCalls

trait DefinitionRouting {
  protected val _builder: {def setRouting(routing: String): Any}

  def routing(routing: String): this.type = {
    _builder.setRouting(routing)
    this
  }
}

trait DefinitionVersion {
  protected val _builder: {def setVersion(version: Long): Any}

  def version(version: Long): this.type = {
    _builder.setVersion(version)
    this
  }
}

trait DefinitionPreference {
  protected val _builder: {def setPreference(pref: String): Any}

  def preference(pref: String): this.type = {
    _builder.setPreference(pref)
    this
  }
}

trait DefinitionMinScore {
  protected val _builder: {def setMinScore(minScore: Float): Any}

  def minScore(minScore: Double): this.type = {
    _builder.setMinScore(minScore.toFloat)
    this
  }
}

trait DefinitionTerminateAfter {
  protected val _builder: {def setTerminateAfter(termAfter: Int): Any}

  /** The maximum count for each shard, upon reaching which the query execution will terminate early.
    * If set, the response will have a boolean field terminated_early to indicate whether the query execution
    * has actually terminated_early. Defaults to no terminate_after.
    */
  def terminateAfter(termAfter: Int): this.type = {
    _builder.setTerminateAfter(termAfter)
    this
  }
}
