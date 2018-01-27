package com.sksamuel.elastic4s.script

import scala.language.implicitConversions

case class ScriptDefinition(script: String,
                            lang: Option[String] = None,
                            scriptType: ScriptType = ScriptType.Source,
                            params: Map[String, Any] = Map.empty,
                            options: Map[String, String] = Map.empty) {

  def lang(lang: String): ScriptDefinition                                    = copy(lang = Option(lang))
  def param(name: String, value: Any): ScriptDefinition                       = copy(params = params + (name -> value))
  def params(first: (String, Any), rest: (String, AnyRef)*): ScriptDefinition = params(first +: rest)
  def params(seq: Seq[(String, Any)]): ScriptDefinition                       = params(seq.toMap)
  def params(map: Map[String, Any]): ScriptDefinition                         = copy(params = params ++ map)

  def scriptType(tpe: String): ScriptDefinition            = copy(scriptType = ScriptType.valueOf(tpe))
  def scriptType(scriptType: ScriptType): ScriptDefinition = copy(scriptType = scriptType)
}

object ScriptDefinition {
  implicit def string2Script(script: String): ScriptDefinition = ScriptDefinition(script)
}
