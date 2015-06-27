package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.validate.query.{ValidateQueryRequestBuilder, ValidateQueryResponse}
import org.elasticsearch.client.Client
import org.elasticsearch.common.xcontent.XContentHelper

import scala.concurrent.Future

trait ValidateDsl {

  implicit object ValidateDefinitionExecutable
    extends Executable[ValidateDefinition, ValidateQueryResponse, ValidateQueryResponse] {
    override def apply(c: Client, t: ValidateDefinition): Future[ValidateQueryResponse] = {
      injectFuture(c.admin.indices.validateQuery(t.build, _))
    }
  }

  implicit object ValidateDefinitionShow extends Show[ValidateDefinition] {
    override def show(f: ValidateDefinition): String = XContentHelper
      .convertToJson(f._builder.request.source, true, true)
  }

  implicit class ValidateDefinitionShowOps(f: ValidateDefinition) {
    def show: String = ValidateDefinitionShow.show(f)
  }
}

class ValidateDefinition(index: String, `type`: String) {

  val _builder = new ValidateQueryRequestBuilder(ProxyClients.indices).setIndices(index).setTypes(`type`)
  def build = _builder.request

  /**
   * Adds a single string query to this search
   *
   * @param string the query string
   *
   * @return this
   */
  def query(string: String): this.type = {
    val q = new QueryStringQueryDefinition(string)
    _builder.setSource(q.builder.buildAsBytes)
    this
  }

  def rewrite(rewrite: Boolean): this.type = {
    _builder.setRewrite(rewrite)
    this
  }

  def query(block: => QueryDefinition): this.type = {
    _builder.setSource(block.builder.buildAsBytes)
    this
  }

  def explain(ex: Boolean): this.type = {
    _builder.setExplain(ex)
    this
  }
}