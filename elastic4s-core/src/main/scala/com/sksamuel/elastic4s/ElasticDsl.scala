package com.sksamuel.elastic4s

import java.util.UUID

import com.sksamuel.elastic4s.admin._
import com.sksamuel.elastic4s.mappings.FieldType._
import com.sksamuel.elastic4s.mappings._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

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
  with FacetDsl
  with FieldStatsDsl
  with ExplainDsl
  with GetDsl
  with IndexAdminDsl
  with IndexRecoveryDsl
  with IndexStatusDsl
  with IndexTemplateDsl
  with MappingDsl
  with MoreLikeThisDsl
  with MultiGetDsl
  with OptimizeDsl
  with PercolateDsl
  with SearchDsl
  with SettingsDsl
  with ScoreDsl
  with ScrollDsl
  with SnapshotDsl
  with TermVectorDsl
  with UpdateDsl
  with ValidateDsl
  with ElasticImplicits {

  case object add {
    def alias(alias: String): AddAliasExpectsIndex = {
      require(alias.nonEmpty, "alias name must not be null or empty")
      new AddAliasExpectsIndex(alias)
    }
  }
  def addAlias(name: String): AddAliasExpectsIndex = add alias name

  @deprecated("use `add/remove/get alias` instead of `aliases add` for a more readable dsl", "1.4.0.Beta2")
  case object aliases {
    @deprecated("use `add alias` instead of `aliases add` for a more readable dsl", "1.4.0.Beta2")
    def add(alias: String) = new AddAliasExpectsIndex(alias)
    @deprecated("use `remove alias` instead of `aliases remove` for a more readable dsl", "1.4.0.Beta2")
    def remove(alias: String) = new RemoveAliasExpectsIndex(alias)
    @deprecated("use `get alias` instead of `aliases get` for a more readable dsl", "1.4.0.Beta2")
    def get(aliases: String*) = new GetAliasDefinition(aliases)
  }

  def aliases(aliasMutations: MutateAliasDefinition*): IndicesAliasesRequestDefinition = aliases(aliasMutations)
  def aliases(aliasMutations: Iterable[MutateAliasDefinition]): IndicesAliasesRequestDefinition = {
    new IndicesAliasesRequestDefinition(aliasMutations.toSeq: _*)
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
    def geohash(name:String) = new GeoHashGridAggregationDefinition(name)
    def global(name: String) = new GlobalAggregationDefinition(name)
    def histogram(name: String) = new HistogramAggregation(name)
    def ipRange(name: String) = new IpRangeAggregationDefinition(name)
    def max(name: String) = new MaxAggregationDefinition(name)
    def min(name: String) = new MinAggregationDefinition(name)
    def missing(name: String) = new MissingAggregationDefinition(name)
    def nested(name: String) = new NestedAggregationDefinition(name)
    def reverseNested(name: String) = new ReverseNestedAggregationDefinition(name)
    def percentiles(name: String) = new PercentilesAggregationDefinition(name)
    def percentileranks(name: String) = new PercentileRanksAggregationDefinition(name)
    def range(name: String) = new RangeAggregationDefinition(name)
    def sigTerms(name: String) = new SigTermsAggregationDefinition(name)
    def stats(name: String) = new StatsAggregationDefinition(name)
    def sum(name: String) = new SumAggregationDefinition(name)
    def terms(name: String) = new TermAggregationDefinition(name)
    def topHits(name: String) = new TopHitsAggregationDefinition(name)
  }

  def attachmentField(name: String) = field(name).typed(AttachmentType)

  def binaryField(name: String) = field(name).typed(BinaryType)
  def booleanField(name: String) = field(name).typed(BooleanType)
  def byteField(name: String) = field(name).typed(ByteType)

  @deprecated("use score sort, geo sort, field sort or script sort", "1.6.0")
  case object by {
    def score: ScoreSortDefinition = ElasticDsl.score.sort
    def geo(field: String): GeoDistanceSortDefinition = ElasticDsl.geo sort field
    def field(field: String): FieldSortDefinition = ElasticDsl.field.sort(field)
    def script(script: String) = ElasticDsl.script.sort(script)
  }

  case object clear {
    def cache(indexes: Iterable[String]): ClearCacheDefinition = new ClearCacheDefinition(indexes.toSeq)
    def cache(indexes: String*): ClearCacheDefinition = new ClearCacheDefinition(indexes)
    def scroll(id: String, ids: String*): ClearScrollDefinition = clearScroll(id +: ids)
    def scroll(ids: Iterable[String]): ClearScrollDefinition = clearScroll(ids)
  }

  def clearCache(indexes: String*): ClearCacheDefinition = new ClearCacheDefinition(indexes)
  def clearCache(indexes: Iterable[String]): ClearCacheDefinition = new ClearCacheDefinition(indexes.toSeq)
  def clearIndex(indexes: String*): ClearCacheDefinition = new ClearCacheDefinition(indexes)
  def clearIndex(indexes: Iterable[String]): ClearCacheDefinition = new ClearCacheDefinition(indexes.toSeq)
  def clearScroll(id: String, ids: String*): ClearScrollDefinition = ClearScrollDefinition(id +: ids)
  def clearScroll(ids: Iterable[String]): ClearScrollDefinition = ClearScrollDefinition(ids.toSeq)

  case object close {
    def index(index: String): CloseIndexDefinition = new CloseIndexDefinition(index)
  }

  def closeIndex(index: String): CloseIndexDefinition = close index index

  case object cluster {
    def persistentSettings(settings: Map[String, String]) = ClusterSettingsDefinition(settings, Map.empty)
    def transientSettings(settings: Map[String, String]) = ClusterSettingsDefinition(Map.empty, settings)
  }

  def clusterPersistentSettings(settings: Map[String, String]) = cluster persistentSettings settings
  def clusterTransientSettings(settings: Map[String, String]) = cluster transientSettings settings

  def clusterState = new ClusterStateDefinition
  def clusterHealth = new ClusterHealthDefinition()
  def clusterStats = new ClusterStatsDefinition
  @deprecated("use clusterStats", "1.6.1")
  def clusterStatus = new ClusterStatsDefinition
  def clusterHealth(indices: String*) = new ClusterHealthDefinition(indices: _*)

  case object commonGrams {
    def tokenfilter(name: String): CommonGramsTokenFilter = CommonGramsTokenFilter(name)
  }
  def commonGramsTokenFilter(name: String) = CommonGramsTokenFilter(name)

  case object completion {
    def suggestion(name: String) = new CompletionSuggestionDefinition(name)
  }
  def completionSuggestion: CompletionSuggestionDefinition = completion suggestion UUID.randomUUID.toString
  def completionSuggestion(name: String): CompletionSuggestionDefinition = completion suggestion name

  case object count {
    def from(indexType: IndexType): CountDefinition = from(IndexesTypes(indexType))
    def from(indexesTypes: IndexesTypes): CountDefinition = new CountDefinition(indexesTypes)
    def from(indexes: Iterable[String]): CountDefinition = from(IndexesTypes(indexes))
    def from(indexes: String*): CountDefinition = from(IndexesTypes(indexes))
  }

  @deprecated("use countFrom", "1.6.0")
  def count(indexesTypes: IndexesTypes): CountDefinition = new CountDefinition(indexesTypes)
  @deprecated("use countFrom", "1.6.0")
  def count(indexes: String*): CountDefinition = new CountDefinition(IndexesTypes(indexes))

  def countFrom(index: (String, String)): CountDefinition = count from index
  def countFrom(indexes: String*): CountDefinition = count from indexes
  def countFrom(indexes: IndexType): CountDefinition = count from indexes

  case object create {

    def index(name: String) = {
      require(name.nonEmpty, "index name must not be null or empty")
      new CreateIndexDefinition(name)
    }

    def snapshot(name: String) = {
      require(name.nonEmpty, "snapshot name must not be null or empty")
      new CreateSnapshotExpectsIn(name)
    }

    def repository(name: String): CreateRepositoryExpectsType = {
      require(name.nonEmpty, "repository name must not be null or empty")
      new CreateRepositoryExpectsType(name)
    }

    def template(name: String): CreateIndexTemplateExpectsPattern = {
      require(name.nonEmpty, "template name must not be null or empty")
      new CreateIndexTemplateExpectsPattern(name)
    }
  }
  def createIndex(name: String) = create index name
  def createSnapshot(name: String) = create snapshot name
  def createRepository(name: String) = create repository name
  def createTemplate(name: String) = create template name

  def dateField(name: String) = field(name).typed(DateType)
  def doubleField(name: String) = field(name).typed(DoubleType)

  case object delete {

    def id(id: Any): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)

    @deprecated(
      "Delete by query will be removed in 2.0. Instead, use the scroll/scan API to find all matching IDs and then issue a bulk req",
      "1.6.0")
    def from(indexesTypes: IndexesTypes): DeleteByQueryExpectsClause = new DeleteByQueryExpectsClause(indexesTypes)
    @deprecated(
      "Delete by query will be removed in 2.0. Instead, use the scroll/scan API to find all matching IDs and then issue a bulk req",
      "1.6.0")
    def from(indexType: IndexType): DeleteByQueryExpectsClause = from(IndexesTypes(indexType))
    @deprecated(
      "Delete by query will be removed in 2.0. Instead, use the scroll/scan API to find all matching IDs and then issue a bulk req",
      "1.6.0")
    def from(index: String): DeleteByQueryExpectsClause = from(IndexesTypes(index))
    @deprecated(
      "Delete by query will be removed in 2.0. Instead, use the scroll/scan API to find all matching IDs and then issue a bulk req",
      "1.6.0")
    def from(indexes: String*): DeleteByQueryExpectsType = from(indexes)
    @deprecated(
      "Delete by query will be removed in 2.0. Instead, use the scroll/scan API to find all matching IDs and then issue a bulk req",
      "1.6.0")
    def from(indexes: Iterable[String]): DeleteByQueryExpectsType = new DeleteByQueryExpectsType(indexes.toSeq)

    def index(indexes: String*): DeleteIndexDefinition = new DeleteIndexDefinition(indexes: _*)
    def index(indexes: Iterable[String]): DeleteIndexDefinition = new DeleteIndexDefinition(indexes.toSeq: _*)
    def snapshot(name: String): DeleteSnapshotExpectsIn = new DeleteSnapshotExpectsIn(name)
    def template(name: String) = new DeleteIndexTemplateDefinition(name)
    def mapping(indexes: String*) = DeleteMappingDefinition(indexes)
    def mapping(indexType: IndexType) = DeleteMappingDefinition(List(indexType.index)).types(indexType.`type`)
  }

  def delete(id: Any): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)

  def deleteIndex(indexes: String*): DeleteIndexDefinition = new DeleteIndexDefinition(indexes: _*)
  def deleteIndex(indexes: Iterable[String]): DeleteIndexDefinition = new DeleteIndexDefinition(indexes.toSeq: _*)

  def deleteSnapshot(name: String): DeleteSnapshotExpectsIn = delete snapshot name
  def deleteTemplate(name: String): DeleteIndexTemplateDefinition = delete template name
  def deleteMapping(indexes: String*) = DeleteMappingDefinition(indexes)
  def deleteMapping(indexType: IndexType) = DeleteMappingDefinition(List(indexType.index)).types(indexType.`type`)

  case object explain {
    def id(id: Any): ExplainExpectsIndex = new ExplainExpectsIndex(id)
  }
  def explain(id: Any): ExplainExpectsIndex = explain id id

  case object field extends TypeableFields {
    val name = ""
    def name(name: String): FieldDefinition = new FieldDefinition(name)
    def sort(field: String): FieldSortDefinition = new FieldSortDefinition(field)
    def stats(fields: String*): FieldStatsDefinition = new FieldStatsDefinition(fields = fields)
    def stats(fields: Iterable[String]): FieldStatsDefinition = new FieldStatsDefinition(fields = fields.toSeq)
  }
  def field(name: String): FieldDefinition = field name name
  def fieldStats(fields: String*): FieldStatsDefinition = new FieldStatsDefinition(fields = fields)
  def fieldStats(fields: Iterable[String]): FieldStatsDefinition = new FieldStatsDefinition(fields = fields.toSeq)
  def fieldSort(name: String) = field sort name

  case object flush {
    def index(indexes: Iterable[String]): FlushIndexDefinition = new FlushIndexDefinition(indexes.toSeq)
    def index(indexes: String*): FlushIndexDefinition = new FlushIndexDefinition(indexes)
  }

  def flushIndex(indexes: Iterable[String]): FlushIndexDefinition = flush index indexes
  def flushIndex(indexes: String*): FlushIndexDefinition = flush index indexes

  case object fuzzyCompletion {
    def suggestion(name: String) = new FuzzyCompletionSuggestionDefinition(name)
  }
  def fuzzyCompletionSuggestion: FuzzyCompletionSuggestionDefinition = fuzzyCompletionSuggestion(UUID
    .randomUUID
    .toString)
  def fuzzyCompletionSuggestion(name: String): FuzzyCompletionSuggestionDefinition = fuzzyCompletion suggestion name

  case object geo {
    def sort(field: String): GeoDistanceSortDefinition = new GeoDistanceSortDefinition(field)
  }
  def geoSort(name: String): GeoDistanceSortDefinition = geo sort name


  def completionField(name: String) = field(name).typed(CompletionType)

  def floatField(name: String) = field(name).typed(FloatType)

  def multiField(name: String) = field(name).typed(MultiFieldType)
  def geopointField(name: String) = field(name).typed(GeoPointType)
  def geoshapeField(name: String) = field(name).typed(GeoShapeType)

  case object get {

    def id(id: Any) = {
      require(id.toString.nonEmpty, "id must not be null or empty")
      new GetWithIdExpectsFrom(id.toString)
    }

    def alias(aliases: String*): GetAliasDefinition = new GetAliasDefinition(aliases)

    def cluster(stats: StatsKeyword): ClusterStatsDefinition = new ClusterStatsDefinition
    def cluster(health: HealthKeyword): ClusterHealthDefinition = new ClusterHealthDefinition

    def mapping(ixTp: IndexType): GetMappingDefinition = new GetMappingDefinition(List(ixTp.index)).types(ixTp.`type`)
    def mapping(indexes: Iterable[String]): GetMappingDefinition = new GetMappingDefinition(indexes)
    def mapping(indexes: String*): GetMappingDefinition = mapping(indexes)

    def segments(indexes: String*): GetSegmentsDefinition = new GetSegmentsDefinition(indexes)
    def segments(indexes: Iterable[String]): GetSegmentsDefinition = new GetSegmentsDefinition(indexes.toSeq)

    def settings(indexes: String*): GetSettingsDefinition = new GetSettingsDefinition(indexes)
    def settings(indexes: Iterable[String]): GetSettingsDefinition = new GetSettingsDefinition(indexes.toSeq)

    def template(name: String): GetTemplateDefinition = new GetTemplateDefinition(name)

    def snapshot(names: Iterable[String]): GetSnapshotsExpectsFrom = new GetSnapshotsExpectsFrom(names.toSeq)
    def snapshot(names: String*): GetSnapshotsExpectsFrom = snapshot(names)
  }

  def get(id: Any): GetWithIdExpectsFrom = new GetWithIdExpectsFrom(id.toString)
  def getAlias(aliases: String*): GetAliasDefinition = new GetAliasDefinition(aliases)
  def getMapping(ixTp: IndexType): GetMappingDefinition = new GetMappingDefinition(List(ixTp.index)).types(ixTp.`type`)
  def getSegments(indexes: String*): GetSegmentsDefinition = get segments (indexes.toSeq: _*)
  def getSettings(indexes: String*): GetSettingsDefinition = get settings indexes
  def getSnapshot(names: Iterable[String]): GetSnapshotsExpectsFrom = get snapshot names
  def getSnapshot(names: String*): GetSnapshotsExpectsFrom = get snapshot names
  def getTemplate(name: String): GetTemplateDefinition = get template name

  def intField(name: String) = field(name).typed(IntegerType)
  def ipField(name: String) = field(name).typed(IpType)

  trait HealthKeyword
  case object health extends HealthKeyword

  case object highlight {
    def field(name: String): HighlightDefinition = new HighlightDefinition(name)
  }
  def highlight(field: String): HighlightDefinition = new HighlightDefinition(field)

  trait StatsKeyword
  case object stats extends StatsKeyword

  @deprecated("use index keyword", "1.4.0")
  def insert: index.type = index
  case object index {

    def exists(indexes: Iterable[String]): IndexExistsDefinition = new IndexExistsDefinition(indexes.toSeq)
    def exists(indexes: String*): IndexExistsDefinition = new IndexExistsDefinition(indexes)

    def into(index: String): IndexDefinition = {
      require(index.nonEmpty, "index must not be null or empty")
      into(index.split("/").head, index.split("/").last)
    }

    def into(index: String, `type`: String): IndexDefinition = {
      require(index.nonEmpty, "index must not be null or empty")
      new IndexDefinition(index, `type`)
    }

    def into(kv: (String, String)): IndexDefinition = {
      into(kv._1, kv._2)
    }

    def into(indexType: IndexType): IndexDefinition = {
      require(indexType != null, "indexType must not be null or empty")
      new IndexDefinition(indexType.index, indexType.`type`)
    }

    def stats(indexes: Iterable[String]): IndicesStatsDefinition = new IndicesStatsDefinition(indexes.toSeq)
    def stats(indexes: String*): IndicesStatsDefinition = new IndicesStatsDefinition(indexes)
  }

  def indexExists(indexes: Iterable[String]): IndexExistsDefinition = new IndexExistsDefinition(indexes.toSeq)
  def indexExists(indexes: String*): IndexExistsDefinition = new IndexExistsDefinition(indexes)

  def indexInto(indexType: IndexType): IndexDefinition = {
    require(indexType != null, "indexType must not be null or empty")
    new IndexDefinition(indexType.index, indexType.`type`)
  }

  def indexInto(index: String, `type`: String): IndexDefinition = {
    require(index.nonEmpty, "index must not be null or empty")
    new IndexDefinition(index, `type`)
  }

  def indexStats(indexes: Iterable[String]): IndicesStatsDefinition = new IndicesStatsDefinition(indexes.toSeq)
  def indexStats(indexes: String*): IndicesStatsDefinition = new IndicesStatsDefinition(indexes)

  case object inner {
    def hits(name: String): QueryInnerHitsDefinition = new QueryInnerHitsDefinition(name)
    def hit(name: String): InnerHitDefinition = new InnerHitDefinition(name)
  }
  def innerHit(name: String): InnerHitDefinition = inner hit name
  def innerHits(name: String): QueryInnerHitsDefinition = inner hits name

  def longField(name: String) = field(name).typed(LongType)

  case object mapping {
    def name(name: String): MappingDefinition = {
      require(name.nonEmpty, "mapping name must not be null or empty")
      new MappingDefinition(name)
    }
  }
  def mapping(name: String): MappingDefinition = mapping name name

  @deprecated("The More Like This API will be removed in 2.0. Instead, use the More Like This Query", "1.6.0")
  case object more {
    def like(id: Any): MltExpectsIndex = {
      require(id.toString.nonEmpty, "id must not be null or empty")
      new MltExpectsIndex(id.toString)
    }
  }

  @deprecated("The More Like This API will be removed in 2.0. Instead, use the More Like This Query", "1.6.0")
  def mlt: morelike.type = morelike
  @deprecated("The More Like This API will be removed in 2.0. Instead, use the More Like This Query", "1.6.0")
  case object morelike {
    def id(id: Any): MltExpectsIndex = {
      require(id.toString.nonEmpty, "id must not be null or empty")
      new MltExpectsIndex(id.toString)
    }
  }

  def multiget(gets: Iterable[GetDefinition]): MultiGetDefinition = new MultiGetDefinition(gets)
  def multiget(gets: GetDefinition*): MultiGetDefinition = new MultiGetDefinition(gets)

  def nestedField(name: String): NestedFieldDefinition = field(name).typed(NestedType)

  case object ngram {
    def tokenfilter(name: String): NGramTokenFilter = NGramTokenFilter(name)
  }
  def ngramTokenFilter(name: String): NGramTokenFilter = NGramTokenFilter(name)

  def objectField(name: String): ObjectFieldDefinition = field(name).typed(ObjectType)

  case object edgeNGram {
    def tokenfilter(name: String): EdgeNGramTokenFilter = EdgeNGramTokenFilter(name)
  }
  def edgeNGramTokenfilter(name: String): EdgeNGramTokenFilter = EdgeNGramTokenFilter(name)

  case object open {
    def index(index: String): OpenIndexDefinition = new OpenIndexDefinition(index)
  }
  def openIndex(index: String): OpenIndexDefinition = open index index

  case object optimize {
    def index(indexes: Iterable[String]): OptimizeDefinition = new OptimizeDefinition(indexes.toSeq: _*)
    def index(indexes: String*): OptimizeDefinition = index(indexes)
  }
  @deprecated("use optimizeIndex", "1.6.2")
  def optimize(indexes: String*): OptimizeDefinition = new OptimizeDefinition(indexes: _*)
  def optimizeIndex(indexes: String*): OptimizeDefinition = optimize index indexes
  def optimizeIndex(indexes: Iterable[String]): OptimizeDefinition = optimize index indexes

  case object percolate {
    def in(index: String): PercolateDefinition = {
      require(index.nonEmpty, "index must not be null or empty")
      new PercolateDefinition(index)
    }
    def in(indexType: IndexType): PercolateDefinition = new PercolateDefinition(IndexesTypes(indexType))
  }

  def percolateIn(index: String): PercolateDefinition = percolate in index
  def percolateIn(indexType: IndexType): PercolateDefinition = percolate in indexType

  case object phrase {
    def suggestion(name: String): PhraseSuggestionDefinition = new PhraseSuggestionDefinition(name)
  }
  def phraseSuggestion: PhraseSuggestionDefinition = phrase suggestion UUID.randomUUID.toString
  def phraseSuggestion(name: String): PhraseSuggestionDefinition = phrase suggestion name

  case object put {
    def mapping(indexType: IndexType): PutMappingDefinition = new PutMappingDefinition(indexType)
  }
  def putMapping(indexType: IndexType): PutMappingDefinition = new PutMappingDefinition(indexType)

  case object recover {
    def index(indexes: Iterable[String]): IndexRecoveryDefinition = new IndexRecoveryDefinition(indexes.toSeq)
    def index(indexes: String*): IndexRecoveryDefinition = new IndexRecoveryDefinition(indexes)
  }
  def recoverIndex(indexes: String*): IndexRecoveryDefinition = recover index indexes
  def recoverIndex(indexes: Iterable[String]): IndexRecoveryDefinition = recover index indexes

  case object refresh {
    def index(indexes: Iterable[String]): RefreshIndexDefinition = new RefreshIndexDefinition(indexes.toSeq)
    def index(indexes: String*): RefreshIndexDefinition = new RefreshIndexDefinition(indexes)
  }

  def refreshIndex(indexes: Iterable[String]): RefreshIndexDefinition = refresh index indexes
  def refreshIndex(indexes: String*): RefreshIndexDefinition = refresh index indexes

  case object remove {
    def alias(alias: String): RemoveAliasExpectsIndex = {
      require(alias.nonEmpty, "alias must not be null or empty")
      new RemoveAliasExpectsIndex(alias)
    }
  }
  def removeAlias(alias: String): RemoveAliasExpectsIndex = remove alias alias

  case object register {
    def id(id: Any): RegisterExpectsIndex = {
      require(id.toString.nonEmpty, "id must not be null or empty")
      new RegisterExpectsIndex(id.toString)
    }
  }
  def register(id: Any): RegisterExpectsIndex = register id id

  case object repository {
    @deprecated("use `create repository` instead of `repository create` for a more readable dsl", "1.4.0.Beta2")
    def create(name: String): CreateRepositoryExpectsType = new CreateRepositoryExpectsType(name)
  }

  case object restore {
    def snapshot(name: String): RestoreSnapshotExpectsFrom = {
      require(name.nonEmpty, "snapshot name must not be null or empty")
      new RestoreSnapshotExpectsFrom(name)
    }
  }
  def restoreSnapshot(name: String): RestoreSnapshotExpectsFrom = restore snapshot name

  case object score {
    def sort: ScoreSortDefinition = new ScoreSortDefinition
  }
  def scoreSort: ScoreSortDefinition = score.sort

  case object script {
    def sort(script: String): ScriptSortDefinition = new ScriptSortDefinition(script)
    def field(n: String): ExpectsScript = ExpectsScript(field = n)
  }
  def scriptSort(scripttext: String): ScriptSortDefinition = script sort scripttext
  def scriptField(n: String): ExpectsScript = script field n

  @deprecated("use search keyword", "1.4.0.Beta2")
  def select: search.type = search
  case object search {
    def in(indexes: String*): SearchDefinition = in(IndexesTypes(indexes))
    def in(tuple: (String, String)): SearchDefinition = in(IndexesTypes(tuple))
    def in(indexesTypes: IndexesTypes): SearchDefinition = new SearchDefinition(indexesTypes)
    def in(indexType: IndexType): SearchDefinition = new SearchDefinition(IndexesTypes(indexType))
    def scroll(id: String): SearchScrollDefinition = new SearchScrollDefinition(id)
  }

  @deprecated("use search", "1.6.0")
  def select(indexes: String*): SearchDefinition = search(indexes: _*)
  def search(indexType: IndexType): SearchDefinition = search in indexType
  def search(indexes: String*): SearchDefinition = new SearchDefinition(IndexesTypes(indexes))

  def searchScroll(id: String): SearchScrollDefinition = new SearchScrollDefinition(id)

  case object shingle {
    def tokenfilter(name: String): ShingleTokenFilter = ShingleTokenFilter(name)
  }
  def shingleTokenFilter(name: String): ShingleTokenFilter = ShingleTokenFilter(name)

  def shortField(name: String) = field(name).typed(ShortType)

  case object snapshot {
    @deprecated("use `create snapshot` instead of `snapshot create` for a more readable dsl", "1.4.0.Beta2")
    def create(name: String): CreateSnapshotExpectsIn = new CreateSnapshotExpectsIn(name)
    @deprecated("use `restore snapshot` instead of `snapshot restore` for a more readable dsl", "1.4.0.Beta2")
    def restore(name: String): RestoreSnapshotExpectsFrom = new RestoreSnapshotExpectsFrom(name)
    @deprecated("use `delete snapshot` instead of `snapshot delete` for a more readable dsl", "1.4.0.Beta2")
    def delete(name: String): DeleteSnapshotExpectsIn = new DeleteSnapshotExpectsIn(name)
  }

  case object snowball {
    def tokenfilter(name: String): SnowballTokenFilter = SnowballTokenFilter(name)
  }
  def snowballTokenFilter(name: String): SnowballTokenFilter = SnowballTokenFilter(name)

  @deprecated("use score sort, geo sort, field sort or script sort", "1.6.1")
  case object sortby {
    def score: ScoreSortDefinition = new ScoreSortDefinition
    def geo(field: String): GeoDistanceSortDefinition = new GeoDistanceSortDefinition(field)
    def field(field: String): FieldSortDefinition = new FieldSortDefinition(field)
    def script(script: String): ScriptSortDefinition = new ScriptSortDefinition(script)
  }

  case object stemmer {
    def tokenfilter(name: String): StemmerTokenFilter = StemmerTokenFilter(name)
  }
  def stemmerTokenFilter(name: String): StemmerTokenFilter = StemmerTokenFilter(name)

  def stringField(name: String): StringFieldDefinition = field(name).typed(StringType)

  def suggestions(suggestions: SuggestionDefinition*): SuggestDefinition = SuggestDefinition(suggestions)
  def suggestions(suggestions: Iterable[SuggestionDefinition]): SuggestDefinition = SuggestDefinition(suggestions.toSeq)

  case object template {
    @deprecated("use `create template` instead of `template create` for a more readable dsl", "1.4.0.Beta2")
    def create(name: String): CreateIndexTemplateExpectsPattern = new CreateIndexTemplateExpectsPattern(name)
    @deprecated("use `delete template` instead of `template delete` for a more readable dsl", "1.4.0.Beta2")
    def delete(name: String): DeleteIndexTemplateDefinition = new DeleteIndexTemplateDefinition(name)

    def name(name: String): DynamicTemplateDefinition = new DynamicTemplateDefinition(name)
  }
  def template(name: String): DynamicTemplateDefinition = template name name

  case object term {
    def suggestion(name: String): TermSuggestionDefinition = new TermSuggestionDefinition(name)
  }
  def termSuggestion: TermSuggestionDefinition = term suggestion UUID.randomUUID.toString
  def termSuggestion(name: String): TermSuggestionDefinition = term suggestion name

  case object timestamp {
    def enabled(en: Boolean): TimestampDefinition = TimestampDefinition(en)
  }
  def timestamp(en: Boolean): TimestampDefinition = TimestampDefinition(en)

  def tokenCountField(name: String) = field(name).typed(TokenCountType)

  class TypesExistExpectsIn(types: Seq[String]) {
    def in(indexes: String*): TypesExistsDefinition = new TypesExistsDefinition(indexes, types)
  }
  case object types {
    def exist(types: String*): TypesExistExpectsIn = new TypesExistExpectsIn(types)
  }
  def typesExist(types: String*): TypesExistExpectsIn = new TypesExistExpectsIn(types)

  case object update {
    def id(id: Any): UpdateExpectsIndex = {
      require(id.toString.nonEmpty, "id must not be null or empty")
      new UpdateExpectsIndex(id.toString)
    }
    def settings(index: String): UpdateSettingsDefinition = new UpdateSettingsDefinition(index)
  }
  def update(id: Any): UpdateExpectsIndex = new UpdateExpectsIndex(id.toString)

  case object validate {
    def in(indexType: IndexType): ValidateDefinition = new ValidateDefinition(indexType.index, indexType.`type`)
    def in(value: String): ValidateDefinition = {
      require(value.nonEmpty, "value must not be null or empty")
      in(value.split("/").toSeq)
    }
    def in(value: Seq[String]): ValidateDefinition = in((value.head, value(1)))
    def in(tuple: (String, String)): ValidateDefinition = new ValidateDefinition(tuple._1, tuple._2)
  }

  def validateIn(indexType: IndexType): ValidateDefinition = validate in indexType
  def validateIn(value: String): ValidateDefinition = validate in value

  implicit class RichFuture[T](future: Future[T]) {
    def await(implicit duration: Duration = 10.seconds): T = Await.result(future, duration)
  }
}

object ElasticDsl extends ElasticDsl

//mapping char filter
//
//htmlStrip char filter
//
//patternReplace char filter
