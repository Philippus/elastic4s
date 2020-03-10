package com.sksamuel.elastic4s.testutils

import com.sksamuel.elastic4s.testutils.StringExtensions.StringOps
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToStringShouldWrapper

class StringExtensionsTest extends AnyFlatSpec {

  it should "convert line endings to Windows style" in {
    "one\r\ntwo\nthree\n".withWindowsLineEndings shouldBe "one\r\ntwo\r\nthree\r\n"
  }

  it should "convert line endings to Unix style" in {
    "one\r\ntwo\nthree\r\n".withUnixLineEndings shouldBe "one\ntwo\nthree\n"
  }

}
