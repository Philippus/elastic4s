package com.sksamuel.elastic4s

/** @author Dmitry Gorbunov */
abstract class ScriptType(val elasticType: org.elasticsearch.script.ScriptService.ScriptType)
case object ScriptType {
  case object Inline extends ScriptType(org.elasticsearch.script.ScriptService.ScriptType.INLINE)
  case object File extends ScriptType(org.elasticsearch.script.ScriptService.ScriptType.FILE)
}
