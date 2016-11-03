package com.sksamuel.elastic4s.admin

import com.sksamuel.elastic4s.mappings.MappingDefinition
import org.elasticsearch.action.admin.indices.alias.Alias
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequestBuilder
import org.elasticsearch.common.settings.Settings
import com.sksamuel.exts.OptionImplicits._

case class CreateIndexTemplateDefinition(name: String,
                                         pattern: String,
                                         settings: Settings = Settings.EMPTY,
                                         mappings: Seq[MappingDefinition] = Nil,
                                         order: Option[Int] = None,
                                         create: Option[Boolean] = None,
                                         aliases: Seq[Alias] = Nil) {

  require(name.nonEmpty, "template name must not be null or empty")
  require(pattern.nonEmpty, "pattern must not be null or empty")

  def populate(builder: PutIndexTemplateRequestBuilder) = {
    builder.setTemplate(pattern)
    builder.setSettings(settings)
    order.foreach(builder.setOrder)
    create.foreach(builder.setCreate)
    mappings.foreach { mapping =>
      builder.addMapping(mapping.`type`, mapping.buildWithName)
    }
    aliases.foreach(builder.addAlias)
  }

  def mappings(first: MappingDefinition, rest: MappingDefinition*): CreateIndexTemplateDefinition = mappings(first +: rest)
  def mappings(mappings: Iterable[MappingDefinition]): CreateIndexTemplateDefinition = copy(mappings = mappings.toSeq)
  def settings(settings: Settings): CreateIndexTemplateDefinition = copy(settings = settings)
  def order(order: Int): CreateIndexTemplateDefinition = copy(order = order.some)
  def create(create: Boolean): CreateIndexTemplateDefinition = copy(create = create.some)
  def alias(alias: Alias): CreateIndexTemplateDefinition = aliases(Seq(alias))
  def aliases(aliases: Seq[Alias]): CreateIndexTemplateDefinition = copy(aliases = aliases)
}
