package com.sksamuel.elastic4s2.index

import com.sksamuel.elastic4s2.analyzers._
import com.sksamuel.elastic4s2.{Executable, Show}
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.client.Client

import scala.concurrent.Future

trait CreateIndexDsl {

  def createIndex(name: String) = CreateIndexDefinition(name)

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
