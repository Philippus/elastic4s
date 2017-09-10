package com.sksamuel.elastic4s

import java.util.UUID

import com.sksamuel.elastic4s.indexes.IndexDefinition
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import com.sksamuel.exts.OptionImplicits._
import scala.util.Try

class HitReaderTest extends FlatSpec with MockitoSugar with DiscoveryLocalNodeProvider with Matchers {

  import com.sksamuel.elastic4s.http.ElasticDsl._

  private val IndexName = "football"

  case class Team(name: String, stadium: String, founded: Int)

  implicit val TeamIndexable = new Indexable[Team] {
    override def json(t: Team): String =
      s"""{ "name" : "${t.name}", "stadium" : "${t.stadium}", "founded" : ${t.founded} }"""
  }

  implicit val HitReader = new HitReader[Team] {
    override def read(hit: Hit): Either[Throwable, Team] =
      Right(Team(
        hit.sourceField("name").toString,
        hit.sourceField("stadium").toString,
        hit.sourceField("founded").toString.toInt
      ))
  }

  http.execute {
    createIndex(IndexName).mappings(
      mapping("teams").fields(
        textField("name"),
        textField("stadium"),
        intField("founded")
      )
    )
  }.await

  def indexRequest(id: Any, team: Team): IndexDefinition = indexInto(IndexName).source(team).id(id)

  http.execute(
    bulk(
      indexRequest(1, Team("Middlesbrough", "Fortress Riverside", 1876)),
      indexRequest(2, Team("Arsenal", "The Library", 1886))
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "hit reader" should "unmarshall search results" in {
    val teams = http.execute {
      search("football").matchAllQuery()
    }.await.right.get.to[Team]

    teams.toSet shouldBe Set(
      Team("Arsenal", "The Library", 1886),
      Team("Middlesbrough", "Fortress Riverside", 1876)
    )
  }

  it should "unmarshall safely search results" in {
    val teams = http.execute {
      search("football").matchAllQuery()
    }.await.right.get.safeTo[Team]

    teams.toSet shouldBe Set(
      Right(Team("Arsenal", "The Library", 1886)),
      Right(Team("Middlesbrough", "Fortress Riverside", 1876))
    )
  }

  it should "unmarshall safely a get response" in {
    val team = http.execute {
      get(1).from(IndexName)
    }.await.safeTo[Team]

    team shouldBe Right(Team("Middlesbrough", "Fortress Riverside", 1876))
  }

  it should "unmarshall a get response" in {
    val team = http.execute {
      get(1).from(IndexName)
    }.await.to[Team]

    team shouldBe Team("Middlesbrough", "Fortress Riverside", 1876)
  }

  it should "unmarshall safely multi get results" in {
    val teams = http.execute {
      multiget(
        get(1).from(IndexName),
        get(2).from(IndexName)
      )
    }.await.safeTo[Team]

    teams.toSet shouldBe Set(
      Right(Team("Arsenal", "The Library", 1886)),
      Right(Team("Middlesbrough", "Fortress Riverside", 1876))
    )
  }

  it should "unmarshall multi get results" in {
    val teams = http.execute {
      multiget(
        get(1).from(IndexName),
        get(2).from(IndexName)
      )
    }.await.to[Team]

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
      http.execute {
        deleteIndex("galaxies")
      }.await
    }

    import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

    http.execute {
      indexInto("galaxies/g").doc(milkyway).refresh(RefreshPolicy.IMMEDIATE)
    }.await

    http.execute {
      search("galaxies").matchAllQuery()
    }.await.right.get.to[Galaxy].head shouldBe milkyway
  }
}

case class Galaxy(quadrants: Seq[Quadrant])
case class Quadrant(name: String, races: Map[UUID, Race])
case class Race(name: String, homeworld: Planet, population: Long, peaceful: Boolean, affiliation: Affiliation, leader: Option[String])
case class Planet(name: String, x: Double, y: Double, z: Double)
