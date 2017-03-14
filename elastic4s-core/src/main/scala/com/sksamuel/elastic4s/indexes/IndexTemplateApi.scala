package com.sksamuel.elastic4s.indexes

trait IndexTemplateApi {

  def deleteTemplate(name: String): DeleteIndexTemplateDefinition = DeleteIndexTemplateDefinition(name)

  def createTemplate(name: String, pattern: String): CreateIndexTemplateDefinition =
    CreateIndexTemplateDefinition(name, pattern)

  def createTemplate(name: String) = new CreateIndexTemplateExpectsPattern(name)
  class CreateIndexTemplateExpectsPattern(name: String) {
    def pattern(pat: String) = CreateIndexTemplateDefinition(name, pat)
  }

  def getTemplate(name: String): GetIndexTemplateDefinition = GetIndexTemplateDefinition(name)
}
