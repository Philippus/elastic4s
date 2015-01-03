package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.admin._
import com.sksamuel.elastic4s.mappings.MappingDsl
import com.sksamuel.elastic4s.source.ObjectSource

import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }

/** @author Stephen Samuel */
trait ElasticDsl
    extends IndexDsl
    with AliasesDsl
    with BulkDsl
    with ClusterDsl
    with CountDsl
    with CreateIndexDsl
    with DeleteIndexDsl
    with DeleteDsl
    with ExplainDsl
    with FacetDsl
    with GetDsl
    with IndexRecoveryDsl
    with IndexStatusDsl
    with MappingDsl
    with MoreLikeThisDsl
    with MultiGetDsl
    with OptimizeDsl
    with PercolateDsl
    with SearchDsl
    with ScoreDsl
    with SnapshotDsl
    with TemplateDsl
    with UpdateDsl
    with ValidateDsl {

  case object add {
    def alias(alias: String) = new AddAliasExpectsIndex(alias)
  }

  case object aliases {
    @deprecated("use `add alias` instead of `aliases add` for a more readable dsl", "1.4.0.Beta2")
    def add(alias: String) = new AddAliasExpectsIndex(alias)
    @deprecated("use `remove alias` instead of `aliases remove` for a more readable dsl", "1.4.0.Beta2")
    def remove(alias: String) = new RemoveAliasExpectsIndex(alias)
    @deprecated("use `get alias` instead of `aliases get` for a more readable dsl", "1.4.0.Beta2")
    def get(aliases: String*) = new GetAliasDefinition(aliases)
  }

  def agg = aggregation
  case object aggregation {
    def avg(name: String) = new AvgAggregationDefinition(name)
    def children(name: String) = new ChildrenAggregationDefinition(name)
    def count(name: String) = new ValueCountAggregationDefinition(name)
    def cardinality(name: String) = new CardinalityAggregationDefinition(name)
    def datehistogram(name: String) = new DateHistogramAggregation(name)
    def daterange(name: String) = new DateRangeAggregation(name)
    def extendedstats(name: String) = new ExtendedStatsAggregationDefinition(name)
    def filter(name: String) = new FilterAggregationDefinition(name)
    def filters(name: String) = new FiltersAggregationDefinition(name)
    def geobounds(name: String) = new GeoBoundsAggregationDefinition(name)
    def geodistance(name: String) = new GeoDistanceAggregationDefinition(name)
    def global(name: String) = new GlobalAggregationDefinition(name)
    def histogram(name: String) = new HistogramAggregation(name)
    def max(name: String) = new MaxAggregationDefinition(name)
    def min(name: String) = new MinAggregationDefinition(name)
    def missing(name: String) = new MissingAggregationDefinition(name)
    def nested(name: String) = new NestedAggregationDefinition(name)
    def percentiles(name: String) = new PercentilesAggregationDefinition(name)
    def percentileranks(name: String) = new PercentileRanksAggregationDefinition(name)
    def range(name: String) = new RangeAggregationDefinition(name)
    def sigTerms(name: String) = new SigTermsAggregationDefinition(name)
    def stats(name: String) = new StatsAggregationDefinition(name)
    def sum(name: String) = new SumAggregationDefinition(name)
    def terms(name: String) = new TermAggregationDefinition(name)
    def topHits(name: String) = new TopHitsAggregationDefinition(name)
  }

  case object by {

    def prefix(tuple: (String, Any)): PrefixQueryDefinition = prefix(tuple._1, tuple._2)
    def prefix(field: String, value: Any): PrefixQueryDefinition = new PrefixQueryDefinition(field, value)

    def score = new ScoreSortDefinition

    def geo(field: String): GeoDistanceSortDefinition = new GeoDistanceSortDefinition(field)
    def field(field: String): FieldSortDefinition = new FieldSortDefinition(field)

    def script(script: String) = new ScriptSortDefinition(script)
  }

  case object count {
    def from(indexesTypes: IndexesTypes): CountDefinition = new CountDefinition(indexesTypes)
    def from(indexes: Iterable[String]): CountDefinition = from(IndexesTypes(indexes))
    def from(indexes: String*): CountDefinition = from(IndexesTypes(indexes))
  }

  case object create {
    def index(name: String) = new CreateIndexDefinition(name)
    def snapshot(name: String) = new CreateSnapshotExpectsIn(name)
    def repository(name: String) = new CreateRepositoryExpectsType(name)
    def template(name: String) = new CreateIndexTemplateExpectsPattern(name)
  }

  case object delete {
    def id(id: Any): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)
    def from(indexesTypes: IndexesTypes): DeleteByQueryExpectsWhere = new DeleteByQueryExpectsWhere(indexesTypes)
    def from(index: String): DeleteByQueryExpectsWhere = from(IndexesTypes(index))
    def from(indexes: String*): DeleteByQueryExpectsType = from(indexes)
    def from(indexes: Iterable[String]): DeleteByQueryExpectsType = new DeleteByQueryExpectsType(indexes.toSeq)
    def index(indexes: String*): DeleteIndexDefinition = new DeleteIndexDefinition(indexes: _*)
    def snapshot(name: String) = new DeleteSnapshotExpectsIn(name)
    def template(name: String) = new DeleteIndexTemplateDefinition(name)
  }

  case object explain {
    def id(id: Any) = new ExplainExpectsIndex(id)
  }

  case object get {
    def id(id: Any) = new GetWithIdExpectsFrom(id.toString)
    def alias(aliases: String*) = new GetAliasDefinition(aliases)
  }

  @deprecated("use index keyword", "1.4.0")
  def insert = index
  case object index {
    def into(index: String): IndexDefinition = into(index.split("/").head, index.split("/").last)
    def into(index: String, `type`: String): IndexDefinition = new IndexDefinition(index, `type`)
    def into(kv: (String, String)): IndexDefinition = into(kv._1, kv._2)
  }

  def mlt = morelike
  case object morelike {
    def id(id: Any) = new MltExpectsIndex(id.toString)
  }

  case object optimize {
    def index(indexes: Iterable[String]): OptimizeDefinition = new OptimizeDefinition(indexes.toSeq: _*)
    def index(indexes: String*): OptimizeDefinition = index(indexes)
  }

  case object percolate {
    def in(index: String) = new PercolateDefinition(index)
  }

  case object put {
    def mapping(indexes: IndexesTypes) = new PutMappingDefinition(indexes)
  }

  case object recover {
    def index(indexes: String*) = new IndexRecoveryDefinition(indexes: _*)
  }

  case object remove {
    def alias(alias: String) = new RemoveAliasExpectsIndex(alias)
  }

  case object register {
    def id(id: Any) = new RegisterExpectsIndex(id.toString)
  }

  case object repository {
    @deprecated("use `create repository` instead of `repository create` for a more readable dsl", "1.4.0.Beta2")
    def create(name: String) = new CreateRepositoryExpectsType(name)
  }

  case object restore {
    def snapshot(name: String) = new RestoreSnapshotExpectsFrom(name)
  }

  case object script {
    def field(n: String): ExpectsScript = ExpectsScript(field = n)
  }

  case object snapshot {
    @deprecated("use `create snapshot` instead of `snapshot create` for a more readable dsl", "1.4.0.Beta2")
    def create(name: String) = new CreateSnapshotExpectsIn(name)
    @deprecated("use `restore snapshot` instead of `snapshot restore` for a more readable dsl", "1.4.0.Beta2")
    def restore(name: String) = new RestoreSnapshotExpectsFrom(name)
    @deprecated("use `delete snapshot` instead of `snapshot delete` for a more readable dsl", "1.4.0.Beta2")
    def delete(name: String) = new DeleteSnapshotExpectsIn(name)
  }

  @deprecated("use search keyword", "1.4.0.Beta2")
  def select = search
  case object search {
    def in(indexes: String*): SearchDefinition = in(IndexesTypes(indexes))
    def in(tuple: (String, String)): SearchDefinition = in(IndexesTypes(tuple))
    def in(indexesTypes: IndexesTypes): SearchDefinition = new SearchDefinition(indexesTypes)
  }

  case object template {
    @deprecated("use `create template` instead of `template create` for a more readable dsl", "1.4.0.Beta2")
    def create(name: String) = new CreateIndexTemplateExpectsPattern(name)
    @deprecated("use `delete template` instead of `template delete` for a more readable dsl", "1.4.0.Beta2")
    def delete(name: String) = new DeleteIndexTemplateDefinition(name)
  }

  case object update {
    def id(id: Any) = new UpdateExpectsIndex(id.toString)
  }

  case object validate {
    def in(value: String): ValidateDefinition = in(value.split("/").toSeq)
    def in(value: Seq[String]): ValidateDefinition = in((value(0), value(1)))
    def in(tuple: (String, String)): ValidateDefinition = new ValidateDefinition(tuple._1, tuple._2)
  }

  implicit val duration: Duration = 10.seconds

  implicit class RichFuture[T](future: Future[T]) {
    def await(implicit duration: Duration = 10.seconds) = Await.result(future, duration)
  }

  implicit def product2source(obj: Product): ObjectSource = ObjectSource(obj)
}

object ElasticDsl extends ElasticDsl
