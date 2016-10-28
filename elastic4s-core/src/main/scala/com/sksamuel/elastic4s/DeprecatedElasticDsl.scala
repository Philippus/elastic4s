package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.admin._
import com.sksamuel.elastic4s.alias.GetAliasDefinition
import com.sksamuel.elastic4s.analyzers._
import com.sksamuel.elastic4s.mappings.{FieldDefinition, GetMappingDefinition, MappingDefinition, PutMappingDefinition, TimestampDefinition, TypeableFields}
import com.sksamuel.elastic4s.search.query.{FuzzyQueryDefinition, IdQueryDefinition, IndicesQueryDefinition}
import com.sksamuel.elastic4s.search.{QueryDefinition, SearchDefinition}
import com.sksamuel.elastic4s.sort.{FieldSortDefinition, GeoDistanceSortDefinition, ScoreSortDefinition}

// a dumping ground for deprecated syntax, keeps the main file clear
trait DeprecatedElasticDsl {
  self: ElasticDsl =>

  case object add {
    @deprecated("Use full method syntax, eg addAlias()", "3.0.0")
    def alias(alias: String) = addAlias(alias)
  }

  case object update {
    @deprecated("use update(id)", "3.0.0")
    def id(id: Any) = update(id)

    @deprecated("use updateSettings(index)", "3.0.0")
    def settings(index: String): UpdateSettingsDefinition = updateSettings(index)
  }

  case object types {
    @deprecated("use typesExist(types)", "3.0.0")
    def exist(types: String*) = typesExist(types)
  }

  case object restore {
    @deprecated("use restoreSnapshot(name)", "3.0.0")
    def snapshot(name: String) = restoreSnapshot(name)
  }

  case object search {
    @deprecated("use search(index) or search(indexes/types)", "3.0.0")
    def in(indexesTypes: IndexesAndTypes): SearchDefinition = SearchDefinition(indexesTypes)
    @deprecated("use searchScroll(id)", "3.0.0")
    def scroll(id: String): SearchScrollDefinition = SearchScrollDefinition(id)
  }

  case object term {
    @deprecated("use termSuggestion(name)", "3.0.0")
    def suggestion(name: String) = ??? // todo TermSuggestionDefinition = TermSuggestionDefinition(name)
  }

  case object score {
    @deprecated("use scoreSort()", "3.0.0")
    def sort: ScoreSortDefinition = ScoreSortDefinition()
  }

  @deprecated("use idsQuery", "2.0.0")
  def ids(ids: Iterable[String]): IdQueryDefinition = IdQueryDefinition(ids.toSeq)
  @deprecated("use idsQuery", "2.0.0")
  def ids(ids: String*): IdQueryDefinition = IdQueryDefinition(ids.toSeq)

  @deprecated("use putMapping(index)", "3.0.0")
  case object put {
    @deprecated("use putMapping(index)", "3.0.0")
    def mapping(indexesAndType: IndexesAndType): PutMappingDefinition = new PutMappingDefinition(indexesAndType)
  }

  @deprecated("use phraseSuggestion(name)", "3.0.0")
  case object phrase {
    def suggestion(name: String) = ??? // todo: PhraseSuggestionDefinition = PhraseSuggestionDefinition(name)
  }

  case object remove {
    @deprecated("Use dot syntax, eg removeAlias(alias", "3.0.0")
    def alias(alias: String) = removeAlias(alias)
  }

  @deprecated("use recoverIndex(index)", "3.0.0")
  case object recover {
    @deprecated("use putMapping(index)", "3.0.0")
    def index(indexes: Iterable[String]): IndexRecoveryDefinition = new IndexRecoveryDefinition(indexes.toSeq)
    @deprecated("use putMapping(index)", "3.0.0")
    def index(indexes: String*): IndexRecoveryDefinition = new IndexRecoveryDefinition(indexes)
  }

  @deprecated("use refreshIndex(index)", "3.0.0")
  case object refresh {
    @deprecated("use refreshIndex(index)", "3.0.0")
    def index(indexes: Iterable[String]): RefreshIndexDefinition = RefreshIndexDefinition(indexes.toSeq)
    @deprecated("use refreshIndex(index)", "3.0.0")
    def index(indexes: String*): RefreshIndexDefinition = RefreshIndexDefinition(indexes)
  }

