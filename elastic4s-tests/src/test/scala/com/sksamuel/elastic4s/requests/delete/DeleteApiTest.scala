package com.sksamuel.elastic4s.requests.delete

import com.sksamuel.elastic4s.requests.common.{RefreshPolicy, VersionType}
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DeleteApiTest extends AnyFlatSpec with Matchers with TypeCheckedTripleEquals {
  import com.sksamuel.elastic4s.ElasticApi._

  "a delete by id request" should "parse" in {
    deleteById("index", "141212")
  }

  it should "accept routing key" in {
    deleteById("places", "141212").routing("my-route")
  }

  it should "accept version and version type" in {
    deleteById("places", "141212") version 53423L versionType VersionType.External
  }

  it should "accept refresh" in {
    deleteById("places", "141212") refresh RefreshPolicy.Immediate
  }
}
