package com.sksamuel.elastic4s.requests.script

import scala.language.implicitConversions

case class Script(script: String,
                  lang: Option[String] = None,
                  scriptType: ScriptType = ScriptType.Source,
                  params: Map[String, Any] = Map.empty,
                  options: Map[String, String] = Map.empty) {

  def lang(lang: String): Script                                    = copy(lang = Option(lang))
  def param(name: String, value: Any): Script                       = copy(params = params + (name -> value))
  def params(first: (String, Any), rest: (String, AnyRef)*): Script = params(first +: rest)
  def params(seq: Seq[(String, Any)]): Script                       = params(seq.toMap)
  def params(map: Map[String, Any]): Script                         = copy(params = params ++ map)

  def scriptType(tpe: String): Script            = copy(scriptType = ScriptType.valueOf(tpe))
  def scriptType(scriptType: ScriptType): Script = copy(scriptType = scriptType)
}

object Script {
  implicit def string2Script(script: String): Script = Script(script)
}
