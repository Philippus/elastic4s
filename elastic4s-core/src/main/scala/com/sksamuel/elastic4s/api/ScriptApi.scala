package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.script.Script

trait ScriptApi {

  @deprecated("Create a script using the case class constructor, eg Script(\"source\")")
  def script(source: String): Script = Script(source)

  @deprecated("Create a script using the case class constructor, eg Script(\"source\")")
  def script(name: String, script: String): Script = Script(script)
}
