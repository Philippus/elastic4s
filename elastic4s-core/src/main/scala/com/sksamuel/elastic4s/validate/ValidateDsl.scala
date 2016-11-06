package com.sksamuel.elastic4s.validate

import com.sksamuel.elastic4s.searches.QueryDefinition
import com.sksamuel.elastic4s.{Executable, IndexesAndTypes, Show}
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait ValidateDsl {

  def validateIn(indexesAndTypes: IndexesAndTypes): ValidateExpectsQuery = new ValidateExpectsQuery(indexesAndTypes)
  class ValidateExpectsQuery(indexesAndTypes: IndexesAndTypes) {
    def query(query: QueryDefinition): ValidateDefinition = ValidateDefinition(indexesAndTypes, query.builder)
  }

  implicit object ValidateDefinitionExecutable
    extends Executable[ValidateDefinition, ValidateQueryResponse, ValidateQueryResponse] {
    override def apply(c: Client, v: ValidateDefinition): Future[ValidateQueryResponse] = {
      val f = c.admin().indices().validateQuery(v.builder.request(), _: ActionListener[ValidateQueryResponse])
      injectFuture(f)
    }
  }

  implicit object ValidateDefinitionShow extends Show[ValidateDefinition] {
    override def show(v: ValidateDefinition): String = {
      v.builder.request().toString
    }
  }

  implicit class ValidateDefinitionShowOps(f: ValidateDefinition) {
    def show: String = ValidateDefinitionShow.show(f)
  }
}
