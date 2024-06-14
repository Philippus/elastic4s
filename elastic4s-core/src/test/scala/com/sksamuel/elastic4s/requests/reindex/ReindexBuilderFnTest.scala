package com.sksamuel.elastic4s.requests.reindex

import com.sksamuel.elastic4s.handlers.reindex.ReindexBuilderFn
import com.sksamuel.elastic4s.requests.common.VersionType.ExternalGte
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ReindexBuilderFnTest extends AnyFunSuite with Matchers {
  import com.sksamuel.elastic4s.ElasticApi._

  test("reindex content builder should support version type") {
    val req = reindex("source", "target").versionType(ExternalGte)

    ReindexBuilderFn(req).string shouldBe
      """{"source":{"index":["source"]},"dest":{"index":"target","version_type":"external_gte"}}""".stripMargin
  }
}
