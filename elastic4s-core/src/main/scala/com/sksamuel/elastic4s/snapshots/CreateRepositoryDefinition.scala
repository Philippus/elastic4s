package com.sksamuel.elastic4s.snapshots

import com.sksamuel.exts.OptionImplicits._

case class CreateRepositoryDefinition(name: String,
                                      `type`: String,
                                      verify: Option[Boolean] = None,
                                      settings: Map[String, AnyRef] = Map.empty) {
  require(name.nonEmpty, "repository name must not be null or empty")
  require(`type`.nonEmpty, "repository name must not be null or empty")

  def settings(settings: Map[String, AnyRef]): CreateRepositoryDefinition = copy(settings = settings)
  def verify(v: Boolean): CreateRepositoryDefinition = copy(verify = v.some)
}
