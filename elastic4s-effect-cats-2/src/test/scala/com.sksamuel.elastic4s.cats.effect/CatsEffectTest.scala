package com.sksamuel.elastic4s.cats.effect

import cats.data.OptionT
import cats.effect.IO
import com.sksamuel.elastic4s.{ElasticClient, ElasticProperties}
import com.sksamuel.elastic4s.ElasticDsl._
import org.scalatest.flatspec.AnyFlatSpec
import com.sksamuel.elastic4s.cats.effect.instances._
import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.requests.indexes.admin.DeleteIndexResponse
import com.sksamuel.elastic4s.requests.indexes.{CreateIndexRequest, CreateIndexResponse, DeleteIndexRequest}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CatsEffectTest extends AnyFlatSpec {
  type OptionIO[A] = OptionT[IO, A]

  val client: ElasticClient = ElasticClient(JavaClient(ElasticProperties("http://dummy")))
  val index                 = "index"

  "ElasticClient#execute" should "compile and infer effect type as `IO`" in {
    for {
      r1 <- client.execute(createIndex(index))
      r2 <- client.execute(deleteIndex(index))
    } yield (r1, r2)
  }

  it should "still compile with other Cats `Async` instances with explicit type annotations" in {
    for {
      r1 <- client.execute[CreateIndexRequest, CreateIndexResponse, OptionIO](createIndex(index))
      r2 <- client.execute[DeleteIndexRequest, DeleteIndexResponse, OptionIO](deleteIndex(index))
    } yield (r1, r2)
  }

  it should "still compile with `Future` with explicit type annotations" in {
    for {
      r1 <- client.execute[CreateIndexRequest, CreateIndexResponse, Future](createIndex(index))
      r2 <- client.execute[DeleteIndexRequest, DeleteIndexResponse, Future](deleteIndex(index))
    } yield (r1, r2)
  }
}
