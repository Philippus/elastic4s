package com.sksamuel.elastic4s.cats.effect

import cats.effect.IO
import com.sksamuel.elastic4s.Functor
import com.sksamuel.elastic4s.cats.effect.instances._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CatsEffectTest extends AnyFlatSpec with Matchers {

  "Functor" should "map A to B" in {
    Functor[IO].map(IO(1))(_ + 1).unsafeRunSync() shouldBe 2
  }
}
