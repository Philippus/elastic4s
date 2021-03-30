package com.sksamuel.elastic4s.requests.delete

import com.sksamuel.elastic4s.Index
import com.sksamuel.elastic4s.requests.bulk.BulkCompatibleRequest
import com.sksamuel.elastic4s.requests.common.{RefreshPolicy, VersionType}
import com.sksamuel.exts.OptionImplicits.RichOptionImplicits

case class DeleteByIdRequest(index: Index,
                             id: String,
                             parent: Option[String] = None,
                             routing: Option[String] = None,
                             refresh: Option[RefreshPolicy] = None,
                             waitForActiveShards: Option[Int] = None,
                             version: Option[Long] = None,
                             ifSeqNo: Option[Long] = None,
                             ifPrimaryTerm: Option[Long] = None,
                             versionType: Option[VersionType] = None)
  extends BulkCompatibleRequest {

  def routing(routing: String): DeleteByIdRequest = copy(routing = routing.some)
  def parent(parent: String): DeleteByIdRequest = copy(parent = parent.some)
  def refresh(_refresh: String): DeleteByIdRequest = refresh(RefreshPolicy.valueOf(_refresh))
  def refresh(refresh: RefreshPolicy): DeleteByIdRequest = copy(refresh = refresh.some)

  def refreshImmediately: DeleteByIdRequest = refresh(RefreshPolicy.IMMEDIATE)

  def waitForActiveShards(waitForActiveShards: Int): DeleteByIdRequest =
    copy(waitForActiveShards = waitForActiveShards.some)
  def version(version: Long): DeleteByIdRequest = copy(version = version.some)
  def ifSeqNo(ifSeqNo: Long): DeleteByIdRequest = copy(ifSeqNo = ifSeqNo.some)
  def ifPrimaryTerm(ifPrimaryTerm: Long): DeleteByIdRequest = copy(ifPrimaryTerm = ifPrimaryTerm.some)
  def versionType(versionType: String): DeleteByIdRequest = copy(versionType = VersionType.valueOf(versionType).some)
  def versionType(versionType: VersionType): DeleteByIdRequest = copy(versionType = versionType.some)
}
