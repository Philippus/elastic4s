package com.sksamuel.elastic4s

import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest.AliasActions
import org.elasticsearch.action.admin.indices.alias.get.{GetAliasesRequest, GetAliasesResponse}
import org.elasticsearch.action.admin.indices.alias.{IndicesAliasesRequest, IndicesAliasesResponse}
import org.elasticsearch.client.Client
import org.elasticsearch.cluster.metadata.AliasMetaData
import org.elasticsearch.index.query.QueryBuilder

import scala.concurrent.Future
import scala.language.implicitConversions

trait AliasesDsl {

  class AddAliasExpectsIndex(alias: String) {
    require(alias.nonEmpty, "alias must not be null or empty")
    def on(index: String) = AddAliasActionDefinition(alias, index)
  }

  class RemoveAliasExpectsIndex(alias: String) {
    require(alias.nonEmpty, "alias must not be null or empty")
    def on(index: String) = RemoveAliasActionDefinition(alias, index)
  }

  implicit object GetAliasDefinitionExecutable
    extends Executable[GetAliasDefinition, GetAliasesResponse, GetAliasesResponse] {
    override def apply(c: Client, t: GetAliasDefinition): Future[GetAliasesResponse] = {
      injectFuture(c.admin.indices.getAliases(t.build, _))
    }
  }

  implicit object IndicesAliasesRequestDefinitionExecutable
    extends Executable[IndicesAliasesRequestDefinition, IndicesAliasesResponse, IndicesAliasesResponse] {
    override def apply(c: Client, t: IndicesAliasesRequestDefinition): Future[IndicesAliasesResponse] = {
      injectFuture(c.admin.indices.aliases(t.build, _))
    }
  }

  implicit def getResponseToGetResult(resp: GetAliasesResponse): GetAliasResult = GetAliasResult(resp)
}

case class GetAliasDefinition(aliases: Seq[String]) {

  val request = new GetAliasesRequest(aliases.toArray)
  def build = request

  def on(indexes: String*): GetAliasDefinition = {
    request.indices(indexes: _*)
    this
  }
}

case class GetAliasResult(response: GetAliasesResponse) {

  import scala.collection.JavaConverters._

  def aliases: Map[String, Seq[AliasMetaData]] = {
    response.getAliases.keysIt().asScala.map(key => key -> response.getAliases.get(key).asScala.toSeq).toMap
  }
}

case class AddAliasActionDefinition(alias: String,
                                    index: String,
                                    routing: Option[String] = None,
                                    indexRouting: Option[String] = None,
                                    searchRouting: Option[String] = None,
                                    filter: Option[QueryBuilder] = None) extends AliasActionDefinition {

  def routing(route: String) = copy(routing = Option(route))
  def searchRouting(searchRouting: String) = copy(searchRouting = Option(searchRouting))
  def indexRouting(indexRouting: String) = copy(indexRouting = Option(indexRouting))

  def filter(query: String) = filter(QueryStringQueryDefinition(query))
  def filter(query: QueryBuilder) = copy(filter = Option(query))
  def filter(query: QueryDefinition) = copy(filter = Option(query.builder))

  override def build: IndicesAliasesRequest.AliasActions = {
    val action = AliasActions.add().alias(alias).index(index)
    routing.foreach(action.routing)
    indexRouting.foreach(action.indexRouting)
    searchRouting.foreach(action.searchRouting)
    searchRouting.foreach(action.filter)
    action
  }
}

case class RemoveAliasActionDefinition(alias: String,
                                       index: String,
                                       routing: Option[String] = None,
                                       indexRouting: Option[String] = None,
                                       searchRouting: Option[String] = None,
                                       filter: Option[QueryBuilder] = None) extends AliasActionDefinition {

  def routing(route: String) = copy(routing = Option(route))
  def searchRouting(searchRouting: String) = copy(searchRouting = Option(searchRouting))
  def indexRouting(indexRouting: String) = copy(indexRouting = Option(indexRouting))

  def filter(query: String) = filter(QueryStringQueryDefinition(query))
  def filter(query: QueryBuilder) = copy(filter = Option(query))
  def filter(query: QueryDefinition) = copy(filter = Option(query.builder))

  override def build: IndicesAliasesRequest.AliasActions = {
    val action = AliasActions.remove().alias(alias).index(index)
    routing.foreach(action.routing)
    indexRouting.foreach(action.indexRouting)
    searchRouting.foreach(action.searchRouting)
    searchRouting.foreach(action.filter)
    action
  }
}

case class JavaAliasAction(action: IndicesAliasesRequest.AliasActions) extends AliasActionDefinition {
  override def build: IndicesAliasesRequest.AliasActions = action
}

trait AliasActionDefinition {
  def build: IndicesAliasesRequest.AliasActions
}

case class IndicesAliasesRequestDefinition(actions: Seq[AliasActionDefinition]) {

  def build: IndicesAliasesRequest = {
    val req = new IndicesAliasesRequest()
    actions.foldLeft(req) { (req, action) =>
      req.addAliasAction(action.build)
    }
    req
  }
}
