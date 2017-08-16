package com.sksamuel.elastic4s.testkit

import java.nio.file.{Path, Paths}
import java.util.UUID

import com.sksamuel.elastic4s.embedded.LocalNode

object ClassloaderLocalNode {

  private lazy val tempDirectoryPath: Path = Paths get System.getProperty("java.io.tmpdir")
  private lazy val pathHome: Path = tempDirectoryPath resolve UUID.randomUUID().toString

  lazy val node: LocalNode = try {
    LocalNode("classloader-node", pathHome.toAbsolutePath.toString)
  } catch {
    case t: Throwable =>
      t.printStackTrace()
      println(getClass.getClassLoader)
      throw t
  }
}
