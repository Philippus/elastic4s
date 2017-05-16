package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.script.{ScriptDefinition, ScriptType}
import org.elasticsearch.script.Script

import scala.collection.JavaConverters._

object ScriptBuilder {

  implicit def toESScriptType(scriptType: ScriptType): org.elasticsearch.script.ScriptType = scriptType match {
    case ScriptType.File => org.elasticsearch.script.ScriptType.FILE
    case ScriptType.Inline => org.elasticsearch.script.ScriptType.INLINE
    case ScriptType.Stored => org.elasticsearch.script.ScriptType.STORED
  }

  def apply(script: ScriptDefinition): Script = {
    var options = script.options.asJava
    if (script.scriptType != com.sksamuel.elastic4s.script.ScriptType.Inline) {
      options = null
    }
    if (script.params.isEmpty) {
      new Script(
        script.scriptType,
        script.lang.getOrElse(Script.DEFAULT_SCRIPT_LANG),
        script.script,
        options,
        new java.util.HashMap[String, Object]()
      )
    } else {
      val mappedParams = FieldsMapper.mapper(script.params).asJava
      new Script(
        script.scriptType,
        script.lang.getOrElse(Script.DEFAULT_SCRIPT_LANG),
        script.script,
        options,
        mappedParams
      )
    }
  }
}
