package com.sksamuel.elastic4s.snapshots

import com.sksamuel.exts.OptionImplicits._

case class CreateRepository(name: String,
                            `type`: String,
                            verify: Option[Boolean] = None,
                            settings: Map[String, AnyRef] = Map.empty) {
  require(name.nonEmpty, "repository name must not be null or empty")
  require(`type`.nonEmpty, "repository type must not be null or empty")

  def settings(settings: Map[String, AnyRef]): CreateRepository = copy(settings = settings)
  def verify(v: Boolean): CreateRepository = copy(verify = v.some)
}
