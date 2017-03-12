package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.indexes.{AnalysisContentBuilder, CreateIndexTemplateDefinition}
import com.sksamuel.elastic4s.mappings.MappingContentBuilder
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequestBuilder
import org.elasticsearch.common.io.stream.BytesStreamOutput
import org.elasticsearch.common.xcontent.XContentFactory

import scala.collection.JavaConverters._

object CreateIndexTemplateBuilder {
  def apply(builder: PutIndexTemplateRequestBuilder, req: CreateIndexTemplateDefinition): Unit = {

    builder.setTemplate(req.pattern)
    req.order.foreach(builder.setOrder)
    req.create.foreach(builder.setCreate)
    req.aliases.foreach(builder.addAlias)

    req.mappings.foreach { mapping =>
      builder.addMapping(mapping.`type`, MappingContentBuilder.buildWithName(mapping, mapping.`type`))
    }

    if (!req.settings.isEmpty || req.analysis.nonEmpty) {
      val source = XContentFactory.jsonBuilder().startObject()
      req.settings.getAsMap.asScala.foreach { p => source.field(p._1, p._2) }
      req.analysis.foreach(AnalysisContentBuilder.build(_, source))
      source.endObject()
      builder.setSettings(source.string())
    }

    val output = new BytesStreamOutput()
    builder.request().writeTo(output)
  }
}
