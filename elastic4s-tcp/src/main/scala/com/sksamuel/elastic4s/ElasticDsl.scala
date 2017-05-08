package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.admin._
import com.sksamuel.elastic4s.alias.{AliasExecutables, GetAliasDefinition}
import com.sksamuel.elastic4s.analyzers._
import com.sksamuel.elastic4s.bulk.BulkExecutables
import com.sksamuel.elastic4s.cluster.{ClusterHealthDefinition, ClusterSettingsDefinition, ClusterStatsDefinition}
import com.sksamuel.elastic4s.delete.DeleteExecutables
import com.sksamuel.elastic4s.explain.{ExplainDefinition, ExplainExecutables}
import com.sksamuel.elastic4s.get.GetExecutables
import com.sksamuel.elastic4s.index.{CreateIndexExecutables, DeleteIndexExecutables, IndexExecutables, IndexTemplateExecutables}
import com.sksamuel.elastic4s.index.admin.ForceMergeExecutables
import com.sksamuel.elastic4s.indexes._
import com.sksamuel.elastic4s.indexes.admin.IndexRecoveryDefinition
import com.sksamuel.elastic4s.mappings.FieldType._
import com.sksamuel.elastic4s.mappings._
import com.sksamuel.elastic4s.reindex.ReindexExecutables
import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.search.{ScrollExecutables, SearchImplicits}
import com.sksamuel.elastic4s.searches._
import com.sksamuel.elastic4s.searches.aggs._
import com.sksamuel.elastic4s.searches.queries._
import com.sksamuel.elastic4s.searches.sort.{FieldSortDefinition, ScoreSortDefinition}
import com.sksamuel.elastic4s.task.TaskExecutables
import com.sksamuel.elastic4s.termvectors.TermVectorsExecutables
import com.sksamuel.elastic4s.update.UpdateExecutables
import com.sksamuel.elastic4s.validate.ValidateExecutables
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.search.sort.SortOrder

import scala.language.implicitConversions