  case object mapping {
    @deprecated("use mapping(name)", "3.0.0")
    def name(name: String): MappingDefinition = {
      require(name.nonEmpty, "mapping name must not be null or empty")
      new MappingDefinition(name)
    }
  }

  @deprecated("use openIndex(index)", "3.0.0")
  case object open {
    def index(index: String): OpenIndexDefinition = OpenIndexDefinition(index)
  }

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

  @deprecated("use commonQuery(field", "3.0.0")
  def commonQuery = new CommonQueryExpectsField

  class CommonQueryExpectsField {
    def field(name: String) = new CommonQueryExpectsText(name)
  }

  @deprecated("Fuzzy queries are not useful enough and will be removed in a future version", "3.0.0")
  def fuzzyQuery(name: String, value: Any) = FuzzyQueryDefinition(name, value)

  @deprecated("instead search on the `_index` field")
  def indicesQuery(indices: String*) = new {
    @deprecated("instead search on the `_index` field")
    def query(query: QueryDefinition): IndicesQueryDefinition = IndicesQueryDefinition(indices, query)
  }

  @deprecated("prefer the method commonGramsTokenFilter(\"name\")", "2.0.0")
  case object commonGrams {
    @deprecated("prefer the method commonGramsTokenFilter(\"name\")", "2.0.0")
    def tokenfilter(name: String): CommonGramsTokenFilter = CommonGramsTokenFilter(name)
  }

  @deprecated("prefer the method edgeNGramTokenFilter(\"name\")", "2.0.0")
  case object edgeNGram {
    @deprecated("prefer the method edgeNGramTokenFilter(\"name\")", "2.0.0")
    def tokenfilter(name: String): EdgeNGramTokenFilter = EdgeNGramTokenFilter(name)
  }
  @deprecated("prefer the method edgeNGramTokenFilter(\"name\") <-- note capitalization", "2.0.0")
  def edgeNGramTokenfilter(name: String): EdgeNGramTokenFilter = EdgeNGramTokenFilter(name)

  @deprecated("prefer the method ngramTokenFilter(\"name\")", "2.0.0")
  case object ngram {
    @deprecated("prefer the method ngramTokenFilter(\"name\")", "2.0.0")
    def tokenfilter(name: String): NGramTokenFilter = NGramTokenFilter(name)
  }

  case object create {

    @deprecated("use createIndex(name)", "3.0.0")
    def index(name: String) = CreateIndexDefinition(name)

    @deprecated("use createSnapshot(name)", "3.0.0")
    def snapshot(name: String) = createSnapshot(name)

    @deprecated("use createRepository(name)", "3.0.0")
    def repository(name: String) = createRepository(name)

    @deprecated("use createTemplate(name)", "3.0.0")
    def template(name: String) = createTemplate(name)
  }

  case object delete {
    @deprecated("use delete(id)", "3.0.0")
    def id(id: Any): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)

    @deprecated("use deleteIndex(indexes)", "3.0.0")
    def index(indexes: String*): DeleteIndexDefinition = index(indexes)

    @deprecated("use deleteIndex(indexes)", "3.0.0")
    def index(indexes: Iterable[String]): DeleteIndexDefinition = DeleteIndexDefinition(indexes.toSeq)

    @deprecated("use deleteSnapshot(name)", "3.0.0")
    def snapshot(name: String) = deleteSnapshot(name)

