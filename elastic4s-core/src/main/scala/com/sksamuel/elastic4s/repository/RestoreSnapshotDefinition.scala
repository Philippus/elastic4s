package com.sksamuel.elastic4s.repository

import com.sksamuel.elastic4s.{Index, Indexes}
import com.sksamuel.exts.OptionImplicits._

case class RestoreSnapshotDefinition(name: String,
                                     repo: String,
                                     indices: Indexes = Indexes.All,
                                     ignore_unavailable: Option[Boolean] = None,
                                     includeGlobalState: Option[Boolean] = None,
                                     renamePattern: Option[String] = None,
                                     renameReplacement: Option[String] = None,
                                     partial: Option[Boolean] = None,
                                     includeAliases: Option[Boolean] = None,
                                     waitForCompletion: Option[Boolean] = None
                                    ) {
  require(name.nonEmpty, "snapshot name must not be null or empty")
  require(repo.nonEmpty, "repo must not be null or empty")

  def partial(partial: Boolean) = copy(partial = partial.some)
  def includeAliases(includeAliases: Boolean) = copy(includeAliases = includeAliases.some)
  def includeGlobalState(global: Boolean) = copy(includeGlobalState = global.some)
  def waitForCompletion(wait: Boolean) = copy(waitForCompletion = wait.some)
  def renamePattern(pattern: String) = copy(renamePattern = pattern.some)
  def renameReplacement(str: String) = copy(renameReplacement = str.some)

  def index(index: Index) = copy(indices = index.toIndexes)
  def indices(indices: Indexes) = copy(indices = indices)
}
