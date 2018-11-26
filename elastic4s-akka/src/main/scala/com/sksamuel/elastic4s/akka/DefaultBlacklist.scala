package com.sksamuel.elastic4s.akka

import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConverters._
import scala.concurrent.duration.FiniteDuration

/**
  * Thread-safe host blacklist.
  * Blacklist duration starts with `min` and exponentially increased up to `max` on subsequent calls to `add`.
  * When `remove` is called - blacklist record is permanently removed and next `add` will start with `min` again.
  *
  * @param min      minimum time to keep blacklist record
  * @param max      maximum time to keep blacklist record
  * @param nanoTime clock in nanoseconds
  */
private[akka] class DefaultBlacklist(min: FiniteDuration,
                                     max: FiniteDuration,
                                     nanoTime: => Long = System.nanoTime)
  extends Blacklist {

  import DefaultBlacklist._

  private val hosts = new ConcurrentHashMap[String, BlacklistRecord]()

  override def add(host: String): Boolean = {
    val now = nanoTime
    val record = hosts.getOrDefault(
      host,
      BlacklistRecord(enabled = true, startTime = now, untilTime = -1, -1))

    if(now >= record.untilTime) {
      val retries = record.retries + 1

      val untilTime = now + Math
        .min(min.toNanos * Math.pow(2, retries * 0.5), max.toNanos)
        .toLong

      val updatedRecord =
        record.copy(
          enabled = true,
          untilTime = untilTime,
          retries = retries)

      hosts.put(host, updatedRecord) == null
    } else false
  }

  override def remove(host: String): Boolean = {
    hosts.remove(host) != null
  }

  override def contains(host: String): Boolean = {
    hosts.get(host) match {
      case null => false
      case r =>
        if (r.enabled) {
          if (nanoTime - r.untilTime >= 0) {
            hosts.put(host, r.copy(enabled = false))
            false
          } else true
        } else false
    }
  }

  override def size: Int = hosts.size()

  override def list: List[String] = hosts.keys().asScala.toList
}

object DefaultBlacklist {

  private case class BlacklistRecord(enabled: Boolean,
                                     startTime: Long,
                                     untilTime: Long,
                                     retries: Int)

}
