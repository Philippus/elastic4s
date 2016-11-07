package com.sksamuel.elastic4s.admin

import org.elasticsearch.action.admin.indices.shrink.ShrinkRequestBuilder
import org.elasticsearch.common.settings.Settings

import scala.collection.JavaConverters._

case class ShrinkDefinition(source: String,
                            target: String,
                            waitForActiveShards: Option[Int] = None,
                            settings: Settings = Settings.EMPTY) {

  def populate(builder: ShrinkRequestBuilder): Unit = {
    builder.setSettings(settings)
    waitForActiveShards.foreach(builder.setWaitForActiveShards)
  }

  def settings(map: Map[String, String]): ShrinkDefinition = copy(settings = Settings.builder().put(map.asJava).build)
  def settings(settings: Settings): ShrinkDefinition = copy(settings = settings)
}
