package com.sksamuel.elastic4s.requests.indexes

trait IndexTemplateApi {
  def deleteIndexTemplate(name: String): DeleteIndexTemplateRequest = DeleteIndexTemplateRequest(name)
  def createIndexTemplate(name: String, pattern: String): CreateIndexTemplateRequest =
    CreateIndexTemplateRequest(name, pattern)
  def getIndexTemplate(name: String): GetIndexTemplateRequest = GetIndexTemplateRequest(name)
}
