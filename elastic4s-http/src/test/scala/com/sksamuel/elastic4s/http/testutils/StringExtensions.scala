package com.sksamuel.elastic4s.http.testutils

object StringExtensions {
  private val LineEndingRegex = s"""(\r\n|\n)"""

  private val WindowsLE = "\r\n"
  private val UnixLE = "\n"

  implicit class StringOps(val target: String) extends AnyVal {
    def withWindowsLineEndings: String = target.replaceAll(LineEndingRegex, WindowsLE)

    def withUnixLineEndings: String = target.replaceAll(LineEndingRegex, UnixLE)

    def withoutSpaces: String = target.replaceAll("\\s+", "")
  }

}
