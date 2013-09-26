package com.sksamuel.elastic4s

import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}
import scala.collection.mutable.ListBuffer
import org.elasticsearch.action.admin.indices.create.{CreateIndexAction, CreateIndexRequest}

/** @author Stephen Samuel */
trait CreateIndexDsl {

  def create = new CreateIndexExpectsName
  class CreateIndexExpectsName {
    def index(name: String) = new CreateIndexDefinition(name)
  }

  class IndexSettings(var shards: Int = 5, var replicas: Int = 1)

  class CreateIndexDefinition(name: String) extends IndicesRequestDefinition(CreateIndexAction.INSTANCE) {

    val _mappings = new ListBuffer[MappingDefinition]
    val _settings = new IndexSettings

    def build = new CreateIndexRequest(name).source(_source)

    def shards(shards: Int) = {
      _settings.shards = shards
      this
    }

    def replicas(replicas: Int) = {
      _settings.replicas = replicas
      this
    }

    def mappings(mappings: MappingDefinition*) = {
      _mappings ++= mappings
      this
    }

    def _source: XContentBuilder = {
      val source = XContentFactory.jsonBuilder().startObject()

      source.startObject("settings")
      source.field("number_of_shards", _settings.shards)
      source.field("number_of_replicas", _settings.replicas)
      source.endObject()

      if (_mappings.size > 0) source.startObject("mappings")
      for ( mapping <- _mappings ) {
        mapping.build(source)
      }
      if (_mappings.size > 0) source.endObject()

      source.endObject()
    }
  }
}
