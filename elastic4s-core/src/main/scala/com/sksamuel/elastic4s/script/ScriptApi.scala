package com.sksamuel.elastic4s.script

import scala.language.implicitConversions

trait ScriptApi {
  def script(script: String): Script = Script(script)
  def script(name: String, script: String)     = Script(script)
}
