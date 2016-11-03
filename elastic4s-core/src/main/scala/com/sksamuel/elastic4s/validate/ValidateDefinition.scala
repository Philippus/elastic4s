package com.sksamuel.elastic4s.validate

import com.sksamuel.elastic4s.{IndexesAndTypes, ProxyClients}
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.action.admin.indices.validate.query.{ValidateQueryAction, ValidateQueryRequestBuilder}
import org.elasticsearch.index.query.QueryBuilder

case class ValidateDefinition(indexesAndTypes: IndexesAndTypes,
                              query: QueryBuilder,
                              rewrite: Option[Boolean] = None,
                              explain: Option[Boolean] = None) {
  require(indexesAndTypes != null, "value must not be null or empty")

  def builder = {
    val builder = new ValidateQueryRequestBuilder(ProxyClients.indices, ValidateQueryAction.INSTANCE)
      .setIndices(indexesAndTypes.indexes: _*)
      .setTypes(indexesAndTypes.types: _*)
      .setQuery(query)
    rewrite.foreach(builder.setRewrite)
    explain.foreach(builder.setExplain)
    builder
  }

  def rewrite(rewrite: Boolean): ValidateDefinition = copy(rewrite = rewrite.some)
  def explain(explain: Boolean): ValidateDefinition = copy(explain = explain.some)
}
