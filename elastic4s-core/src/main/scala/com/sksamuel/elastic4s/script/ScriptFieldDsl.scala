package com.sksamuel.elastic4s.script

trait ScriptFieldDsl {
  case class ExpectsScript(field: String) {
    def script(script: String): ScriptFieldDefinition = ScriptFieldDefinition(field, script, None, None)
  }
}


