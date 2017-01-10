package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.IndexAndType
import com.sksamuel.elastic4s.bulk.BulkCompatibleDefinition
import com.sksamuel.exts.OptionImplicits._

case class DeleteByIdDefinition(indexType: IndexAndType,
                                id: Any,
                                parent: Option[String] = None,
                                routing: Option[String] = None,
                                refresh: Option[String] = None,
                                waitForActiveShards: Option[Int] = None,
                                version: Option[Long] = None,
                                versionType: Option[String] = None) extends BulkCompatibleDefinition {

  def routing(routing: String): DeleteByIdDefinition = copy(routing = routing.some)
  def parent(parent: String): DeleteByIdDefinition = copy(parent = parent.some)
  def refresh(refresh: String): DeleteByIdDefinition = copy(refresh = refresh.some)
  def waitForActiveShards(waitForActiveShards: Int): DeleteByIdDefinition = copy(waitForActiveShards = waitForActiveShards.some)
  def version(version: Long): DeleteByIdDefinition = copy(version = version.some)
  def versionType(versionType: String): DeleteByIdDefinition = copy(versionType = versionType.some)
}