    @deprecated("use deleteTemplate(name)", "3.0.0")
    def template(name: String) = DeleteIndexTemplateDefinition(name)
  }

  case object cluster {
    @deprecated("use clusterPersistentSettings(settings)", "3.0.0")
    def persistentSettings(settings: Map[String, String]) = ClusterSettingsDefinition(settings, Map.empty)
    @deprecated("use clusterTransientSettings(settings)", "3.0.0")
    def transientSettings(settings: Map[String, String]) = ClusterSettingsDefinition(Map.empty, settings)
  }

  case object fuzzyCompletion {
    @deprecated("use fuzzyCompletionSuggestion(name)", "3.0.0")
    def suggestion(name: String) = ??? // todo = FuzzyCompletionSuggestionDefinition(name)
  }

  case object script {
    @deprecated("use scriptSort(script).typed(ScriptSortType)", "3.0.0")
    def sort(script: ScriptDefinition) = scriptSort(script)
    @deprecated("use scriptField(name)", "3.0.0")
    def field(n: String): ExpectsScript = ExpectsScript(field = n)
  }

  trait HealthKeyword
  case object health extends HealthKeyword

  trait StatsKeyword
  case object stats extends StatsKeyword

  case object highlight {
    @deprecated("use highlight(field)", "3.0.0")
    def field(field: String): HighlightDefinition = HighlightDefinition(field)
  }

  case object index {

    @deprecated("use indexExists(indexes)", "3.0.0")
    def exists(indexes: Iterable[String]): IndexExistsDefinition = IndexExistsDefinition(indexes.toSeq)

    @deprecated("use indexExists(indexes)", "3.0.0")
    def exists(indexes: String*): IndexExistsDefinition = IndexExistsDefinition(indexes)

    @deprecated("use indexInto(index / type)", "3.0.0")
    def into(indexType: IndexAndTypes): IndexDefinition = new IndexDefinition(indexType.index, indexType.types.head)

    @deprecated("use indexStats(indexes)", "3.0.0")
    def stats(indexes: Indexes): IndicesStatsDefinition = indexStats(indexes)

    @deprecated("use indexStats(indexes)", "3.0.0")
    def stats(first: String, rest: String*): IndicesStatsDefinition = indexStats(first +: rest)
  }

  case object flush {
    @deprecated("use flushIndex(indexes)", "3.0.0")
    def index(indexes: Iterable[String]): FlushIndexDefinition = FlushIndexDefinition(indexes.toSeq)
    @deprecated("use flushIndex(indexes)", "3.0.0")
    def index(indexes: String*): FlushIndexDefinition = FlushIndexDefinition(indexes)
  }

  case object get {

    @deprecated("use get(id)", "3.0.0")
    def id(id: Any) = get(id)

    @deprecated("use getAlias(alias)", "3.0.0")
    def alias(aliases: String*): GetAliasDefinition = GetAliasDefinition(aliases)

    @deprecated("use clusterStats()", "3.0.0")
    def cluster(stats: StatsKeyword): ClusterStatsDefinition = new ClusterStatsDefinition

    @deprecated("use clusterHealth()", "3.0.0")
    def cluster(health: HealthKeyword): ClusterHealthDefinition = new ClusterHealthDefinition

    @deprecated("use getMapping(indexes)", "3.0.0")
    def mapping(it: IndexesAndTypes): GetMappingDefinition = GetMappingDefinition(it)

    @deprecated("use getSegments(indexes)", "3.0.0")
    def segments(indexes: Indexes): GetSegmentsDefinition = getSegments(indexes)

    @deprecated("use getSegments(indexes)", "3.0.0")
    def segments(first: String, rest: String*): GetSegmentsDefinition = getSegments(first +: rest)

    @deprecated("use getSettings(indexes)", "3.0.0")
    def settings(indexes: Indexes): GetSettingsDefinition = GetSettingsDefinition(indexes)

    @deprecated("use getTemplate(name)", "3.0.0")
    def template(name: String): GetTemplateDefinition = GetTemplateDefinition(name)

    @deprecated("use getSnapshot(names)", "3.0.0")
    def snapshot(names: Iterable[String]) = getSnapshot(names.toSeq)

    @deprecated("use getSnapshot(names)", "3.0.0")
    def snapshot(names: String*) = getSnapshot(names)
  }

  case object close {
    @deprecated("use closeIndex(index)", "3.0.0")
    def index(index: String): CloseIndexDefinition = CloseIndexDefinition(index)
  }

  case object timestamp {
    @deprecated("use timestamp(boolean)", "3.0.0")
    def enabled(en: Boolean): TimestampDefinition = TimestampDefinition(en)
  }

  case object clear {
    @deprecated("use clearCache(indexes)", "3.0.0")
    def cache(indexes: Iterable[String]): ClearCacheDefinition = ClearCacheDefinition(indexes.toSeq)
    @deprecated("use clearCache(indexes)", "3.0.0")
    def cache(first: String, rest: String*): ClearCacheDefinition = clearCache(first +: rest)
    @deprecated("use clearScroll(ids)", "3.0.0")
    def scroll(id: String, ids: String*): ClearScrollDefinition = clearScroll(id +: ids)
    @deprecated("use clearScroll(ids)", "3.0.0")
    def scroll(ids: Iterable[String]): ClearScrollDefinition = clearScroll(ids)
  }

  case object completion {
    @deprecated("use completionSuggestion(name)", "3.0.0")
    def suggestion(name: String) = ??? // todo CompletionSuggestionDefinition(name)
  }

  case object explain {
    @deprecated("Use explain(index, type, id", "3.0.0")
    def id(id: String) = new {
      def in(indexAndTypes: IndexAndTypes): ExplainDefinition = {
        ExplainDefinition(indexAndTypes.index, indexAndTypes.types.head, id)
      }
    }
  }

  @deprecated("use optimizeIndex(index)", "1.6.2")
  def optimize(indexes: String*): ForceMergeDefinition = ForceMergeDefinition(indexes.toSeq)

  @deprecated("prefer the method shingleTokenFilter(\"name\")", "2.0.0")
  case object shingle {
    @deprecated("prefer the method shingleTokenFilter(\"name\")", "2.0.0")
    def tokenfilter(name: String): ShingleTokenFilter = ShingleTokenFilter(name)
  }

  @deprecated("prefer the method snowballTokenFilter(\"name\")", "2.0.0")
  case object snowball {
    @deprecated("prefer the method snowballTokenFilter(\"name\")", "2.0.0")
    def tokenfilter(name: String): SnowballTokenFilter = SnowballTokenFilter(name)
  }

  case object field extends TypeableFields {
    val name = ""

    @deprecated("use field(name)", "3.0.0")
    def name(name: String): FieldDefinition = FieldDefinition(name)

    @deprecated("use fieldSort(field)", "3.0.0")
    def sort(field: String): FieldSortDefinition = FieldSortDefinition(field)

    @deprecated("use fieldStats(fields)", "3.0.0")
    def stats(fields: String*): FieldStatsDefinition = FieldStatsDefinition(fields = fields)

    @deprecated("use fieldStats(fields)", "3.0.0")
    def stats(fields: Iterable[String]): FieldStatsDefinition = FieldStatsDefinition(fields = fields.toSeq)
  }

  @deprecated("use score sort, geo sort, field sort or script sort", "1.6.1")
  case object sortby {
    def score: ScoreSortDefinition = new ScoreSortDefinition
    def field(field: String): FieldSortDefinition = FieldSortDefinition(field)
    def script(script: ScriptDefinition) = ElasticDsl.script.sort(script)
  }

  @deprecated("prefer the method stemmerTokenFilter(\"name\")", "2.0.0")
  case object stemmer {
    @deprecated("prefer the method stemmerTokenFilter(\"name\")", "2.0.0")
    def tokenfilter(name: String): StemmerTokenFilter = StemmerTokenFilter(name)
  }

  case object validate {
    @deprecated("use validateIn(index, type) or validateIn(index/type)", "3.0.0")
    def in(indexType: IndexAndTypes): ValidateDefinition = ValidateDefinition(indexType.index, indexType.types.head)
    @deprecated("use validateIn(index, type) or validateIn(index/type)", "3.0.0")
    def in(value: String): ValidateDefinition = {
      require(value.nonEmpty, "value must not be null or empty")
      in(IndexAndTypes(value))
    }
    @deprecated("use validateIn(index, type) or validateIn(index/type)", "3.0.0")
    def in(index: String, `type`: String): ValidateDefinition = ValidateDefinition(index, `type`)
    @deprecated("use validateIn(index, type) or validateIn(index/type)", "3.0.0")
    def in(tuple: (String, String)): ValidateDefinition = ValidateDefinition(tuple._1, tuple._2)
  }
}
