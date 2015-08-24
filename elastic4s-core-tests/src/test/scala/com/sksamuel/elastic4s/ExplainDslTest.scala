package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.elasticsearch.search.fetch.source.FetchSourceContext
import org.scalatest.{WordSpec, Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar

/** @author Stephen Samuel */
class ExplainDslTest extends WordSpec with MockitoSugar with ElasticSugar with Matchers {

  "an explain request" should {
    "create correct index/type/id in raw request" in {
      val req = explain("places", "cities", "123") query regexQuery("name", "col.pla.")
      assert(req.request.id() === "123")
      assert(req.request.index() === "places")
      assert(req.request.`type`() === "cities")
    }
    "set routing in raw request" in {
      val req = explain("places", "cities", "123") query regexQuery("name", "col.pla.") routing "upwards"
      req.request.routing() shouldBe "upwards"
    }
    "set preference in raw request" in {
      val req = explain("places", "cities", "123") query regexQuery("name", "col.pla.") preference "qwe"
      req.request.preference() shouldBe "qwe"
    }
    "set fetchSource in raw request" in {
      val req = explain("places", "cities", "123") query regexQuery("name", "col.pla.") fetchSource true
      req.request.fetchSourceContext() shouldBe FetchSourceContext.FETCH_SOURCE
    }
  }
}