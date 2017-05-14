package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.script.Script
import org.elasticsearch.script.ScriptType

import scala.collection.JavaConverters._

object ScriptBuilder {
  def apply(script: ScriptDefinition): Script = {
    var options = script.options.asJava
    if (script.scriptType != ScriptType.INLINE){
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
