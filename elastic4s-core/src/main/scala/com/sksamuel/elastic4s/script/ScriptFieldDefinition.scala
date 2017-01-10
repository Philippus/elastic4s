package com.sksamuel.elastic4s.script

case class ScriptFieldDefinition(field: String,
                                 script: String,
                                 language: Option[String] = None,
                                 parameters: Option[Map[String, AnyRef]] = None,
                                 options: Option[Map[String, String]] = None,
                                 scriptType: String = ScriptType.Inline) {

  def lang(l: String): ScriptFieldDefinition = copy(language = Option(l))

  def params(p: Map[String, Any]): ScriptFieldDefinition = {
    copy(parameters = Some(p.map(e => e._1 -> e._2.asInstanceOf[AnyRef])))
  }

  def params(ps: (String, Any)*): ScriptFieldDefinition = {
    copy(parameters = Some(ps.toMap.map(e => e._1 -> e._2.asInstanceOf[AnyRef])))
  }

  def scriptType(scriptType: String): ScriptFieldDefinition = copy(scriptType = scriptType)
}

object ScriptType {
  val Inline = "inline"
  val Stored = "stored"
  val File = "file"
}
