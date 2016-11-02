package com.sksamuel.elastic4s2

import org.scalatest.{Matchers, WordSpec}

class IndexAndTypesTest extends WordSpec with Matchers {

  "IndexAndTypes" should {
    "parse /" in {
      IndexAndTypes("indx/t1") shouldBe IndexAndTypes("indx", Array("t1"))
    }
    "parse / and ," in {
      IndexAndTypes("indx/t1,t2") shouldBe IndexAndTypes("indx", Array("t1", "t2"))
    }
  }
}
