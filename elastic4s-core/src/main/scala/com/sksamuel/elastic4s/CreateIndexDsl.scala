package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.anaylzers.{Tokenizer, CharFilterDefinition, CustomAnalyzerDefinition, AnalyzerDefinition, TokenFilterDefinition, TokenFilter}
import com.sksamuel.elastic4s.mappings.MappingDefinition
import org.elasticsearch.action.admin.indices.create.{CreateIndexRequest, CreateIndexResponse}
import org.elasticsearch.client.Client
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.duration.Duration

/** @author Stephen Samuel */
trait CreateIndexDsl {

  def analyzers(analyzers: AnalyzerDefinition*) = new AnalyzersWrapper(analyzers)
  def tokenizers(tokenizers: Tokenizer*) = new TokenizersWrapper(tokenizers)
  def filters(filters: TokenFilter*) = new TokenFiltersWrapper(filters)

  class AnalyzersWrapper(val analyzers: Iterable[AnalyzerDefinition])
  class TokenizersWrapper(val tokenizers: Iterable[Tokenizer])
  class TokenFiltersWrapper(val filters: Iterable[TokenFilter])

  implicit object CreateIndexDefinitionExecutable
    extends Executable[CreateIndexDefinition, CreateIndexResponse, CreateIndexResponse] {
    override def apply(c: Client, t: CreateIndexDefinition): Future[CreateIndexResponse] = {
      injectFuture(c.admin.indices.create(t.build, _))
    }
  }

  implicit object CreateIndexShow extends Show[CreateIndexDefinition] {
    override def show(f: CreateIndexDefinition): String = f._source.string
  }

  implicit class CreateIndexShowOps(f: CreateIndexDefinition) {
    def show = CreateIndexShow.show(f)
  }
}

class IndexSettings {
  private val ShardsKey = "number_of_shards"
  private val ReplicasKey = "number_of_replicas"
  private val RefreshIntervalKey = "refresh_interval"

  val settings: mutable.Map[String, Any] = mutable.Map()

  def shards: Option[Int] = settings.get(ShardsKey).map(_.asInstanceOf[Int])
  def shards_=(s: Int): Unit = settings += ShardsKey -> s

  def replicas: Option[Int] = settings.get(ReplicasKey).map(_.asInstanceOf[Int])
  def replicas_=(r: Int): Unit = settings += ReplicasKey -> r

  def refreshInterval: Option[String] = settings.get(RefreshIntervalKey).map(_.asInstanceOf[String])
  def refreshInterval_=(i: String): Unit = settings += RefreshIntervalKey -> i
}

class CreateIndexDefinition(name: String) {
  require(!name.contains("/"), "Index should not contain / when creating mappings. Specify the type as the mapping")

  val _mappings = new mutable.ListBuffer[MappingDefinition]
  val _settings = new IndexSettings
  var _analysis: Option[AnalysisDefinition] = None

  def build = _rawSource match {
    case Some(s) => new CreateIndexRequest(name).source(s)
    case None => new CreateIndexRequest(name).source(_source)
  }

  def shards(shards: Int): CreateIndexDefinition = {
    _settings.shards = shards
    this
  }

  def replicas(replicas: Int): CreateIndexDefinition = {
    _settings.replicas = replicas
    this
  }

  def refreshInterval(duration: Duration): this.type = refreshInterval(duration.toMillis + "ms")
  def refreshInterval(interval: String): this.type = {
    _settings.refreshInterval = interval
    this
  }

  def indexSetting(name: String, value: Any): CreateIndexDefinition = {
    _settings.settings += name -> value
    this
  }

  def mappings(mappings: MappingDefinition*): CreateIndexDefinition = {
    _mappings ++= mappings
    this
  }

  def analysis(analyzers: Iterable[AnalyzerDefinition]): this.type = {
    _analysis = Some(new AnalysisDefinition(analyzers))
    this
  }

  def analysis(first: AnalyzerDefinition, rest: AnalyzerDefinition*): this.type = analysis(first +: rest)

  var _rawSource: Option[String] = None

  def source(source: String): CreateIndexDefinition = {
    _rawSource = Some(source)
    this
  }

  private[elastic4s] def _source: XContentBuilder = {
    val source = XContentFactory.jsonBuilder().startObject()

    if (_settings.settings.nonEmpty || _analysis.nonEmpty) {

      source.startObject("settings")

      if (_settings.settings.nonEmpty) {
        source.startObject("index")

        _settings.settings foreach {
          case (key, value) =>
            source.field(key, value)
        }

        source.endObject()
      }

      _analysis.foreach(analysis => {
        source.startObject("analysis")

        val charFilterDefinitions = analysis.charFilterDefinitions
        if (charFilterDefinitions.nonEmpty) {
          source.startObject("char_filter")
          charFilterDefinitions.foreach { filter =>
            source.startObject(filter.name)
            source.field("type", filter.filterType)
            filter.build(source)
            source.endObject()
          }
          source.endObject()
        }

        source.startObject("analyzer")
        analysis.analyzers.foreach(analyzer => {
          source.startObject(analyzer.name)
          analyzer.build(source)
          source.endObject()
        })
        source.endObject()

        val tokenizers = analysis.tokenizers
        if (tokenizers.nonEmpty) {
          source.startObject("tokenizer")
          tokenizers.foreach(tokenizer => {
            source.startObject(tokenizer.name)
            tokenizer.build(source)
            source.endObject()
          })
          source.endObject()
        }

        val tokenFilterDefinitions = analysis.tokenFilterDefinitions
        if (tokenFilterDefinitions.nonEmpty) {
          source.startObject("filter")
          tokenFilterDefinitions.foreach(filter => {
            source.startObject(filter.name)
            source.field("type", filter.filterType)
            filter.build(source)
            source.endObject()
          })
          source.endObject()
        }

        source.endObject()
      })

      source.endObject() // end settings
    }

    if (_mappings.nonEmpty) {
      source.startObject("mappings")
      for ( mapping <- _mappings ) {
        source.startObject(mapping.`type`)
        mapping.build(source)
        source.endObject()
      }
      source.endObject()
    }

    source.endObject()
  }
}

case class AnalysisDefinition(analyzers: Iterable[AnalyzerDefinition]) {

  def tokenizers: Iterable[Tokenizer] =
    analyzers.collect {
      case custom: CustomAnalyzerDefinition => custom
    }.map(_.tokenizer).filter(_.customized)

  def tokenFilterDefinitions: Iterable[TokenFilterDefinition] =
    analyzers.collect {
      case custom: CustomAnalyzerDefinition => custom
    }.flatMap(_.filters).collect {
      case token: TokenFilterDefinition => token
    }

  def charFilterDefinitions: Iterable[CharFilterDefinition] =
    analyzers.collect {
      case custom: CustomAnalyzerDefinition => custom
    }.flatMap(_.filters).collect {
      case char: CharFilterDefinition => char
    }
}
