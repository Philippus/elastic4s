package com.sksamuel.elastic4s

import java.util.UUID

import com.sksamuel.elastic4s.admin._
import com.sksamuel.elastic4s.aggregations._
import com.sksamuel.elastic4s.analyzers.{AnalyzerDsl, TokenFilterDsl, TokenizerDsl}
import com.sksamuel.elastic4s.mappings.FieldType._
import com.sksamuel.elastic4s.mappings._
import com.sksamuel.elastic4s.queries.{CompletionSuggestionDefinition, InnerHitDefinition, PhraseSuggestionDefinition, QueryInnerHitsDefinition, SuggestDefinition, SuggestionDefinition, TermSuggestionDefinition}
import com.sksamuel.elastic4s.search.{HighlightDefinition, SearchDefinition, SearchDsl}
import com.sksamuel.elastic4s.sort.{FieldSortDefinition, GeoDistanceSortDefinition, ScoreSortDefinition, ScriptSortDefinition}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.implicitConversions

/** @author Stephen Samuel */
trait ElasticDsl
  extends IndexDsl
  with AggregationDsl
  with AliasesDsl
  with AnalyzerDsl
  with BulkDsl
  with ClusterDsl
  with CreateIndexDsl
  with DeleteIndexDsl
  with DeleteDsl
  with ExplainDsl
  with FieldStatsDsl
  with ForceMergeDsl
  with GetDsl
  with IndexAdminDsl
  with IndexRecoveryDsl
  with IndexTemplateDsl
  with MappingDsl
    with MultiGetApi
  with PercolateDsl
  with PipelineAggregationDsl
  with ReindexDsl
  with ScriptDsl
  with SearchDsl
  with SettingsDsl
  with ScoreDsl
  with ScrollDsl
  with SnapshotDsl
    with TaskApi
    with TermVectorApi
  with TokenizerDsl
  with TokenFilterDsl
  with UpdateDsl
  with ValidateDsl
  with DeprecatedElasticDsl
  with ElasticImplicits {

  case object add {
    def alias(alias: String): AddAliasExpectsIndex = {
      require(alias.nonEmpty, "alias name must not be null or empty")
      new AddAliasExpectsIndex(alias)
    }
  }
  def addAlias(name: String): AddAliasExpectsIndex = add alias name

  def aliases(aliasMutations: MutateAliasDefinition*): IndicesAliasesRequestDefinition = aliases(aliasMutations)
  def aliases(aliasMutations: Iterable[MutateAliasDefinition]): IndicesAliasesRequestDefinition = {
    IndicesAliasesRequestDefinition(aliasMutations.toSeq: _*)
  }

  def agg = aggregation
  case object aggregation {
    def avg(name: String) = AvgAggregationDefinition(name)
    def children(name: String, childType: String) = ChildrenAggregationDefinition(name, childType)
    def count(name: String) = ValueCountAggregationDefinition(name)
    def cardinality(name: String) = CardinalityAggregationDefinition(name)
    def datehistogram(name: String) = DateHistogramAggregation(name)
    def daterange(name: String) = DateRangeAggregation(name)
    def extendedstats(name: String) = ExtendedStatsAggregationDefinition(name)
    def filter(name: String) = FilterAggregationDefinition(name)
    def filters(name: String) = FiltersAggregationDefinition(name)
    def geobounds(name: String) = GeoBoundsAggregationDefinition(name)
    def geodistance(name: String) = GeoDistanceAggregationDefinition(name)
    def geohash(name: String) = GeoHashGridAggregationDefinition(name)
    def global(name: String) = GlobalAggregationDefinition(name)
    def histogram(name: String) = HistogramAggregation(name)
    def ipRange(name: String) = IpRangeAggregationDefinition(name)
    def max(name: String) = MaxAggregationDefinition(name)
    def min(name: String) = MinAggregationDefinition(name)
    def missing(name: String) = MissingAggregationDefinition(name)
    def nested(name: String, path: String) = NestedAggregationDefinition(name, path)
    def reverseNested(name: String) = ReverseNestedAggregationDefinition(name)
    def percentiles(name: String) = PercentilesAggregationDefinition(name)
    def percentileranks(name: String) = PercentileRanksAggregationDefinition(name)
    def range(name: String) = RangeAggregationDefinition(name)
    def scriptedMetric(name: String) = ScriptedMetricAggregationDefinition(name)
    def sigTerms(name: String) = SigTermsAggregationDefinition(name)
    def stats(name: String) = StatsAggregationDefinition(name)
    def sum(name: String) = SumAggregationDefinition(name)
    def terms(name: String) = TermAggregationDefinition(name)
    def topHits(name: String) =  TopHitsAggregationDefinition(name)
  }

  case object clear {
    def cache(indexes: Iterable[String]): ClearCacheDefinition = ClearCacheDefinition(indexes.toSeq)
    def cache(first: String, rest: String*): ClearCacheDefinition = clearCache(first +: rest)
    def scroll(id: String, ids: String*): ClearScrollDefinition = clearScroll(id +: ids)
    def scroll(ids: Iterable[String]): ClearScrollDefinition = clearScroll(ids)
  }

  def clearCache(first: String, rest: String*): ClearCacheDefinition = clearCache(first +: rest)
  def clearCache(indexes: Iterable[String]): ClearCacheDefinition = ClearCacheDefinition(indexes.toSeq)
  def clearIndex(indexes: String*): ClearCacheDefinition = ClearCacheDefinition(indexes)
  def clearIndex(indexes: Iterable[String]): ClearCacheDefinition = ClearCacheDefinition(indexes.toSeq)
  def clearScroll(id: String, ids: String*): ClearScrollDefinition = ClearScrollDefinition(id +: ids)
  def clearScroll(ids: Iterable[String]): ClearScrollDefinition = ClearScrollDefinition(ids.toSeq)

  case object close {
    def index(index: String): CloseIndexDefinition = CloseIndexDefinition(index)
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

  case object completion {
    def suggestion(name: String) = CompletionSuggestionDefinition(name)
  }
  def completionSuggestion: CompletionSuggestionDefinition = completion suggestion UUID.randomUUID.toString
  def completionSuggestion(name: String): CompletionSuggestionDefinition = completion suggestion name

  case object create {

    def index(name: String) = {
      require(name.nonEmpty, "index name must not be null or empty")
      CreateIndexDefinition(name)
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

  case object delete {
    def id(id: Any): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)
    def index(indexes: String*): DeleteIndexDefinition = index(indexes)
    def index(indexes: Iterable[String]): DeleteIndexDefinition = DeleteIndexDefinition(indexes.toSeq)
    def snapshot(name: String): DeleteSnapshotExpectsIn = new DeleteSnapshotExpectsIn(name)
    def template(name: String) = DeleteIndexTemplateDefinition(name)
  }

  def delete(id: Any): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)

  def deleteIndex(indexes: String*): DeleteIndexDefinition = deleteIndex(indexes)
  def deleteIndex(indexes: Iterable[String]): DeleteIndexDefinition = DeleteIndexDefinition(indexes.toSeq)

  def deleteSnapshot(name: String): DeleteSnapshotExpectsIn = delete snapshot name
  def deleteTemplate(name: String): DeleteIndexTemplateDefinition = delete template name

  case object explain {
    def id(id: String): ExplainExpectsIndex = new ExplainExpectsIndex(id)
  }

  def explain(index: String, `type`: String, id: String) = ExplainDefinition(index, `type`, id)

  case object field extends TypeableFields {
    val name = ""
    def name(name: String): FieldDefinition = FieldDefinition(name)
    def sort(field: String): FieldSortDefinition = FieldSortDefinition(field)
    def stats(fields: String*): FieldStatsDefinition = FieldStatsDefinition(fields = fields)
    def stats(fields: Iterable[String]): FieldStatsDefinition = FieldStatsDefinition(fields = fields.toSeq)
  }

  def field(name: String): FieldDefinition = FieldDefinition(name)
  def field(name: String, ft: AttachmentType.type) = new AttachmentFieldDefinition(name)
  def field(name: String, ft: BinaryType.type) = new BinaryFieldDefinition(name)
  def field(name: String, ft: BooleanType.type) = new BooleanFieldDefinition(name)
  def field(name: String, ft: ByteType.type) = new ByteFieldDefinition(name)
  def field(name: String, ft: CompletionType.type) = new CompletionFieldDefinition(name)
  def field(name: String, ft: DateType.type) = new DateFieldDefinition(name)
  def field(name: String, ft: DoubleType.type) = new DoubleFieldDefinition(name)
  def field(name: String, ft: FloatType.type) = new FloatFieldDefinition(name)
  def field(name: String, ft: GeoPointType.type) = new GeoPointFieldDefinition(name)
  def field(name: String, ft: GeoShapeType.type) = new GeoShapeFieldDefinition(name)
  def field(name: String, ft: IntegerType.type) = new IntegerFieldDefinition(name)
  def field(name: String, ft: IpType.type) = new IpFieldDefinition(name)
  def field(name: String, ft: LongType.type) = new LongFieldDefinition(name)
  def field(name: String, ft: MultiFieldType.type) = new MultiFieldDefinition(name)
  def field(name: String, ft: NestedType.type): NestedFieldDefinition = new NestedFieldDefinition(name)
  def field(name: String, ft: ObjectType.type): ObjectFieldDefinition = new ObjectFieldDefinition(name)
  def field(name: String, ft: ShortType.type) = new ShortFieldDefinition(name)
  def field(name: String, ft: StringType.type) = new StringFieldDefinition(name)
  def field(name: String, ft: TokenCountType.type) = new TokenCountDefinition(name)

  def fieldStats(fields: String*): FieldStatsDefinition = FieldStatsDefinition(fields = fields)
  def fieldStats(fields: Iterable[String]): FieldStatsDefinition = FieldStatsDefinition(fields = fields.toSeq)
  def fieldSort(field: String) = FieldSortDefinition(field)

  case object flush {
    def index(indexes: Iterable[String]): FlushIndexDefinition = FlushIndexDefinition(indexes.toSeq)
    def index(indexes: String*): FlushIndexDefinition = FlushIndexDefinition(indexes)
  }

  def flushIndex(indexes: Iterable[String]): FlushIndexDefinition = flush index indexes
  def flushIndex(indexes: String*): FlushIndexDefinition = flush index indexes

  case object fuzzyCompletion {
    def suggestion(name: String) = FuzzyCompletionSuggestionDefinition(name)
  }
  def fuzzyCompletionSuggestion: FuzzyCompletionSuggestionDefinition = {
    fuzzyCompletionSuggestion(UUID.randomUUID.toString)
  }
  def fuzzyCompletionSuggestion(name: String): FuzzyCompletionSuggestionDefinition = fuzzyCompletion suggestion name

  case object geo {
    def sort(field: String): GeoDistanceSortDefinition = new GeoDistanceSortDefinition(field)
  }
  def geoSort(name: String): GeoDistanceSortDefinition = geo sort name

  case object get {

    def id(id: Any) = {
      require(id.toString.nonEmpty, "id must not be null or empty")
      new GetWithIdExpectsFrom(id.toString)
    }

    def alias(aliases: String*): GetAliasDefinition = GetAliasDefinition(aliases)

    def cluster(stats: StatsKeyword): ClusterStatsDefinition = new ClusterStatsDefinition
    def cluster(health: HealthKeyword): ClusterHealthDefinition = new ClusterHealthDefinition

    def mapping(it: IndexesAndTypes): GetMappingDefinition = GetMappingDefinition(it)

    def segments(indexes: Indexes): GetSegmentsDefinition = getSegments(indexes)
    def segments(first: String, rest: String*): GetSegmentsDefinition = getSegments(first +: rest)

    def settings(indexes: Indexes): GetSettingsDefinition = GetSettingsDefinition(indexes)

    def template(name: String): GetTemplateDefinition = GetTemplateDefinition(name)

    def snapshot(names: Iterable[String]): GetSnapshotsExpectsFrom = new GetSnapshotsExpectsFrom(names.toSeq)
    def snapshot(names: String*): GetSnapshotsExpectsFrom = snapshot(names)
  }

  def get(id: Any): GetWithIdExpectsFrom = new GetWithIdExpectsFrom(id.toString)
  def getAlias(first: String, rest: String*): GetAliasDefinition = GetAliasDefinition(first +: rest)
  def getAlias(aliases: Iterable[String]): GetAliasDefinition = GetAliasDefinition(aliases.toSeq)
  def getMapping(ixTp: IndexAndTypes): GetMappingDefinition = GetMappingDefinition(IndexesAndTypes(ixTp))

  def getSegments(indexes: Indexes): GetSegmentsDefinition = GetSegmentsDefinition(indexes)
  def getSegments(first: String, rest: String*): GetSegmentsDefinition = getSegments(first +: rest)

  def getSettings(indexes: Indexes): GetSettingsDefinition = get settings indexes

  def getSnapshot(names: Iterable[String]): GetSnapshotsExpectsFrom = get snapshot names
  def getSnapshot(names: String*): GetSnapshotsExpectsFrom = get snapshot names

  def getTemplate(name: String): GetTemplateDefinition = get template name

  trait HealthKeyword
  case object health extends HealthKeyword

  case object highlight {
    def field(field: String): HighlightDefinition = HighlightDefinition(field)
  }
  def highlight(field: String): HighlightDefinition = HighlightDefinition(field)

  trait StatsKeyword
  case object stats extends StatsKeyword

  case object index {

    def exists(indexes: Iterable[String]): IndexExistsDefinition = IndexExistsDefinition(indexes.toSeq)
    def exists(indexes: String*): IndexExistsDefinition = IndexExistsDefinition(indexes)

    def into(indexType: IndexAndTypes): IndexDefinition = {
      require(indexType != null, "indexType must not be null or empty")
      new IndexDefinition(indexType.index, indexType.types.head)
    }

    def stats(indexes: Indexes): IndicesStatsDefinition = indexStats(indexes)
    def stats(first: String, rest: String*): IndicesStatsDefinition = indexStats(first +: rest)
  }

  def indexExists(indexes: Iterable[String]): IndexExistsDefinition = IndexExistsDefinition(indexes.toSeq)
  def indexExists(indexes: String*): IndexExistsDefinition = IndexExistsDefinition(indexes)

  def indexInto(indexType: IndexAndTypes): IndexDefinition = {
    require(indexType != null, "indexType must not be null or empty")
    new IndexDefinition(indexType.index, indexType.types.head)
  }

  def indexInto(index: String, `type`: String): IndexDefinition = {
    require(index.nonEmpty, "index must not be null or empty")
    new IndexDefinition(index, `type`)
  }

  def indexStats(indexes: Indexes): IndicesStatsDefinition = IndicesStatsDefinition(indexes)
  def indexStats(first: String, rest: String*): IndicesStatsDefinition = indexStats(first +: rest)

  case object inner {
    def hits(name: String): QueryInnerHitsDefinition = QueryInnerHitsDefinition(name)
    def hit(name: String): InnerHitDefinition = InnerHitDefinition(name)
  }
  def innerHit(name: String): InnerHitDefinition = inner hit name
  def innerHits(name: String): QueryInnerHitsDefinition = inner hits name

  def listTasks(first: String, rest: String*): ListTasksDefinition = listTasks(first +: rest)
  def listTasks(nodeIds: Seq[String]): ListTasksDefinition = ListTasksDefinition(nodeIds)

  def cancelTasks(first: String, rest: String*): CancelTasksDefinition = cancelTasks(first +: rest)
  def cancelTasks(nodeIds: Seq[String]): CancelTasksDefinition = CancelTasksDefinition(nodeIds)

  def pendingClusterTasks(local: Boolean): PendingClusterTasksDefinition = PendingClusterTasksDefinition(local)

  case object mapping {
    def name(name: String): MappingDefinition = {
      require(name.nonEmpty, "mapping name must not be null or empty")
      new MappingDefinition(name)
    }
  }
  def mapping(name: String): MappingDefinition = mapping name name

  def multiget(gets: Iterable[GetDefinition]): MultiGetDefinition = MultiGetDefinition(gets)
  def multiget(gets: GetDefinition*): MultiGetDefinition = MultiGetDefinition(gets)

  case object open {
    def index(index: String): OpenIndexDefinition = OpenIndexDefinition(index)
  }
  def openIndex(index: String): OpenIndexDefinition = open index index

  @deprecated("elasticsearch has renamed this forceMerge", "2.1.0")
  case object optimize {
    @deprecated("elasticsearch has renamed this forceMerge", "2.1.0")
    def index(indexes: Iterable[String]): ForceMergeDefinition = ForceMergeDefinition(indexes.toSeq)
    @deprecated("elasticsearch has renamed this forceMerge", "2.1.0")
    def index(indexes: String*): ForceMergeDefinition = ForceMergeDefinition(indexes.toSeq)
  }

  @deprecated("elasticsearch has renamed this forceMerge", "2.1.0")
  def optimizeIndex(indexes: String*): ForceMergeDefinition = ForceMergeDefinition(indexes)
  @deprecated("elasticsearch has renamed this forceMerge", "2.1.0")
  def optimizeIndex(indexes: Iterable[String]): ForceMergeDefinition = ForceMergeDefinition(indexes.toSeq)

  def forceMerge(first: String, rest: String*): ForceMergeDefinition = forceMerge(first +: rest)
  def forceMerge(indexes: Iterable[String]): ForceMergeDefinition = ForceMergeDefinition(indexes.toSeq)

  case object percolate {
    def in(indexType: IndexAndTypes): PercolateDefinition = PercolateDefinition(IndexesAndTypes(indexType))
  }

  def percolateIn(indexType: IndexAndTypes): PercolateDefinition = percolateIn(IndexesAndTypes(indexType))
  def percolateIn(indexesAndTypes: IndexesAndTypes): PercolateDefinition = PercolateDefinition(indexesAndTypes)

  case object phrase {
    def suggestion(name: String): PhraseSuggestionDefinition = PhraseSuggestionDefinition(name)
  }
  def phraseSuggestion: PhraseSuggestionDefinition = phrase suggestion UUID.randomUUID.toString
  def phraseSuggestion(name: String): PhraseSuggestionDefinition = phrase suggestion name

  case object put {
    def mapping(indexesAndType: IndexesAndType): PutMappingDefinition = new PutMappingDefinition(indexesAndType)
  }
  def putMapping(indexesAndType: IndexesAndType): PutMappingDefinition = new PutMappingDefinition(indexesAndType)

  case object recover {
    def index(indexes: Iterable[String]): IndexRecoveryDefinition = new IndexRecoveryDefinition(indexes.toSeq)
    def index(indexes: String*): IndexRecoveryDefinition = new IndexRecoveryDefinition(indexes)
  }
  def recoverIndex(indexes: String*): IndexRecoveryDefinition = recover index indexes
  def recoverIndex(indexes: Iterable[String]): IndexRecoveryDefinition = recover index indexes

  case object refresh {
    def index(indexes: Iterable[String]): RefreshIndexDefinition = RefreshIndexDefinition(indexes.toSeq)
    def index(indexes: String*): RefreshIndexDefinition = RefreshIndexDefinition(indexes)
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

  case object restore {
    def snapshot(name: String): RestoreSnapshotExpectsFrom = {
      require(name.nonEmpty, "snapshot name must not be null or empty")
      new RestoreSnapshotExpectsFrom(name)
    }
  }
  def restoreSnapshot(name: String): RestoreSnapshotExpectsFrom = restore snapshot name

  case object score {
    def sort: ScoreSortDefinition = ScoreSortDefinition()
  }
  def scoreSort(): ScoreSortDefinition = ScoreSortDefinition()

  case object script {
    def sort(script: ScriptDefinition) = scriptSort(script)
    def field(n: String): ExpectsScript = ExpectsScript(field = n)
  }

  def scriptSort(script: ScriptDefinition) = new {
    def as(`type`: String): ScriptSortDefinition = typed(`type`)
    def typed(`type`: String):ScriptSortDefinition = ScriptSortDefinition(script, `type`)
  }

  case object search {
    def in(indexesTypes: IndexesAndTypes): SearchDefinition = SearchDefinition(indexesTypes)
    def scroll(id: String): SearchScrollDefinition = SearchScrollDefinition(id)
  }

  def search(indexType: IndexAndTypes): SearchDefinition = search in indexType
  def search(indexes: String*): SearchDefinition = SearchDefinition(IndexesAndTypes(indexes))

  def searchScroll(id: String): SearchScrollDefinition = SearchScrollDefinition(id)

  // -- helper methods to create the field definitions --
  def attachmentField(name: String) = field(name).typed(AttachmentType)
  def binaryField(name: String) = field(name).typed(BinaryType)
  def booleanField(name: String) = field(name).typed(BooleanType)
  def byteField(name: String) = field(name).typed(ByteType)
  def completionField(name: String) = field(name).typed(CompletionType)
  def dateField(name: String) = field(name).typed(DateType)
  def doubleField(name: String) = field(name, DoubleType)
  def floatField(name: String) = field(name, FloatType)
  def geopointField(name: String) = field(name, GeoPointType)
  def geoshapeField(name: String) = field(name, GeoShapeType)
  def multiField(name: String) = field(name, MultiFieldType)
  def nestedField(name: String): NestedFieldDefinition = field(name).typed(NestedType)
  def objectField(name: String): ObjectFieldDefinition = field(name).typed(ObjectType)
  def intField(name: String) = field(name, IntegerType)
  def ipField(name: String) = field(name, IpType)
  def longField(name: String) = field(name, LongType)
  def scriptField(n: String): ExpectsScript = ExpectsScript(field = n)
  def scriptField(name: String, script: String): ScriptFieldDefinition = ScriptFieldDefinition(name, script, None, None)
  def shortField(name: String) = field(name, ShortType)
  def stringField(name: String): StringFieldDefinition = field(name, StringType)
  def tokenCountField(name: String) = field(name).typed(TokenCountType)

  def suggestions(suggestions: SuggestionDefinition*): SuggestDefinition = SuggestDefinition(suggestions)
  def suggestions(suggestions: Iterable[SuggestionDefinition]): SuggestDefinition = SuggestDefinition(suggestions.toSeq)

  def dynamicTemplate(name: String): DynamicTemplateExpectsMapping = new DynamicTemplateExpectsMapping(name)
  def dynamicTemplate(name: String, mapping: TypedFieldDefinition): DynamicTemplateDefinition = {
    DynamicTemplateDefinition(name, mapping)
  }

  case object term {
    def suggestion(name: String): TermSuggestionDefinition = TermSuggestionDefinition(name)
  }

  def termVectors(index: String, `type`: String, id: String): TermVectorsDefinition = {
    TermVectorsDefinition(index / `type`, id)
  }

  def termSuggestion: TermSuggestionDefinition = term suggestion UUID.randomUUID.toString
  def termSuggestion(name: String): TermSuggestionDefinition = term suggestion name

  case object timestamp {
    def enabled(en: Boolean): TimestampDefinition = TimestampDefinition(en)
  }
  def timestamp(en: Boolean): TimestampDefinition = TimestampDefinition(en)

  case object types {
    def exist(types: String*): TypesExistExpectsIn = TypesExistExpectsIn(types)
  }
  def typesExist(types: String*): TypesExistExpectsIn = TypesExistExpectsIn(types)

  case object update {
    def id(id: Any): UpdateExpectsIndex = {
      require(id.toString.nonEmpty, "id must not be null or empty")
      new UpdateExpectsIndex(id.toString)
    }
    def settings(index: String): UpdateSettingsDefinition = new UpdateSettingsDefinition(index)
  }
  def update(id: Any): UpdateExpectsIndex = new UpdateExpectsIndex(id.toString)

  case object validate {
    def in(indexType: IndexAndTypes): ValidateDefinition = ValidateDefinition(indexType.index, indexType.types.head)
    def in(value: String): ValidateDefinition = {
      require(value.nonEmpty, "value must not be null or empty")
      in(IndexAndTypes(value))
    }
    def in(index: String, `type`: String): ValidateDefinition = ValidateDefinition(index, `type`)
    def in(tuple: (String, String)): ValidateDefinition = ValidateDefinition(tuple._1, tuple._2)
  }

  def validateIn(indexType: IndexAndTypes): ValidateDefinition = validate in indexType
  def validateIn(value: String): ValidateDefinition = validate in value

  implicit class RichFuture[T](future: Future[T]) {
    def await(implicit duration: Duration = 10.seconds): T = Await.result(future, duration)
  }
}

case class TypesExistExpectsIn(types: Seq[String]) {
  def in(indexes: String*): TypesExistsDefinition = new TypesExistsDefinition(indexes, types)
}

object ElasticDsl extends ElasticDsl
