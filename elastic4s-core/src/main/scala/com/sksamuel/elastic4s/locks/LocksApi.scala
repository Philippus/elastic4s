package com.sksamuel.elastic4s.locks

trait LocksApi {
  def acquireGlobalLock() = AcquireGlobalLockDefinition()
  def releaseGlobalLock() = ReleaseGlobalLockDefinition()
}

case class AcquireGlobalLockDefinition()
case class ReleaseGlobalLockDefinition()
