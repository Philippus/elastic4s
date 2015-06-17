package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait ValidateDsl {

  implicit object ValidateDefinitionExecutable
    extends Executable[ValidateDefinition, ValidateQueryResponse, ValidateQueryResponse] {
    override def apply(c: Client, t: ValidateDefinition): Future[ValidateQueryResponse] = {
      injectFuture(c.admin.indices.validateQuery(t.build, _))
    }
  }
}
