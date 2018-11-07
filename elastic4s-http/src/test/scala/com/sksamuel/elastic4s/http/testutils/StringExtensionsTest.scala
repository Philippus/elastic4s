package com.sksamuel.elastic4s.http.testutils

import com.sksamuel.elastic4s.http.testutils.StringExtensions.StringOps
import org.scalatest.FlatSpec
import org.scalatest.Matchers.convertToStringShouldWrapper

class StringExtensionsTest extends FlatSpec {

  it should "convert line endings to Windows style" in {
    "one\r\ntwo\nthree\n".withWindowsLineEndings shouldBe "one\r\ntwo\r\nthree\r\n"
  }

  it should "convert line endings to Unix style" in {
    "one\r\ntwo\nthree\r\n".withUnixLineEndings shouldBe "one\ntwo\nthree\n"
  }

}
