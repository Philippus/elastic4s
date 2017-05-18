package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.indexes.{AnalysisContentBuilder, CreateIndexTemplateDefinition}
import com.sksamuel.elastic4s.json.XContentFactory
import com.sksamuel.elastic4s.mappings.MappingContentBuilder
import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.action.admin.indices.alias.Alias
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequestBuilder
import org.elasticsearch.common.io.stream.BytesStreamOutput
import org.elasticsearch.common.xcontent.XContentType

object CreateIndexTemplateBuilder {
  def apply(builder: PutIndexTemplateRequestBuilder, req: CreateIndexTemplateDefinition): Unit = {

    builder.setTemplate(req.pattern)
    req.order.foreach(builder.setOrder)
    req.create.foreach(builder.setCreate)

    req.aliases.foreach { a =>
      val alias = new Alias(a.name)
      a.filter.map(QueryBuilderFn.apply).foreach(alias.filter)
      a.routing.foreach(alias.routing)
      builder.addAlias(alias)
    }

    req.mappings.foreach { mapping =>
      builder.addMapping(mapping.`type`, MappingContentBuilder.buildWithName(mapping, mapping.`type`))
    }

    if (req.settings.nonEmpty || req.analysis.nonEmpty) {
      val source = XContentFactory.jsonBuilder()
      req.settings.foreach { p => source.field(p._1, p._2.toString) }
      req.analysis.foreach(AnalysisContentBuilder.build(_, source))
      source.endObject()
      builder.setSettings(source.string(), XContentType.JSON)
    }

    val output = new BytesStreamOutput()
    builder.request().writeTo(output)
  }
}
