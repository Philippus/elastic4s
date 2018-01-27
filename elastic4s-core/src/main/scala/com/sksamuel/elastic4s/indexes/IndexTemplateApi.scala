package com.sksamuel.elastic4s.indexes

trait IndexTemplateApi {

  @deprecated("use deleteIndexTemplate(name)", "6.0.0")
  def deleteTemplate(name: String): DeleteIndexTemplateDefinition = DeleteIndexTemplateDefinition(name)

  def deleteIndexTemplate(name: String): DeleteIndexTemplateDefinition = DeleteIndexTemplateDefinition(name)

  def createIndexTemplate(name: String, pattern: String): CreateIndexTemplateDefinition =
    CreateIndexTemplateDefinition(name, pattern)

  @deprecated("use createIndexTemplate(name: String, pattern: String)", "6.0.0")
  def createTemplate(name: String, pattern: String): CreateIndexTemplateDefinition = createIndexTemplate(name, pattern)
  @deprecated("use createIndexTemplate(name: String, pattern: String)", "6.0.0")
  def createTemplate(name: String) = new CreateIndexTemplateExpectsPattern(name)
  class CreateIndexTemplateExpectsPattern(name: String) {
    @deprecated("use createIndexTemplate(name: String, pattern: String)", "6.0.0")
    def pattern(pat: String) = CreateIndexTemplateDefinition(name, pat)
  }

  @deprecated("use getIndexTemplate(name)", "6.0.0")
  def getTemplate(name: String): GetIndexTemplateDefinition = GetIndexTemplateDefinition(name)

  def getIndexTemplate(name: String): GetIndexTemplateDefinition = GetIndexTemplateDefinition(name)
}
