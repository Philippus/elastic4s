package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.indexes.{CreateIndexTemplateRequest, DeleteIndexTemplateRequest, GetIndexTemplateRequest}

trait IndexTemplateApi {
  def deleteIndexTemplate(name: String): DeleteIndexTemplateRequest = DeleteIndexTemplateRequest(name)
  def createIndexTemplate(name: String, pattern: Seq[String]): CreateIndexTemplateRequest =
    CreateIndexTemplateRequest(name, pattern)
  def createIndexTemplate(name: String, pattern: String): CreateIndexTemplateRequest =
    CreateIndexTemplateRequest(name, Seq(pattern))
  def getIndexTemplate(name: String): GetIndexTemplateRequest = GetIndexTemplateRequest(name)
}
