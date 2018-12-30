package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.{ElasticDsl, RequestFailure, RequestSuccess, Response}
import org.scalatest.{FlatSpec, Matchers}

class ElasticClientResponsesTest extends FlatSpec with Matchers with ElasticDsl {

  "HttpClient" should "provide flatMap and for-comprehension on responses" in {
    // Functor
    val response0: Response[Int] = RequestSuccess(0, None, Map.empty, 42)

    val response1: Response[Int] = for {
      i <- response0
    } yield { i + 10 }

    assert(response1 == RequestSuccess(0, None, Map.empty, 52))

    // Monad
    val response2 = for {
      i <- response0
      j <- response1
    } yield { i + j }


    assert(response2 == RequestSuccess(0, None, Map.empty, 94))


    // Failure
    val responseFail: Response[Int] = RequestFailure(0, None, Map.empty, null)

    val response3 = for {
      i <- responseFail
    } yield { i + 42 }

    assert(response3 == responseFail)
  }
}

