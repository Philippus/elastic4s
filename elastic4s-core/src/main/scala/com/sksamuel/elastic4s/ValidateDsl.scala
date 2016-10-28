package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.query.QueryStringQueryDefinition
import org.elasticsearch.action.admin.indices.validate.query.{ValidateQueryAction, ValidateQueryRequestBuilder, ValidateQueryResponse}
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait ValidateDsl {

  def validateIn(indexType: IndexAndTypes): ValidateDefinition =
    ValidateDefinition(indexType.index, indexType.types.head)

  def validateIn(value: String): ValidateDefinition = validate in value

  implicit object ValidateDefinitionExecutable
    extends Executable[ValidateDefinition, ValidateQueryResponse, ValidateQueryResponse] {
    override def apply(c: Client, t: ValidateDefinition): Future[ValidateQueryResponse] = {
      injectFuture(c.admin.indices.validateQuery(t.build, _))
    }
  }

  implicit object ValidateDefinitionShow extends Show[ValidateDefinition] {
    override def show(f: ValidateDefinition): String = {
      Option(f.q).fold("")(q => q.builder.toString)
    }
  }

  implicit class ValidateDefinitionShowOps(f: ValidateDefinition) {
    def show: String = ValidateDefinitionShow.show(f)
  }
}

case class ValidateDefinition(index: String, `type`: String) {

  private[elastic4s] var q: QueryDefinition = _

  val _builder = {
    new ValidateQueryRequestBuilder(ProxyClients.indices, ValidateQueryAction.INSTANCE)
      .setIndices(index)
      .setTypes(`type`)
  }

  def build = _builder.request

  /**
   * Adds a single string query to this search
   *
   * @param string the query string
   *
   * @return this
   */
  def query(string: String): this.type = {
    this.q = QueryStringQueryDefinition(string)
    _builder.setQuery(q.builder)
    this
  }

  def rewrite(rewrite: Boolean): this.type = {
    _builder.setRewrite(rewrite)
    this
  }

  def query(block: => QueryDefinition): this.type = {
    this.q = block
    _builder.setQuery(q.builder)
    this
  }

  def explain(ex: Boolean): this.type = {
    _builder.setExplain(ex)
    this
  }
}
