package com.sksamuel.elastic4s.requests.script

import scala.language.implicitConversions

trait ScriptApi {
  def script(source: String): Script       = Script(source)
  def script(name: String, script: String) = Script(script)
}
