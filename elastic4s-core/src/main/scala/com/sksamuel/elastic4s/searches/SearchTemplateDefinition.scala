//package com.sksamuel.elastic4s.searches
//
//import com.sksamuel.elastic4s.script.ScriptDefinition
//import org.elasticsearch.script.mustache.SearchTemplateRequestBuilder
//import com.sksamuel.exts.OptionImplicits._
//import org.elasticsearch.script.ScriptService.ScriptType
//
//import scala.collection.JavaConverters._
//
//case class SearchTemplateDefinition(search: SearchDefinition,
//                                    params: Map[String, AnyRef] = Map.empty,
//                                    simulate: Option[Boolean] = None) {
//
//  def populate(builder: SearchTemplateRequestBuilder): Unit = {
//    builder.setRequest(search.build)
//    val script = ScriptDefinition(""" { "query": { "match" : { "name" : "Scheele" } } } """).lang("mustache").build
//    builder.setScript(script.toString)
//    builder.setScriptType(ScriptType.INLINE)
//    if (params.nonEmpty)
//      builder.setScriptParams(params.asJava)
//    simulate.foreach(builder.setSimulate)
//  }
//
//  def params(map: Map[String, AnyRef]): SearchTemplateDefinition = copy(params = params)
//  def simulate(simulate: Boolean): SearchTemplateDefinition = copy(simulate = simulate.some)
//}