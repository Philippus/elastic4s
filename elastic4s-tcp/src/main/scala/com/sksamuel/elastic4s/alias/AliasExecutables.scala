package com.sksamuel.elastic4s.alias

import com.sksamuel.elastic4s.Executable
import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest.AliasActions
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesResponse
import org.elasticsearch.action.admin.indices.alias.{IndicesAliasesRequest, IndicesAliasesResponse}
import org.elasticsearch.client.Client

import scala.concurrent.Future
import scala.language.implicitConversions

object AliasActionBuilders {

  def remove(t: RemoveAliasActionDefinition): IndicesAliasesRequest.AliasActions = {
    val action = AliasActions.remove().alias(t.alias).index(t.index)
    t.routing.foreach(action.routing)
    t.indexRouting.foreach(action.indexRouting)
    t.searchRouting.foreach(action.searchRouting)
    t.filter.map(QueryBuilderFn.apply).foreach(action.filter)
    action
  }

  def add(t: AddAliasActionDefinition): IndicesAliasesRequest.AliasActions = {
    val action = AliasActions.add().alias(t.alias).index(t.index)
    t.routing.foreach(action.routing)
    t.indexRouting.foreach(action.indexRouting)
    t.searchRouting.foreach(action.searchRouting)
    t.filter.map(QueryBuilderFn.apply).foreach(action.filter)
    action
  }
}

trait AliasExecutables {

  implicit object GetAliasDefinitionExecutable
    extends Executable[GetAliasDefinition, GetAliasesResponse, GetAliasesResponse] {
    override def apply(c: Client, t: GetAliasDefinition): Future[GetAliasesResponse] = {
      val _builder = c.admin().indices().prepareGetAliases(t.aliases: _*).addIndices(t.indices: _*)
      injectFuture(_builder.execute(_))
    }
  }

  // executable for a bulk alias definition
  implicit object IndicesAliasesRequestDefinitionExecutable
    extends Executable[IndicesAliasesRequestDefinition, IndicesAliasesResponse, IndicesAliasesResponse] {

    override def apply(c: Client, t: IndicesAliasesRequestDefinition): Future[IndicesAliasesResponse] = {
      val _builder = c.admin().indices().prepareAliases()
      t.actions.map {
        case remove: RemoveAliasActionDefinition => AliasActionBuilders.remove(remove)
        case add: AddAliasActionDefinition => AliasActionBuilders.add(add)
      }.foreach { action =>
        _builder.addAliasAction(action)
      }
      injectFuture(_builder.execute(_))
    }
  }

  // executable so we can use addAlias directly
  implicit object AddAliasActionDefinitionExecutable
    extends Executable[AddAliasActionDefinition, IndicesAliasesResponse, IndicesAliasesResponse] {
    override def apply(c: Client, t: AddAliasActionDefinition): Future[IndicesAliasesResponse] = {
      val _builder = c.admin.indices().prepareAliases().addAliasAction(AliasActionBuilders.add(t))
      injectFuture(_builder.execute(_))
    }
  }

  // executable so we can use removeAlias directly
  implicit object RemoveAliasActionDefinitionExecutable
    extends Executable[RemoveAliasActionDefinition, IndicesAliasesResponse, IndicesAliasesResponse] {
    override def apply(c: Client, t: RemoveAliasActionDefinition): Future[IndicesAliasesResponse] = {
      val _builder = c.admin.indices().prepareAliases().addAliasAction(AliasActionBuilders.remove(t))
      injectFuture(_builder.execute(_))
    }
  }

  implicit def getResponseToGetResult(resp: GetAliasesResponse): GetAliasResult = GetAliasResult(resp)
}
