package com.sksamuel.elastic4s.akka

import scala.concurrent.duration._
import scala.language.postfixOps

import org.scalatest.{Matchers, WordSpec}

class DefaultBlacklistTest extends WordSpec with Matchers {

  val minDuration = 1 second
  val maxDuration = 10 seconds
  val host = "elastic.test"

  "DefaultBlacklist" should {

    "add host to blacklist" in {
      val blacklist = new DefaultBlacklist(minDuration, maxDuration)
      blacklist.add(host) shouldBe true
      blacklist.contains(host) shouldBe true
    }

    "remove host from blacklist" in {
      val blacklist = new DefaultBlacklist(minDuration, maxDuration)
      blacklist.add(host)
      blacklist.remove(host) shouldBe true
      blacklist.contains(host) shouldBe false
    }

    "ensure host is in blacklist" in {
      val blacklist = new DefaultBlacklist(minDuration, maxDuration)
      blacklist.add(host) shouldBe true
      blacklist.add(host) shouldBe false
    }

    "ensure host is not in blacklist" in {
      val blacklist = new DefaultBlacklist(minDuration, maxDuration)
      blacklist.remove(host) shouldBe false
    }

    "remove host from blacklist on timeout" in {
      var now: Long = 0
      val blacklist = new DefaultBlacklist(minDuration, maxDuration, now)

      blacklist.add(host)
      blacklist.contains(host) shouldBe true

      now += minDuration.toNanos
      blacklist.contains(host) shouldBe false
    }

    "increase blacklist timeout up to max" in {
      var now: Long = 0
      val blacklist = new DefaultBlacklist(minDuration, maxDuration, now)

      blacklist.add(host)

      now += minDuration.toNanos
      blacklist.contains(host) shouldBe false

      // after first blacklist timed out add it again
      blacklist.add(host)

      // check that the same time increase now doesn't result in invalidated blacklist record
      now += minDuration.toNanos
      blacklist.contains(host) shouldBe true

      // now when more time elapses it should invalidate it again
      now += maxDuration.toNanos
      blacklist.contains(host) shouldBe false
    }

    "not increase blacklist timeout on early `add`" in {
      var now: Long = 0
      val blacklist = new DefaultBlacklist(minDuration, maxDuration, now)

      blacklist.add(host)
      now = minDuration.toNanos / 2
      blacklist.add(host)
      now = minDuration.toNanos
      blacklist.contains(host) shouldBe false
    }
  }
}
