package com.sksamuel.elastic4s.requests.indexes

trait IndexTemplateApi {

  @deprecated("use deleteIndexTemplate(name)", "6.0.0")
  def deleteTemplate(name: String): DeleteIndexTemplateRequest = DeleteIndexTemplateRequest(name)

  def deleteIndexTemplate(name: String): DeleteIndexTemplateRequest = DeleteIndexTemplateRequest(name)

  def createIndexTemplate(name: String, pattern: String): CreateIndexTemplateRequest =
    CreateIndexTemplateRequest(name, pattern)

  @deprecated("use createIndexTemplate(name: String, pattern: String)", "6.0.0")
  def createTemplate(name: String, pattern: String): CreateIndexTemplateRequest = createIndexTemplate(name, pattern)
  @deprecated("use createIndexTemplate(name: String, pattern: String)", "6.0.0")
  def createTemplate(name: String) = new CreateIndexTemplateExpectsPattern(name)
  class CreateIndexTemplateExpectsPattern(name: String) {
    @deprecated("use createIndexTemplate(name: String, pattern: String)", "6.0.0")
    def pattern(pat: String) = CreateIndexTemplateRequest(name, pat)
  }

  @deprecated("use getIndexTemplate(name)", "6.0.0")
  def getTemplate(name: String): GetIndexTemplateRequest = GetIndexTemplateRequest(name)

  def getIndexTemplate(name: String): GetIndexTemplateRequest = GetIndexTemplateRequest(name)
}
