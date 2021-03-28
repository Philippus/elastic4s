package com.sksamuel.elastic4s.requests.script

import scala.language.implicitConversions

trait ScriptApi {

  @deprecated("Create a script using the case class constructor, eg Script(\"source\")")
  def script(source: String): Script       = Script(source)

  @deprecated("Create a script using the case class constructor, eg Script(\"source\")")
  def script(name: String, script: String): Script = Script(script)
}
