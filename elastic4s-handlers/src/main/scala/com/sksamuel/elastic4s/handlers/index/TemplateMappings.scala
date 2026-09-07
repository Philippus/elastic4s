package com.sksamuel.elastic4s.handlers.index

import com.sksamuel.elastic4s.fields.ElasticField
import com.sksamuel.elastic4s.requests.mappings.dynamictemplate.DynamicTemplateRequest

case class TemplateMappings(
    dynamicTemplates: Seq[DynamicTemplateRequest] = Seq.empty,
    properties: Seq[ElasticField] = Seq.empty
)
