package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.ScriptBuilder
import org.elasticsearch.index.query.{QueryBuilders, ScriptQueryBuilder}

object ScriptQueryBuilder {
  def apply(q: ScriptQueryDefinition): ScriptQueryBuilder = {
    val builder = QueryBuilders.scriptQuery(ScriptBuilder(q.script))
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.queryName.foreach(builder.queryName)
    builder
  }
}
