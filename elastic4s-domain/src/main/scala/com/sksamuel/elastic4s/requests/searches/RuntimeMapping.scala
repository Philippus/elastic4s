package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.requests.script.Script

case class RuntimeMapping(field: String, `type`: String, script: Option[Script] = None)

object RuntimeMapping {
  def apply(field: String, `type`: String, script: Script): RuntimeMapping =
    RuntimeMapping(field, `type`, Some(script))

  def apply(field: String, `type`: String, scriptSource: String): RuntimeMapping =
    RuntimeMapping(field, `type`, Some(Script(scriptSource)))
}
