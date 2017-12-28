package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.bulk.BulkCompatibleDefinition
import com.sksamuel.elastic4s.{IndexAndType, RefreshPolicy, VersionType}
import com.sksamuel.exts.OptionImplicits._

case class DeleteByIdDefinition(indexType: IndexAndType,
                                id: String,
                                parent: Option[String] = None,
                                routing: Option[String] = None,
                                refresh: Option[RefreshPolicy] = None,
                                waitForActiveShards: Option[Int] = None,
                                version: Option[Long] = None,
                                versionType: Option[VersionType] = None) extends BulkCompatibleDefinition {

  def routing(routing: String): DeleteByIdDefinition = copy(routing = routing.some)
  def parent(parent: String): DeleteByIdDefinition = copy(parent = parent.some)
  def refresh(_refresh: String): DeleteByIdDefinition = refresh(RefreshPolicy.valueOf(_refresh))
  def refresh(refresh: RefreshPolicy): DeleteByIdDefinition = copy(refresh = refresh.some)

  def refreshImmediately: DeleteByIdDefinition = refresh(RefreshPolicy.IMMEDIATE)

  def waitForActiveShards(waitForActiveShards: Int): DeleteByIdDefinition = copy(waitForActiveShards = waitForActiveShards.some)
  def version(version: Long): DeleteByIdDefinition = copy(version = version.some)
  def versionType(versionType: String): DeleteByIdDefinition = copy(versionType = VersionType.valueOf(versionType).some)
  def versionType(versionType: VersionType): DeleteByIdDefinition = copy(versionType = versionType.some)
}
