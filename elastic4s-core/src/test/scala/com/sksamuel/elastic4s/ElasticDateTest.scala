package com.sksamuel.elastic4s

import java.time.LocalDate

import org.scalatest.Matchers

class ElasticDateTest extends org.scalatest.FlatSpec with Matchers {

  "ElasticDate" should "support 'now' for the date" in {
    ElasticDate.now.show should fullyMatch regex "now"
  }

  it should "support LocalDate date" in {
    ElasticDate(LocalDate.of(2014, 12, 2)).show shouldBe "2014-12-02||"
  }

  it should "support additions" in {
    ElasticDate.now.add(2, Hours).show shouldBe "now+2h"
    ElasticDate(LocalDate.of(2014, 12, 2)).add(2, Years).show shouldBe "2014-12-02||+2y"
  }

  it should "support subtractions" in {
    ElasticDate.now.subtract(3, Minutes).show shouldBe "now-3m"
    ElasticDate(LocalDate.of(2014, 12, 3)).subtract(3, Seconds).show shouldBe "2014-12-03||-3s"
  }

  it should "support multiple adjustments" in {
    ElasticDate.now.subtract(3, Minutes).add(1, Weeks).show shouldBe "now-3m+1w"
  }

  it should "support rounding" in {
    ElasticDate.now.rounding(Days).show shouldBe "now/d"
    ElasticDate(LocalDate.of(2014, 12, 11)).rounding(Days).show shouldBe "2014-12-11||/d"
  }

  it should "support rounding and adjustments" in {
    ElasticDate.now.add(3, Weeks).rounding(Days).show shouldBe "now+3w/d"
    ElasticDate(LocalDate.of(2014, 11, 11)).add(3, Weeks).rounding(Hours).show shouldBe "2014-11-11||+3w/h"
  }
}
