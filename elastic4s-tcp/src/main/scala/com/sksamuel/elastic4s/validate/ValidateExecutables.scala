package com.sksamuel.elastic4s.validate

import com.sksamuel.elastic4s.Executable
import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.action.admin.indices.validate.query.ValidateQueryResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait ValidateExecutables {

  implicit object ValidateDefinitionExecutable
    extends Executable[ValidateDefinition, ValidateQueryResponse, ValidateQueryResponse] {
    override def apply(c: Client, v: ValidateDefinition): Future[ValidateQueryResponse] = {

      val builder = c.admin().indices().prepareValidateQuery(v.indexesAndTypes.indexes: _*)
        .setTypes(v.indexesAndTypes.types: _*)
        .setQuery(QueryBuilderFn(v.query))
      v.rewrite.foreach(builder.setRewrite)
      v.explain.foreach(builder.setExplain)

      injectFuture(builder.execute(_))
    }
  }
}
