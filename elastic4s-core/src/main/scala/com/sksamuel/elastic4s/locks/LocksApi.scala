package com.sksamuel.elastic4s.locks

trait LocksApi {
  def acquireGlobalLock() = AcquireGlobalLock()
  def releaseGlobalLock() = ReleaseGlobalLock()
}

case class AcquireGlobalLock()
case class ReleaseGlobalLock()
