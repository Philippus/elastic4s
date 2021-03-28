package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.locks.{AcquireGlobalLock, ReleaseGlobalLock}

trait LocksApi {
  def acquireGlobalLock() = AcquireGlobalLock()
  def releaseGlobalLock() = ReleaseGlobalLock()
}
