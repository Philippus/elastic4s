package com.sksamuel.elastic4s

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class IndexAndTypesTest extends AnyWordSpec with Matchers {

  "IndexAndTypes" should {
    "parse /" in {
      IndexAndTypes("indx/t1") shouldBe IndexAndTypes("indx", Array("t1"))
    }
    "parse / and ," in {
      IndexAndTypes("indx/t1,t2") shouldBe IndexAndTypes("indx", Array("t1", "t2"))
    }
  }
}
