package com.sksamuel.elastic4s.script

import scala.language.implicitConversions

trait ScriptDsl {
  def script(script: String): ScriptDefinition = ScriptDefinition(script)
  def script(name: String, script: String) = ScriptDefinition(script)
}
