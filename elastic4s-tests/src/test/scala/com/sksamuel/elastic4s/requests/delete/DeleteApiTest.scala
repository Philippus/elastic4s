package com.sksamuel.elastic4s.requests.delete

import com.sksamuel.elastic4s.requests.common.{RefreshPolicy, VersionType}
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DeleteApiTest extends AnyFlatSpec with Matchers with TypeCheckedTripleEquals {

  import com.sksamuel.elastic4s.ElasticApi._

  "a delete by id request" should "parse slash indextype" in {
    delete("141212") from "index/type"
  }

  it should "accept index and type in dot syntax" in {
    delete("123").from("places")
    delete("123").from("places")
  }

  it should "accept routing key" in {
    delete("141212").from("places").routing("my-route")
  }

  it should "accept version and version type" in {
    delete("141212") from "places" version 53423L versionType VersionType.External
  }

  it should "accept refresh" in {
    delete("141212") from "places" refresh RefreshPolicy.Immediate
  }

  "a delete by query request" should "support the dsl syntax" in {
    //  deleteIn("places").by("query")
  }
}
