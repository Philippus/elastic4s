package com.sksamuel.elastic4s.validate

import com.sksamuel.elastic4s.{Executable, Show}
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait ValidateExecutables {

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
