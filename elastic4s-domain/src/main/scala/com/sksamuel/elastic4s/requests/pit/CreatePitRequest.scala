package com.sksamuel.elastic4s.requests.pit

import com.sksamuel.elastic4s.Index

import scala.concurrent.duration.FiniteDuration

case class CreatePitRequest(index: Index, keepAlive: Option[FiniteDuration] = None) {
  def keepAlive(keepAlive: FiniteDuration): CreatePitRequest = copy(keepAlive = Some(keepAlive))
}
