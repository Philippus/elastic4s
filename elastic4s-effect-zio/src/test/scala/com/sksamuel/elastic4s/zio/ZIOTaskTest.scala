package com.sksamuel.elastic4s.zio

import com.sksamuel.elastic4s.Functor
import com.sksamuel.elastic4s.zio.instances._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.{Task, Unsafe, ZIO}

class ZIOTaskTest extends AnyFlatSpec with Matchers {

  implicit class RichZIO[A](task: Task[A]) {
    def unsafeRun: Either[Throwable, A] = {
      Unsafe.unsafe { implicit u =>
        zio.Runtime.default.unsafe.run(task).toEither
      }
    }
  }

  "ZIO Functor" should "map A to B" in {
    Functor[Task].map(ZIO.succeed(1))(_ + 1).unsafeRun shouldBe Right(2)
  }

}
