package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.admin._
import com.sksamuel.elastic4s.alias._
import com.sksamuel.elastic4s.analyzers.{AnalyzerDsl, TokenFilterDsl, TokenizerDsl}
import com.sksamuel.elastic4s.get.{GetDsl, MultiGetApi}
import com.sksamuel.elastic4s.mappings.FieldType._
import com.sksamuel.elastic4s.mappings._
import com.sksamuel.elastic4s.search.query.InnerHitDefinition
import com.sksamuel.elastic4s.search.{PercolateDsl, SearchDsl}
import com.sksamuel.elastic4s.sort.SortDsl

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.implicitConversions

trait ElasticDsl
  extends AliasesDsl
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
    with IndexDsl
    with IndexAdminDsl
    with IndexRecoveryDsl
    with IndexTemplateDsl
    with MappingDsl
    with MultiGetApi
    with PercolateDsl
    with ScriptDsl
    with SearchDsl
    with SettingsDsl
    with ScoreDsl
    with ScrollDsl
    with SortDsl
    with SnapshotDsl
    with TaskApi
    with TermVectorApi
    with TokenizerDsl
    with TokenFilterDsl
    with UpdateDsl
    with ValidateDsl
    with DeprecatedElasticDsl
    with ElasticImplicits {

  //  def agg = aggregation
  //  case object aggregation {
  //    def avg(name: String) = AvgAggregationDefinition(name)
  //    def children(name: String) = ChildrenAggregationDefinition(name)
  //    def count(name: String) = ValueCountAggregationDefinition(name)
  //    def cardinality(name: String) = CardinalityAggregationDefinition(name)
  //    def datehistogram(name: String) = DateHistogramAggregation(name)
  //    def daterange(name: String) = DateRangeAggregation(name)
  //    def extendedstats(name: String) = ExtendedStatsAggregationDefinition(name)
  //    def filter(name: String) = FilterAggregationDefinition(name)
  //    def filters(name: String) = FiltersAggregationDefinition(name)
  //    def geobounds(name: String) = GeoBoundsAggregationDefinition(name)
  //    def geodistance(name: String) = GeoDistanceAggregationDefinition(name)
  //    def geohash(name: String) = GeoHashGridAggregationDefinition(name)
  //    def global(name: String) = GlobalAggregationDefinition(name)
  //    def histogram(name: String) = HistogramAggregation(name)
  //    def ipRange(name: String) = IpRangeAggregationDefinition(name)
  //    def max(name: String) = MaxAggregationDefinition(name)
  //    def min(name: String) = MinAggregationDefinition(name)
  //    def missing(name: String) = MissingAggregationDefinition(name)
  //    def nested(name: String) = NestedAggregationDefinition(name)
  //    def reverseNested(name: String) = ReverseNestedAggregationDefinition(name)
  //    def percentiles(name: String) = PercentilesAggregationDefinition(name)
  //    def percentileranks(name: String) = PercentileRanksAggregationDefinition(name)
  //    def range(name: String) = RangeAggregationDefinition(name)
  //    def scriptedMetric(name: String) = ScriptedMetricAggregationDefinition(name)
  //    def sigTerms(name: String) = SigTermsAggregationDefinition(name)
  //    def stats(name: String) = StatsAggregationDefinition(name)
  //    def sum(name: String) = SumAggregationDefinition(name)
  //    def terms(name: String) = TermAggregationDefinition(name)
  //    def topHits(name: String) =  TopHitsAggregationDefinition(name)
  //  }

  def clearCache(first: String, rest: String*): ClearCacheDefinition = clearCache(first +: rest)
  def clearCache(indexes: Iterable[String]): ClearCacheDefinition = ClearCacheDefinition(indexes.toSeq)
  def clearIndex(indexes: String*): ClearCacheDefinition = ClearCacheDefinition(indexes)
  def clearIndex(indexes: Iterable[String]): ClearCacheDefinition = ClearCacheDefinition(indexes.toSeq)
  def clearScroll(id: String, ids: String*): ClearScrollDefinition = ClearScrollDefinition(id +: ids)
  def clearScroll(ids: Iterable[String]): ClearScrollDefinition = ClearScrollDefinition(ids.toSeq)

  def clusterPersistentSettings(settings: Map[String, String]) = ClusterSettingsDefinition(settings, Map.empty)
  def clusterTransientSettings(settings: Map[String, String]) = ClusterSettingsDefinition(Map.empty, settings)

  def clusterState() = new ClusterStateDefinition
  def clusterHealth() = new ClusterHealthDefinition()
  def clusterStats() = new ClusterStatsDefinition

  def clusterHealth(indices: String*) = new ClusterHealthDefinition(indices: _*)

  //  def completionSuggestion(): CompletionSuggestionDefinition = completionSuggestion(UUID.randomUUID.toString)
  //  def completionSuggestion(name: String): CompletionSuggestionDefinition = CompletionSuggestionDefinition(name)

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

  def flushIndex(indexes: Iterable[String]): FlushIndexDefinition = FlushIndexDefinition(indexes.toSeq)
  def flushIndex(indexes: String*): FlushIndexDefinition = flushIndex(indexes)

  //  def fuzzyCompletionSuggestion(): FuzzyCompletionSuggestionDefinition =
  //    fuzzyCompletionSuggestion(UUID.randomUUID.toString)
  //
  //  def fuzzyCompletionSuggestion(name: String): FuzzyCompletionSuggestionDefinition =
  //    FuzzyCompletionSuggestionDefinition(name)

  def getMapping(ixTp: IndexAndTypes): GetMappingDefinition = GetMappingDefinition(IndexesAndTypes(ixTp))

  def getSegments(indexes: Indexes): GetSegmentsDefinition = GetSegmentsDefinition(indexes)
  def getSegments(first: String, rest: String*): GetSegmentsDefinition = getSegments(first +: rest)

  def getSettings(indexes: Indexes): GetSettingsDefinition = GetSettingsDefinition(indexes)

  def highlight(field: String): HighlightDefinition = HighlightDefinition(field)

  def indexExists(indexes: Iterable[String]): IndexExistsDefinition = IndexExistsDefinition(indexes.toSeq)
  def indexExists(indexes: String*): IndexExistsDefinition = IndexExistsDefinition(indexes)

  def innerHit(name: String): InnerHitDefinition = InnerHitDefinition(name)

  def listTasks(first: String, rest: String*): ListTasksDefinition = listTasks(first +: rest)
  def listTasks(nodeIds: Seq[String]): ListTasksDefinition = ListTasksDefinition(nodeIds)

  def cancelTasks(first: String, rest: String*): CancelTasksDefinition = cancelTasks(first +: rest)
  def cancelTasks(nodeIds: Seq[String]): CancelTasksDefinition = CancelTasksDefinition(nodeIds)

  def pendingClusterTasks(local: Boolean): PendingClusterTasksDefinition = PendingClusterTasksDefinition(local)

  def mapping(name: String): MappingDefinition = new MappingDefinition(name)


  //  def phraseSuggestion(): PhraseSuggestionDefinition = PhraseSuggestionDefinition(UUID.randomUUID.toString)
  //  def phraseSuggestion(name: String): PhraseSuggestionDefinition = PhraseSuggestionDefinition(name)

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

  //  def suggestions(suggestions: SuggestionDefinition*): SuggestDefinition = SuggestDefinition(suggestions)
  //  def suggestions(suggestions: Iterable[SuggestionDefinition]): SuggestDefinition = SuggestDefinition(suggestions.toSeq)

  def dynamicTemplate(name: String) = new {
    def mapping(mapping: TypedFieldDefinition) = DynamicTemplateDefinition(name, mapping)
  }

  def dynamicTemplate(name: String, mapping: TypedFieldDefinition): DynamicTemplateDefinition = {
    DynamicTemplateDefinition(name, mapping)
  }

  def termVectors(index: String, `type`: String, id: String) = TermVectorsDefinition(index / `type`, id)

  //  def termSuggestion(): TermSuggestionDefinition = TermSuggestionDefinition(UUID.randomUUID.toString)
  //  def termSuggestion(name: String): TermSuggestionDefinition = TermSuggestionDefinition(name)

  def timestamp(en: Boolean): TimestampDefinition = TimestampDefinition(en)

  implicit class RichFuture[T](future: Future[T]) {
    def await(implicit duration: Duration = 10.seconds): T = Await.result(future, duration)
  }
}

object ElasticDsl extends ElasticDsl