// the entry point for TCP users. This is the trait that should be mixed in, or use the object
// version and import it. The name ElasticDsl is kept for backwards compatibility.
trait ElasticDsl
  extends ElasticApi
    with FieldStatsDsl
    with PercolateDsl
    with SettingsDsl
    with SnapshotDsl
    with TokenFilterDsl
    with AliasExecutables
    with BulkExecutables
    with ClusterExecutables
    with CreateIndexExecutables
    with DeleteExecutables
    with DeleteIndexExecutables
    with ExplainExecutables
    with ForceMergeExecutables
    with GetExecutables
    with IndexAdminExecutables
    with IndexExecutables
    with IndexRecoveryExecutables
    with IndexTemplateExecutables
    with MappingExecutables
    with ReindexExecutables
    with ScrollExecutables
    with SearchImplicits
    with TaskExecutables
    with TermVectorsExecutables
    with UpdateExecutables
    with ValidateExecutables
    with BuildableTermsQueryImplicits
    with ElasticImplicits {

  implicit def toRichResponse(resp: SearchResponse): RichSearchResponse = RichSearchResponse(resp)

  @deprecated("Use xxxAggregation(...) methods", "5.0.0")
  def agg: aggregation.type = aggregation

  @deprecated("Use xxxAggregation(...) methods", "5.0.0")
  case object aggregation {

    def avg(name: String) = AvgAggregationDefinition(name)

    @deprecated("Use valueCountAggregation(...)", "5.0.0")
    def count(name: String): ValueCountAggregationDefinition = valueCountAggregation(name)

    @deprecated("Use cardinalityAggregation(...)", "5.0.0")
    def cardinality(name: String): CardinalityAggregationDefinition = cardinalityAggregation(name)

    @deprecated("Use dateHistogramAggregation(...)", "5.0.0")
    def datehistogram(name: String): DateHistogramAggregation = dateHistogramAggregation(name)

    @deprecated("Use dateRangeAggregation(...)", "5.0.0")
    def daterange(name: String): DateRangeAggregation = dateRangeAggregation(name)

    @deprecated("Use extendedStatsAggregation(...)", "5.0.0")
    def extendedstats(name: String) = extendedStatsAggregation(name)

    @deprecated("Use filterAggregation(...)", "5.0.0")
    def filter(name: String) = filterAggregation(name)

    @deprecated("Use filtersAggregation(...)", "5.0.0")
    def filters(name: String) = filtersAggregation(name)

    @deprecated("Use geoBoundsAggregation(...)", "5.0.0")
    def geobounds(name: String) = geoBoundsAggregation(name)

    @deprecated("Use geoDistanceAggregation(...)", "5.0.0")
    def geodistance(name: String) = geoDistanceAggregation(name)

    @deprecated("Use geoHashGridAggregation(...)", "5.0.0")
    def geohash(name: String) = geoHashGridAggregation(name)

    @deprecated("Use globalAggregation(...)", "5.0.0")
    def global(name: String) = globalAggregation(name)

    @deprecated("Use histogramAggregation(...)", "5.0.0")
    def histogram(name: String) = histogramAggregation(name)

    @deprecated("Use ipRangeAggregation(...)", "5.0.0")
    def ipRange(name: String) = ipRangeAggregation(name)

    @deprecated("Use maxAggregation(...)", "5.0.0")
    def max(name: String): MaxAggregationDefinition = maxAggregation(name)

    @deprecated("Use minAggregation(...)", "5.0.0")
    def min(name: String): MinAggregationDefinition = minAggregation(name)

    @deprecated("Use nestedAggregation(...)", "5.0.0")
    def nested(name: String) = new {
      def path(path: String) = nestedAggregation(name, path)
    }

    @deprecated("Use missingAggregation(...)", "5.0.0")
    def missing(name: String): MissingAggregationDefinition = missingAggregation(name)

    @deprecated("Use reverseNestedAggregation(...)", "5.0.0")
    def reverseNested(name: String): ReverseNestedAggregationDefinition = reverseNestedAggregation(name)

    @deprecated("Use percentilesAggregation(...)", "5.0.0")
    def percentiles(name: String) = percentilesAggregation(name)

    @deprecated("Use percentileRanksAggregation(...)", "5.0.0")
    def percentileranks(name: String) = percentileRanksAggregation(name)

    @deprecated("Use rangeAggregation(...)", "5.0.0")
    def range(name: String) = rangeAggregation(name)

    @deprecated("Use scriptedMetricAggregation(...)", "5.0.0")
    def scriptedMetric(name: String) = scriptedMetricAggregation(name)

    @deprecated("Use sigTermsAggregation(...)", "5.0.0")
    def sigTerms(name: String) = sigTermsAggregation(name)

    @deprecated("Use statsAggregation(...)", "5.0.0")
    def stats(name: String) = statsAggregation(name)

    @deprecated("Use sumAggregation(...)", "5.0.0")
    def sum(name: String) = sumAggregation(name)

    @deprecated("Use termsAggregation(...)", "5.0.0")
    def terms(name: String) = termsAggregation(name)

    @deprecated("Use topHitsAggregation(...)", "5.0.0")
    def topHits(name: String) = topHitsAggregation(name)
  }

  def innerHit(name: String): InnerHitDefinition = InnerHitDefinition(name)

  case object add {
    @deprecated("Use full method syntax, eg addAlias()", "5.0.0")
    def alias(alias: String) = addAlias(alias)
  }

  case object update {
    @deprecated("use update(id)", "5.0.0")
    def id(id: Any) = update(id)

    @deprecated("use updateSettings(index)", "5.0.0")
    def settings(index: String): UpdateSettingsDefinition = updateSettings(index)
  }

  case object types {
    @deprecated("use typesExist(types)", "5.0.0")
    def exist(types: String*) = typesExist(types)
  }

  case object restore {
    @deprecated("use restoreSnapshot(name)", "5.0.0")
    def snapshot(name: String) = restoreSnapshot(name)
  }

  case object search {
    @deprecated("use search(index) or search(indexes/types)", "5.0.0")
    def in(indexesTypes: IndexesAndTypes): SearchDefinition = SearchDefinition(indexesTypes)
    @deprecated("use searchScroll(id)", "5.0.0")
    def scroll(id: String): SearchScrollDefinition = SearchScrollDefinition(id)
  }

  case object term {
    @deprecated("use termSuggestion(name)", "5.0.0")
    def suggestion(name: String) = termSuggestion(name)
  }

  case object score {
    @deprecated("use scoreSort()", "5.0.0")
    def sort = new {
      def order(order: SortOrder): ScoreSortDefinition = ScoreSortDefinition(order)
    }
  }

  @deprecated("use putMapping(index)", "5.0.0")
  case object put {
    @deprecated("use putMapping(index)", "5.0.0")
    def mapping(indexAndType: IndexAndType): PutMappingDefinition = PutMappingDefinition(indexAndType)
  }

  @deprecated("use phraseSuggestion(name)", "5.0.0")
  case object phrase {
    def suggestion(name: String): PhraseSuggExpectsField = phraseSuggestion(name)
  }

  case object remove {
    @deprecated("Use dot syntax, eg removeAlias(alias", "5.0.0")
    def alias(alias: String): RemoveAliasExpectsOn = removeAlias(alias)
  }

  @deprecated("use recoverIndex(index)", "5.0.0")
  case object recover {
    @deprecated("use putMapping(index)", "5.0.0")
    def index(indexes: Iterable[String]): IndexRecoveryDefinition = IndexRecoveryDefinition(indexes.toSeq)
    @deprecated("use putMapping(index)", "5.0.0")
    def index(indexes: String*): IndexRecoveryDefinition = IndexRecoveryDefinition(indexes)
  }

  @deprecated("use refreshIndex(index)", "5.0.0")
  case object refresh {
    @deprecated("use refreshIndex(index)", "5.0.0")
    def index(indexes: Iterable[String]): RefreshIndexDefinition = RefreshIndexDefinition(indexes.toSeq)
    @deprecated("use refreshIndex(index)", "5.0.0")
    def index(indexes: String*): RefreshIndexDefinition = RefreshIndexDefinition(indexes)
  }

  case object mapping {
    @deprecated("use mapping(name)", "5.0.0")
    def name(name: String): MappingDefinition = {
      require(name.nonEmpty, "mapping name must not be null or empty")
      MappingDefinition(`type` = name)
    }
  }

  @deprecated("use openIndex(index)", "5.0.0")
  case object open {
    def index(index: String): OpenIndexDefinition = OpenIndexDefinition(index)
  }

  @deprecated("use commonQuery(field", "5.0.0")
  def commonQuery = new CommonQueryExpectsField

  class CommonQueryExpectsField {
    def field(name: String) = new CommonQueryExpectsText(name)
  }

  def fuzzyQuery(name: String, value: Any) = FuzzyQueryDefinition(name, value)

  @deprecated("instead search on the `_index` field", "5.0.0")
  def indicesQuery(indices: String*) = new {
    @deprecated("instead search on the `_index` field", "5.0.0")
    def query(query: QueryDefinition) = IndicesQueryDefinition(indices, query)
  }

  case object create {

    @deprecated("use createIndex(name)", "5.0.0")
    def index(name: String) = CreateIndexDefinition(name)

    @deprecated("use createSnapshot(name)", "5.0.0")
    def snapshot(name: String): CreateSnapshotExpectsIn = createSnapshot(name)

    @deprecated("use createRepository(name)", "5.0.0")
    def repository(name: String): CreateRepositoryExpectsType = createRepository(name)

    @deprecated("use createTemplate(name)", "5.0.0")
    def template(name: String): CreateIndexTemplateExpectsPattern = createTemplate(name)
  }

  case object delete {
    @deprecated("use delete(id)", "5.0.0")
    def id(id: Any): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)

    @deprecated("use deleteIndex(indexes)", "5.0.0")
    def index(indexes: String*): DeleteIndexDefinition = deleteIndex(indexes)

    @deprecated("use deleteIndex(indexes)", "5.0.0")
    def index(indexes: Iterable[String]): DeleteIndexDefinition = DeleteIndexDefinition(indexes.toSeq)

    @deprecated("use deleteSnapshot(name)", "5.0.0")
    def snapshot(name: String): DeleteSnapshotExpectsIn = deleteSnapshot(name)

    @deprecated("use deleteTemplate(name)", "5.0.0")
    def template(name: String) = DeleteIndexTemplateDefinition(name)
  }

  case object cluster {
    @deprecated("use clusterPersistentSettings(settings)", "5.0.0")
    def persistentSettings(settings: Map[String, String]) = ClusterSettingsDefinition(settings, Map.empty)
    @deprecated("use clusterTransientSettings(settings)", "5.0.0")
    def transientSettings(settings: Map[String, String]) = ClusterSettingsDefinition(Map.empty, settings)
  }

  case object script {
    @deprecated("use scriptSort(script).typed(ScriptSortType)", "5.0.0")
    def sort(script: ScriptDefinition): ScriptSortExpectsType = scriptSort(script)
    @deprecated("use scriptField(name)", "5.0.0")
    def field(n: String): ExpectsScript = ExpectsScript(field = n)
  }

  trait HealthKeyword
  case object health extends HealthKeyword

  trait StatsKeyword
  case object stats extends StatsKeyword

  case object highlight {
    @deprecated("use highlight(field)", "5.0.0")
    def field(field: String): HighlightFieldDefinition = HighlightFieldDefinition(field)
  }

  case object index {

    def exists(index: String): IndexExistsDefinition = IndexExistsDefinition(index)

    @deprecated("use indexInto(index / type)", "5.0.0")
    def into(indexType: IndexAndTypes): IndexDefinition = IndexDefinition(IndexAndType(indexType.index, indexType.types.head))

    @deprecated("use indexStats(indexes)", "5.0.0")
    def stats(indexes: Indexes): IndicesStatsDefinition = indexStats(indexes)

    @deprecated("use indexStats(indexes)", "5.0.0")
    def stats(first: String, rest: String*): IndicesStatsDefinition = indexStats(first +: rest)
  }

  case object flush {
    @deprecated("use flushIndex(indexes)", "5.0.0")
    def index(indexes: Iterable[String]): FlushIndexDefinition = FlushIndexDefinition(indexes.toSeq)
    @deprecated("use flushIndex(indexes)", "5.0.0")
    def index(indexes: String*): FlushIndexDefinition = FlushIndexDefinition(indexes)
  }

  case object get {

    @deprecated("use get(id)", "5.0.0")
    def id(id: Any) = get(id)

    @deprecated("use getAlias(alias)", "5.0.0")
    def alias(aliases: String*): GetAliasDefinition = GetAliasDefinition(aliases)

    @deprecated("use clusterStats()", "5.0.0")
    def cluster(stats: StatsKeyword): ClusterStatsDefinition = ClusterStatsDefinition()

    @deprecated("use clusterHealth()", "5.0.0")
    def cluster(health: HealthKeyword): ClusterHealthDefinition = clusterHealth()

    @deprecated("use getMapping(indexes)", "5.0.0")
    def mapping(it: IndexesAndTypes): GetMappingDefinition = GetMappingDefinition(it)

    @deprecated("use getSegments(indexes)", "5.0.0")
    def segments(indexes: Indexes): GetSegmentsDefinition = getSegments(indexes)

    @deprecated("use getSegments(indexes)", "5.0.0")
    def segments(first: String, rest: String*): GetSegmentsDefinition = getSegments(first +: rest)

    @deprecated("use getSettings(indexes)", "5.0.0")
    def settings(indexes: Indexes): GetSettingsDefinition = GetSettingsDefinition(indexes)

    @deprecated("use getTemplate(name)", "5.0.0")
    def template(name: String): GetIndexTemplateDefinition = GetIndexTemplateDefinition(name)

    @deprecated("use getSnapshot(names)", "5.0.0")
    def snapshot(names: Iterable[String]): GetSnapshotExpectsFrom = getSnapshot(names.toSeq)

    @deprecated("use getSnapshot(names)", "5.0.0")
    def snapshot(names: String*): GetSnapshotExpectsFrom = getSnapshot(names)
  }

  case object close {
    @deprecated("use closeIndex(index)", "5.0.0")
    def index(index: String): CloseIndexDefinition = CloseIndexDefinition(index)
  }

  case object timestamp {
    @deprecated("use timestamp(boolean)", "5.0.0")
    def enabled(en: Boolean): TimestampDefinition = TimestampDefinition(en)
  }

  case object clear {
    @deprecated("use clearCache(indexes)", "5.0.0")
    def cache(indexes: Iterable[String]): ClearCacheDefinition = ClearCacheDefinition(indexes.toSeq)
    @deprecated("use clearCache(indexes)", "5.0.0")
    def cache(first: String, rest: String*): ClearCacheDefinition = clearCache(first +: rest)
    @deprecated("use clearScroll(ids)", "5.0.0")
    def scroll(id: String, ids: String*): ClearScrollDefinition = clearScroll(id +: ids)
    @deprecated("use clearScroll(ids)", "5.0.0")
    def scroll(ids: Iterable[String]): ClearScrollDefinition = clearScroll(ids)
  }

  case object completion {
    @deprecated("use completionSuggestion(name)", "5.0.0")
    def suggestion(name: String): CompletionSuggExpectsField = completionSuggestion(name)
  }

  case object explain {
    @deprecated("Use explain(index, type, id", "5.0.0")
    def id(id: String) = new {
      def in(indexAndTypes: IndexAndTypes): ExplainDefinition = {
        ExplainDefinition(IndexAndType(indexAndTypes.index, indexAndTypes.types.head), id)
      }
    }
  }

  case object field {

    def name(name:String) = new {
      @deprecated("use binaryField(name)", "5.2.11")
      def withType(ft: BinaryType.type): BasicFieldDefinition = binaryField(name)

      @deprecated("use booleanField(name)", "5.2.11")
      def withType(ft: BooleanType.type): BasicFieldDefinition = booleanField(name)

      @deprecated("use byteField(name)", "5.2.11")
      def withType(ft: ByteType.type): BasicFieldDefinition = byteField(name)

      @deprecated("use completionField(name)", "5.2.11")
      def withType(ft: CompletionType.type): CompletionFieldDefinition = completionField(name)

      @deprecated("use dateField(name)", "5.2.11")
      def withType(ft: DateType.type): BasicFieldDefinition = dateField(name)

      @deprecated("use doubleField(name)", "5.2.11")
      def withType(ft: DoubleType.type): BasicFieldDefinition = doubleField(name)

      @deprecated("use floatField(name)", "5.2.11")
      def withType(ft: FloatType.type): BasicFieldDefinition = floatField(name)

      @deprecated("use geopointField(name)", "5.2.11")
      def withType(ft: GeoPointType.type): BasicFieldDefinition = geopointField(name)

      @deprecated("use byteField(name)", "5.2.11")
      def withType(ft: GeoShapeType.type): GeoshapeFieldDefinition = geoshapeField(name)

      @deprecated("use byteField(name)", "5.2.11")
      def withType(ft: IntegerType.type): BasicFieldDefinition = intField(name)

      @deprecated("use ipField(name)", "5.2.11")
      def withType(ft: IpType.type): BasicFieldDefinition = ipField(name)

      @deprecated("use byteField(name)", "5.2.11")
      def withType(ft: LongType.type): BasicFieldDefinition = longField(name)

      @deprecated("use byteField(name)", "5.2.11")
      def withType(ft: NestedType.type): NestedFieldDefinition = nestedField(name)

      @deprecated("use byteField(name)", "5.2.11")
      def withType(ft: ObjectType.type): ObjectFieldDefinition = objectField(name)

      @deprecated("use byteField(name)", "5.2.11")
      def withType(ft: ShortType.type): BasicFieldDefinition = shortField(name)

      @deprecated("string type is deprecated in ES 5, use text or keyword types", "5.0.0")
      def withType(ft: StringType.type): BasicFieldDefinition = stringField(name)

      @deprecated("use textField(name)", "5.2.11")
      def withType(ft: TextType.type): TextFieldDefinition = textField(name)

      @deprecated("use tokenCountField(name)", "5.2.11")
      def withType(ft: TokenCountType.type): BasicFieldDefinition = tokenCountField(name)

      @deprecated("use binaryField(name)", "5.2.11")
      def typed(ft: BinaryType.type): BasicFieldDefinition = binaryField(name)

      @deprecated("use boolean(name)", "5.2.11")
      def typed(ft: BooleanType.type): BasicFieldDefinition = booleanField(name)

      @deprecated("use byteField(name)", "5.2.11")
      def typed(ft: ByteType.type): BasicFieldDefinition = byteField(name)

      @deprecated("use completionField(name)", "5.2.11")
      def typed(ft: CompletionType.type): CompletionFieldDefinition = completionField(name)

      @deprecated("use dateField(name)", "5.2.11")
      def typed(ft: DateType.type): BasicFieldDefinition = dateField(name)

      @deprecated("use doubleField(name)", "5.2.11")
      def typed(ft: DoubleType.type): BasicFieldDefinition = doubleField(name)

      @deprecated("use floatField(name)", "5.2.11")
      def typed(ft: FloatType.type): BasicFieldDefinition = floatField(name)

      @deprecated("use geopointField(name)", "5.2.11")
      def typed(ft: GeoPointType.type): BasicFieldDefinition = geopointField(name)

      @deprecated("use geoshapeField(name)", "5.2.11")
      def typed(ft: GeoShapeType.type): GeoshapeFieldDefinition = geoshapeField(name)

      @deprecated("use intField(name)", "5.2.11")
      def typed(ft: IntegerType.type): BasicFieldDefinition = intField(name)

      @deprecated("use ipField(name)", "5.2.11")
      def typed(ft: IpType.type): BasicFieldDefinition = ipField(name)

      @deprecated("use keywordField(name)", "5.2.11")
      def typed(ft: KeywordType.type): KeywordFieldDefinition = keywordField(name)

      @deprecated("use longField(name)", "5.2.11")
      def typed(ft: LongType.type): BasicFieldDefinition = longField(name)

      @deprecated("use nestedField(name)", "5.2.11")
      def typed(ft: NestedType.type): NestedFieldDefinition = nestedField(name)

      @deprecated("use objectField(name)", "5.2.11")
      def typed(ft: ObjectType.type): ObjectFieldDefinition = objectField(name)

      @deprecated("use shortField(name)", "5.2.11")
      def typed(ft: ShortType.type): BasicFieldDefinition = shortField(name)

      @deprecated("string type is deprecated in ES 5, use text or keyword types", "5.0.0")
      def typed(ft: StringType.type): BasicFieldDefinition = stringField(name)

      @deprecated("use textField(name)", "5.2.11")
      def typed(ft: TextType.type): TextFieldDefinition = textField(name)

      @deprecated("use tokenCountField(name)", "5.2.11")
      def typed(ft: TokenCountType.type): BasicFieldDefinition = tokenCountField(name)
    }

    @deprecated("use fieldSort(field)", "5.0.0")
    def sort(field: String): FieldSortDefinition = FieldSortDefinition(field)

    @deprecated("use fieldStats(fields)", "5.0.0")
    def stats(fields: String*): FieldStatsDefinition = FieldStatsDefinition(fields = fields)

    @deprecated("use fieldStats(fields)", "5.0.0")
    def stats(fields: Iterable[String]): FieldStatsDefinition = FieldStatsDefinition(fields = fields.toSeq)
  }

  case object validate {
    @deprecated("use validateIn(index, type) or validateIn(index/type)", "5.0.0")
    def in(indexType: IndexAndTypes): ValidateExpectsQuery = validateIn(indexType.toIndexesAndTypes)

    @deprecated("use validateIn(index, type) or validateIn(index/type)", "5.0.0")
    def in(value: String): ValidateExpectsQuery = validateIn(IndexesAndTypes(value))

    @deprecated("use validateIn(index, type) or validateIn(index/type)", "5.0.0")
    def in(index: String, `type`: String): ValidateExpectsQuery = validateIn(IndexesAndTypes(index, `type`))

    @deprecated("use validateIn(index, type) or validateIn(index/type)", "5.0.0")
    def in(tuple: (String, String)): ValidateExpectsQuery = validateIn(IndexesAndTypes(tuple))
  }
}

object ElasticDsl extends ElasticDsl
