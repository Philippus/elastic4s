package com.sksamuel.elastic4s.akka

import java.util.concurrent.ConcurrentHashMap

import scala.jdk.CollectionConverters._
import scala.concurrent.duration.FiniteDuration

/** Thread-safe host blacklist. Blacklist duration starts with `min` and exponentially increased up to `max` on
  * subsequent calls to `add`. When `remove` is called - blacklist record is permanently removed and next `add` will
  * start with `min` again.
  *
  * @param min
  *   minimum time to keep blacklist record
  * @param max
  *   maximum time to keep blacklist record
  * @param nanoTime
  *   clock in nanoseconds
  */
private[akka] class DefaultBlacklist(min: FiniteDuration, max: FiniteDuration, nanoTime: => Long = System.nanoTime)
    extends Blacklist {

  import DefaultBlacklist._

  private val hosts = new ConcurrentHashMap[String, BlacklistRecord]()

  override def add(host: String): Boolean = {
    val now    = nanoTime
    val record = hosts.getOrDefault(
      host,
      BlacklistRecord(enabled = true, startTime = now, untilTime = -1, -1)
    )

    if (now >= record.untilTime) {
      val retries = record.retries + 1

      val untilTime = now +
        Math
          .min(min.toNanos * Math.pow(2, retries * 0.5), max.toNanos.toDouble)
          .toLong

      val updatedRecord =
        record.copy(
          enabled = true,
          untilTime = untilTime,
          retries = retries
        )

      hosts.put(host, updatedRecord) == null
    } else false
  }

  override def remove(host: String): Boolean = {
    hosts.remove(host) != null
  }

  private def isActive(host: String, record: BlacklistRecord, now: Long): Boolean = {
    if (!record.enabled)
      false
    else if (now - record.untilTime >= 0) {
      hosts.put(host, record.copy(enabled = false))
      false
    } else
      true
  }

  override def contains(host: String): Boolean = {
    val now = nanoTime
    hosts.get(host) match {
      case null => false
      case r    => isActive(host, r, now)
    }
  }

  override def size: Int = {
    val now = nanoTime
    hosts.entrySet().asScala.count(entry => isActive(entry.getKey, entry.getValue, now))
  }

  override def list: List[String] = {
    val now = nanoTime
    hosts.entrySet().asScala.toList.collect {
      case entry if isActive(entry.getKey, entry.getValue, now) =>
        entry.getKey
    }
  }
}

object DefaultBlacklist {

  private case class BlacklistRecord(enabled: Boolean, startTime: Long, untilTime: Long, retries: Int)

}
