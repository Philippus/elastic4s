package com.sksamuel.elastic4s

import java.util.UUID

import com.sksamuel.elastic4s.admin.{OpenIndexDefinition, TypesExistsDefinition, RefreshIndexDefinition, IndicesStatsDefinition, IndexExistsDefinition, GetSegmentsDefinition, GetTemplateDefinition, FlushIndexDefinition, DeleteIndexTemplateDefinition, FieldStatsDefinition, ClusterStatsDefinition, ClusterHealthDefinition, ClusterStateDefinition, ClusterSettingsDefinition, CloseIndexDefinition, ClearCacheDefinition, ClusterDsl, SnapshotDsl, IndexTemplateDsl, IndexAdminDsl, FieldStatsDsl}
import com.sksamuel.elastic4s.anaylzers.{TokenFilterDsl, TokenizerDsl, AnalyzerDsl}
import com.sksamuel.elastic4s.mappings.FieldType.{ObjectType, NestedType, TokenCountType, StringType, ShortType, LongType, IpType, IntegerType, GeoShapeType, DateType, DoubleType, GeoPointType, MultiFieldType, FloatType, CompletionType, BooleanType, ByteType, BinaryType, AttachmentType}
import com.sksamuel.elastic4s.mappings.{AttachmentFieldDefinition, BinaryFieldDefinition, BooleanFieldDefinition, ByteFieldDefinition, CompletionFieldDefinition, DateFieldDefinition, DoubleFieldDefinition, FloatFieldDefinition, GeoPointFieldDefinition, GeoShapeFieldDefinition, IntegerFieldDefinition, IpFieldDefinition, LongFieldDefinition, MultiFieldDefinition, ShortFieldDefinition, TokenCountDefinition, ObjectFieldDefinition, NestedFieldDefinition, TimestampDefinition, DynamicTemplateDefinition, StringFieldDefinition, PutMappingDefinition, MappingDefinition, TypeableFields, FieldDefinition, MappingDsl}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/** @author Stephen Samuel */
trait ElasticDsl
  extends IndexDsl
    with AliasesDsl
    with AnalyzerDsl
    with BulkDsl
    with ClusterDsl
    with CountDsl
    with CreateIndexDsl
    with DeleteIndexDsl
    with DeleteDsl
    with FieldStatsDsl
    with ExplainDsl
    with GetDsl
    with IndexAdminDsl
    with IndexRecoveryDsl
    with IndexTemplateDsl
    with MappingDsl
    with MultiGetDsl
    with OptimizeDsl
    with PercolateDsl
    with ReindexDsl
    with ScriptDsl
    with SearchDsl
    with SettingsDsl
    with ScoreDsl
    with ScrollDsl
    with SnapshotDsl
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
    def geohash(name: String) = new GeoHashGridAggregationDefinition(name)
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

  case object completion {
    def suggestion(name: String) = new CompletionSuggestionDefinition(name)
  }
  def completionSuggestion: CompletionSuggestionDefinition = completion suggestion UUID.randomUUID.toString
  def completionSuggestion(name: String): CompletionSuggestionDefinition = completion suggestion name

  case object count {
    def from(index: String): CountDefinition = CountDefinition(IndexesAndTypes(index))
    def from(indexes: String*): CountDefinition = CountDefinition(IndexesAndTypes(indexes))
    def from(indexesAndTypes: IndexesAndTypes): CountDefinition = CountDefinition(indexesAndTypes)
  }

  def countFrom(index: String): CountDefinition = CountDefinition(IndexesAndTypes(index))
  def countFrom(indexes: String*): CountDefinition = CountDefinition(IndexesAndTypes(indexes))
  def countFrom(indexesAndTypes: IndexesAndTypes): CountDefinition = CountDefinition(indexesAndTypes)

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

  case object delete {
    def id(id: Any): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)
    def index(indexes: String*): DeleteIndexDefinition = index(indexes)
    def index(indexes: Iterable[String]): DeleteIndexDefinition = new DeleteIndexDefinition(indexes.toSeq)
    def snapshot(name: String): DeleteSnapshotExpectsIn = new DeleteSnapshotExpectsIn(name)
    def template(name: String) = new DeleteIndexTemplateDefinition(name)
  }

  def delete(id: Any): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)

  def deleteIndex(indexes: String*): DeleteIndexDefinition = deleteIndex(indexes)
  def deleteIndex(indexes: Iterable[String]): DeleteIndexDefinition = new DeleteIndexDefinition(indexes.toSeq)

  def deleteSnapshot(name: String): DeleteSnapshotExpectsIn = delete snapshot name
  def deleteTemplate(name: String): DeleteIndexTemplateDefinition = delete template name

  @deprecated("use explain(index, type, id).query(query)...", "2.0.0")
  case object explain {
    def id(id: String): ExplainExpectsIndex = new ExplainExpectsIndex(id)
  }

  def explain(index: String, `type`: String, id: String) = ExplainDefinition(index, `type`, id)

  case object field extends TypeableFields {
    val name = ""
    @deprecated("use field(name, type)", "2.0.0")
    def name(name: String): FieldDefinition = new FieldDefinition(name)
    @deprecated("use fieldSort(field:String)", "2.0.0")
    def sort(field: String): FieldSortDefinition = FieldSortDefinition(field)
    def stats(fields: String*): FieldStatsDefinition = new FieldStatsDefinition(fields = fields)
    def stats(fields: Iterable[String]): FieldStatsDefinition = new FieldStatsDefinition(fields = fields.toSeq)
  }

  @deprecated("use specific methods for each type, eg longField, stringField", "2.0.0")
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

  def fieldStats(fields: String*): FieldStatsDefinition = new FieldStatsDefinition(fields = fields)
  def fieldStats(fields: Iterable[String]): FieldStatsDefinition = new FieldStatsDefinition(fields = fields.toSeq)
  def fieldSort(field: String) = FieldSortDefinition(field)

  case object flush {
    def index(indexes: Iterable[String]): FlushIndexDefinition = new FlushIndexDefinition(indexes.toSeq)
    def index(indexes: String*): FlushIndexDefinition = new FlushIndexDefinition(indexes)
  }

  def flushIndex(indexes: Iterable[String]): FlushIndexDefinition = flush index indexes
  def flushIndex(indexes: String*): FlushIndexDefinition = flush index indexes

  case object fuzzyCompletion {
    def suggestion(name: String) = new FuzzyCompletionSuggestionDefinition(name)
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

    def alias(aliases: String*): GetAliasDefinition = new GetAliasDefinition(aliases)

    def cluster(stats: StatsKeyword): ClusterStatsDefinition = new ClusterStatsDefinition
    def cluster(health: HealthKeyword): ClusterHealthDefinition = new ClusterHealthDefinition

    def mapping(it: IndexesAndTypes): GetMappingDefinition = GetMappingDefinition(it)

    def segments(indexes: Indexes): GetSegmentsDefinition = new GetSegmentsDefinition(indexes)

    def settings(indexes: Indexes): GetSettingsDefinition = new GetSettingsDefinition(indexes)

    def template(name: String): GetTemplateDefinition = new GetTemplateDefinition(name)

    def snapshot(names: Iterable[String]): GetSnapshotsExpectsFrom = new GetSnapshotsExpectsFrom(names.toSeq)
    def snapshot(names: String*): GetSnapshotsExpectsFrom = snapshot(names)
  }

  def get(id: Any): GetWithIdExpectsFrom = new GetWithIdExpectsFrom(id.toString)
  def getAlias(aliases: String*): GetAliasDefinition = new GetAliasDefinition(aliases)
  def getMapping(ixTp: IndexAndTypes): GetMappingDefinition = new GetMappingDefinition(IndexesAndTypes(ixTp))

  def getSegments(indexes: Indexes): GetSegmentsDefinition = get segments indexes

  def getSettings(indexes: Indexes): GetSettingsDefinition = get settings indexes

  def getSnapshot(names: Iterable[String]): GetSnapshotsExpectsFrom = get snapshot names
  def getSnapshot(names: String*): GetSnapshotsExpectsFrom = get snapshot names

  def getTemplate(name: String): GetTemplateDefinition = get template name

  trait HealthKeyword
  case object health extends HealthKeyword

  @deprecated("use highlight(field)", "2.0.0")
  case object highlight {
    @deprecated("use highlight(field)", "2.0.0")
    def field(field: String): HighlightDefinition = HighlightDefinition(field)
  }
  def highlight(field: String): HighlightDefinition = HighlightDefinition(field)

  trait StatsKeyword
  case object stats extends StatsKeyword

  case object index {

    def exists(indexes: Iterable[String]): IndexExistsDefinition = new IndexExistsDefinition(indexes.toSeq)
    def exists(indexes: String*): IndexExistsDefinition = new IndexExistsDefinition(indexes)

    def into(indexType: IndexAndTypes): IndexDefinition = {
      require(indexType != null, "indexType must not be null or empty")
      new IndexDefinition(indexType.index, indexType.types.head)
    }

    def stats(indexes: Iterable[String]): IndicesStatsDefinition = new IndicesStatsDefinition(indexes.toSeq)
    def stats(indexes: String*): IndicesStatsDefinition = new IndicesStatsDefinition(indexes)
  }

  def indexExists(indexes: Iterable[String]): IndexExistsDefinition = new IndexExistsDefinition(indexes.toSeq)
  def indexExists(indexes: String*): IndexExistsDefinition = new IndexExistsDefinition(indexes)

  def indexInto(indexType: IndexAndTypes): IndexDefinition = {
    require(indexType != null, "indexType must not be null or empty")
    new IndexDefinition(indexType.index, indexType.types.head)
  }

  def indexInto(index: String, `type`: String): IndexDefinition = {
    require(index.nonEmpty, "index must not be null or empty")
    new IndexDefinition(index, `type`)
  }

  def indexStats(indexes: Iterable[String]): IndicesStatsDefinition = new IndicesStatsDefinition(indexes.toSeq)
  def indexStats(indexes: String*): IndicesStatsDefinition = indexStats(indexes)

  case object inner {
    def hits(name: String): QueryInnerHitsDefinition = new QueryInnerHitsDefinition(name)
    def hit(name: String): InnerHitDefinition = new InnerHitDefinition(name)
  }
  def innerHit(name: String): InnerHitDefinition = inner hit name
  def innerHits(name: String): QueryInnerHitsDefinition = inner hits name

  case object mapping {
    def name(name: String): MappingDefinition = {
      require(name.nonEmpty, "mapping name must not be null or empty")
      new MappingDefinition(name)
    }
  }
  def mapping(name: String): MappingDefinition = mapping name name

  def multiget(gets: Iterable[GetDefinition]): MultiGetDefinition = new MultiGetDefinition(gets)
  def multiget(gets: GetDefinition*): MultiGetDefinition = new MultiGetDefinition(gets)

  case object open {
    def index(index: String): OpenIndexDefinition = new OpenIndexDefinition(index)
  }
  def openIndex(index: String): OpenIndexDefinition = open index index

  @deprecated("use optimizeIndex", "2.0.0")
  case object optimize {
    @deprecated("use optimizeIndex", "2.0.0")
    def index(indexes: Iterable[String]): OptimizeDefinition = OptimizeDefinition(indexes.toSeq)
    @deprecated("use optimizeIndex", "2.0.0")
    def index(indexes: String*): OptimizeDefinition = OptimizeDefinition(indexes.toSeq)
  }

  def optimizeIndex(indexes: String*): OptimizeDefinition = OptimizeDefinition(indexes)
  def optimizeIndex(indexes: Iterable[String]): OptimizeDefinition = OptimizeDefinition(indexes.toSeq)

  @deprecated("use percolate", "2.0.0")
  case object percolate {
    @deprecated("use percolate", "2.0.0")
    def in(indexType: IndexAndTypes): PercolateDefinition = PercolateDefinition(IndexesAndTypes(indexType))
  }

  def percolateIn(indexType: IndexAndTypes): PercolateDefinition = percolateIn(IndexesAndTypes(indexType))
  def percolateIn(indexesAndTypes: IndexesAndTypes): PercolateDefinition = PercolateDefinition(indexesAndTypes)

  case object phrase {
    def suggestion(name: String): PhraseSuggestionDefinition = new PhraseSuggestionDefinition(name)
  }
  def phraseSuggestion: PhraseSuggestionDefinition = phrase suggestion UUID.randomUUID.toString
  def phraseSuggestion(name: String): PhraseSuggestionDefinition = phrase suggestion name

  case object put {
    def mapping(indexType: IndexAndTypes): PutMappingDefinition = new PutMappingDefinition(indexType)
  }
  def putMapping(indexType: IndexAndTypes): PutMappingDefinition = new PutMappingDefinition(indexType)

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

  case object restore {
    def snapshot(name: String): RestoreSnapshotExpectsFrom = {
      require(name.nonEmpty, "snapshot name must not be null or empty")
      new RestoreSnapshotExpectsFrom(name)
    }
  }
  def restoreSnapshot(name: String): RestoreSnapshotExpectsFrom = restore snapshot name

  @deprecated("use scoreSort()", "2.0.0")
  case object score {
    @deprecated("use scoreSort()", "2.0.0")
    def sort: ScoreSortDefinition = ScoreSortDefinition()
  }
  def scoreSort(): ScoreSortDefinition = ScoreSortDefinition()

  case object script {
    @deprecated("use scriptSort(script)", "2.0.0")
    def sort(script: String): ScriptSortDefinition = new ScriptSortDefinition(script)
    @deprecated("use scriptField(script)", "2.0.0")
    def field(n: String): ExpectsScript = ExpectsScript(field = n)
  }
  def scriptSort(scriptText: String): ScriptSortDefinition = ScriptSortDefinition(scriptText)

  case object search {
    def in(indexesTypes: IndexesAndTypes): SearchDefinition = new SearchDefinition(indexesTypes)
    def scroll(id: String): SearchScrollDefinition = new SearchScrollDefinition(id)
  }

  def search(indexType: IndexAndTypes): SearchDefinition = search in indexType
  def search(indexes: String*): SearchDefinition = new SearchDefinition(IndexesAndTypes(indexes))

  def searchScroll(id: String): SearchScrollDefinition = new SearchScrollDefinition(id)

  // -- helper methods to create the field definitions --
  def attachmentField(name: String) = field(name).typed(AttachmentType)
  def binaryField(name: String) = field(name).typed(BinaryType)
  def booleanField(name: String) = field(name).typed(BooleanType)
  def byteField(name: String) = field(name).typed(ByteType)
  def completionField(name: String) = field(name).typed(CompletionType)
  def dateField(name: String) = field(name).typed(DateType)
  def doubleField(name: String) = field(name).typed(DoubleType)
  def floatField(name: String) = field(name).typed(FloatType)
  def geopointField(name: String) = field(name).typed(GeoPointType)
  def geoshapeField(name: String) = field(name).typed(GeoShapeType)
  def multiField(name: String) = field(name).typed(MultiFieldType)
  def nestedField(name: String): NestedFieldDefinition = field(name).typed(NestedType)
  def objectField(name: String): ObjectFieldDefinition = field(name).typed(ObjectType)
  def intField(name: String) = field(name).typed(IntegerType)
  def ipField(name: String) = field(name).typed(IpType)
  def longField(name: String) = field(name).typed(LongType)
  def scriptField(n: String): ExpectsScript = ExpectsScript(field = n)
  def scriptField(n: String, script: String): ScriptFieldDefinition = ScriptFieldDefinition(field = n, script)
  def shortField(name: String) = field(name).typed(ShortType)
  def stringField(name: String): StringFieldDefinition = field(name).typed(StringType)
  def tokenCountField(name: String) = field(name).typed(TokenCountType)

  def suggestions(suggestions: SuggestionDefinition*): SuggestDefinition = SuggestDefinition(suggestions)
  def suggestions(suggestions: Iterable[SuggestionDefinition]): SuggestDefinition = SuggestDefinition(suggestions.toSeq)

  case object template {
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

object ElasticDsl extends ElasticDsl