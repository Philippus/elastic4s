package com.sksamuel.elastic4s.requests.script

import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class PutStoredScriptHttpTest extends AnyFunSuite with Matchers with DockerTests {

  test("put stored script should upload new script") {
    val storedScriptId = "putscripttest"
    val storedScript = StoredScriptSource("painless", "_score")

    Try {
      client.execute {
        deleteStoredScript(storedScriptId)
      }.await
    }

    client.execute {
      putStoredScript(storedScriptId, storedScript)
    }.await

    client.execute {
      getStoredScript(storedScriptId)
    }.await.result shouldBe GetStoredScriptResponse(storedScriptId, found=true, storedScript)
  }
}
