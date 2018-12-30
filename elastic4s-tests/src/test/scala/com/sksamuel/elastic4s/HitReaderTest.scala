package com.sksamuel.elastic4s

import java.util.UUID

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.indexes.IndexRequest
import com.sksamuel.elastic4s.testkit.DockerTests
import com.sksamuel.exts.OptionImplicits._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}

import scala.util.{Success, Try}

class HitReaderTest extends FlatSpec with MockitoSugar with DockerTests with Matchers {

  private val IndexName = "football"

  case class Team(name: String, stadium: String, founded: Int)

  implicit val TeamIndexable: Indexable[Team] = new Indexable[Team] {
    override def json(t: Team): String =
      s"""{ "name" : "${t.name}", "stadium" : "${t.stadium}", "founded" : ${t.founded} }"""
  }

  implicit val HitReader: HitReader[Team] = new HitReader[Team] {
    override def read(hit: Hit): Try[Team] =
      Try(Team(
        hit.sourceField("name").toString,
        hit.sourceField("stadium").toString,
        hit.sourceField("founded").toString.toInt
      ))
  }

  Try {
    client.execute {
      deleteIndex(IndexName)
    }.await
  }

  client.execute {
    createIndex(IndexName).mappings(
      mapping(IndexName).fields(
        textField("name"),
        textField("stadium"),
        intField("founded")
      )
    )
  }.await

  def indexRequest(id: String, team: Team): IndexRequest = indexInto(Index(IndexName), IndexName).source(team).id(id)

  client.execute(
    bulk(
      indexRequest("1", Team("Middlesbrough", "Fortress Riverside", 1876)),
      indexRequest("2", Team("Arsenal", "The Library", 1886))
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "hit reader" should "unmarshall search results" in {
    val teams = client.execute {
      search("football").matchAllQuery()
    }.await.result.to[Team]

    teams.toSet shouldBe Set(
      Team("Arsenal", "The Library", 1886),
      Team("Middlesbrough", "Fortress Riverside", 1876)
    )
  }

  it should "unmarshall safely search results" in {
    val teams = client.execute {
      search("football").matchAllQuery()
    }.await.result.safeTo[Team]

    teams.toSet shouldBe Set(
      Success(Team("Arsenal", "The Library", 1886)),
      Success(Team("Middlesbrough", "Fortress Riverside", 1876))
    )
  }

  it should "unmarshall safely a get response" in {
    val team = client.execute {
      get("1").from(IndexName)
    }.await.result.safeTo[Team]

    team shouldBe Success(Team("Middlesbrough", "Fortress Riverside", 1876))
  }

  it should "unmarshall a get response" in {
    val team = client.execute {
      get("1").from(IndexName)
    }.await.result.to[Team]

    team shouldBe Team("Middlesbrough", "Fortress Riverside", 1876)
  }

  it should "unmarshall safely multi get results" in {
    val teams = client.execute {
      multiget(
        get("1").from(IndexName),
        get("2").from(IndexName)
      )
    }.await.result.safeTo[Team]

    teams.toSet shouldBe Set(
      Success(Team("Arsenal", "The Library", 1886)),
      Success(Team("Middlesbrough", "Fortress Riverside", 1876))
    )
  }

  it should "unmarshall multi get results" in {
    val teams = client.execute {
      multiget(
        get("1").from(IndexName),
        get("2").from(IndexName)
      )
    }.await.result.to[Team]

    teams.toSet shouldBe Set(
      Team("Arsenal", "The Library", 1886),
      Team("Middlesbrough", "Fortress Riverside", 1876)
    )
  }

  it should "support all common types" in {

    val milkyway = Galaxy(
      Seq(
        Quadrant("alpha", Map(
          UUID.randomUUID -> Race("humans", Planet("earth", 0, 0, 0), 19128948125L, true, Affiliation.Federation, None),
          UUID.randomUUID -> Race("vulcans", Planet("Vulcan", 156.13, 360.0, 98.12), 998342345L, true, Affiliation.Federation, None)
        )),
        Quadrant("beta", Map(
          UUID.randomUUID -> Race("romulans", Planet("Romulus", 510, 236.2, 65.2), 73454525L, true, Affiliation.Other, "Shinzon".some)
        )),
        Quadrant("gamma", Map(
          UUID.randomUUID -> Race("vorta", Planet("Kurill Prime", 11.51, 136.2, 265.6), 4389976L, true, Affiliation.Dominion, "Weyoun".some)
        ))
      )
    )

    Try {
      client.execute {
        deleteIndex("galaxies")
      }.await
    }

    import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

    client.execute {
      indexInto("galaxies/g").doc(milkyway).refresh(RefreshPolicy.IMMEDIATE)
    }.await

    client.execute {
      search("galaxies").matchAllQuery()
    }.await.result.to[Galaxy].head shouldBe milkyway
  }
}

case class Galaxy(quadrants: Seq[Quadrant])
case class Quadrant(name: String, races: Map[UUID, Race])
case class Race(name: String, homeworld: Planet, population: Long, peaceful: Boolean, affiliation: Affiliation, leader: Option[String])
case class Planet(name: String, x: Double, y: Double, z: Double)
