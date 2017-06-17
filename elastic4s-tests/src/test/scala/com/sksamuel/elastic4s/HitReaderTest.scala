package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.indexes.IndexDefinition
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.mockito.MockitoSugar

class HitReaderTest extends FlatSpec with MockitoSugar with DiscoveryLocalNodeProvider with Matchers  {

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

  def indexRequest(id: Any, team: Team): IndexDefinition = indexInto(IndexName / "teams").source(team).id(id)

  http.execute(
    bulk(
      indexRequest(1, Team("Middlesbrough", "Fortress Riverside", 1876)),
      indexRequest(2, Team("Arsenal", "The Library", 1886))
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "hit reader" should "unmarshall search results" in {
    val teams = http.execute {
      search("football").matchAllQuery()
    }.await.to[Team]

    teams.toSet shouldBe Set(
      Team("Arsenal", "The Library", 1886),
      Team("Middlesbrough", "Fortress Riverside", 1876)
    )
  }

  it should "unmarshall safely search results" in {
    val teams = http.execute {
      search("football").matchAllQuery()
    }.await.safeTo[Team]

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
}
