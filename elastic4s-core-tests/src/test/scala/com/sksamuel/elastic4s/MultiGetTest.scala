package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.elasticsearch.index.VersionType
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import org.scalatest.mock.MockitoSugar

import scala.collection.JavaConverters._

/** @author Stephen Samuel */
class MultiGetTest extends FlatSpec with MockitoSugar with ElasticSugar {

  client.execute {
    create index "coldplay" shards 2
  }.await

  def album(number: Long, name: String, year: Int, revision: Long) = {
    (
      index into "coldplay/albums"
        fields ("name" -> name, "year" -> year)
        id number
        version revision
        versionType VersionType.EXTERNAL
    )
  }

  client.execute(
    bulk(
      album(1, "parachutes", 2000, 5) routing "2",
      album(3, "x&y", 2005, 4),
      album(5, "mylo xyloto", 2011, 2),
      album(7, "ghost stories", 2005, 1) routing "1"
    )
  ).await

  refresh("coldplay")
  blockUntilCount(4, "coldplay")

  "a multiget request" should "retrieve documents by id" in {

    val resp = client.execute(
      multiget(
        get id 3 from "coldplay/albums",
        get id 5 from "coldplay/albums",
        get id 34 from "coldplay/albums"
      ) preference Preference.Local refresh true realtime true
    ).await
    assert(3 === resp.getResponses.size)
    assert("3" === resp.getResponses.toSeq.head.getResponse.getId)
    assert("5" === resp.getResponses.toSeq(1).getResponse.getId)
    assert(!resp.getResponses.toSeq(2).getResponse.isExists)
  }

  it should "retrieve documents by id with routing" in {

    val resp = client.execute(
      multiget(
        get id 6 from "coldplay/albums" routing "2",
        get id 1 from "coldplay/albums" routing "2"
      ) preference Preference.Local refresh true realtime true
    ).await
    assert(2 === resp.getResponses.size)
    assert(!resp.getResponses.toSeq.head.getResponse.isExists)
    assert("1" === resp.getResponses.toSeq(1).getResponse.getId)
  }

  it should "retrieve documents by id with selected fields" in {

    val resp = client.execute(
      multiget(
        get id 3 from "coldplay/albums" fields("name", "year"),
        get id 5 from "coldplay/albums" fields "name"
      ) preference Preference.Local refresh true realtime true
    ).await
    assert(2 === resp.getResponses.size)
    assert(resp.getResponses.toSeq.head.getResponse.getFields.keySet().asScala === Set("name", "year"))
    assert(resp.getResponses.toSeq(1).getResponse.getFields.keySet().asScala === Set("name"))
  }

  it should "retrieve documents by id with fetchSourceContext" in {

    val resp = client.execute(
      multiget(
        get id 3 from "coldplay/albums" fetchSourceContext Seq("name", "year"),
        get id 5 from "coldplay/albums" fields "name" fetchSourceContext Seq("name")
      ) preference Preference.Local refresh true realtime true
    ).await
    assert(2 === resp.responses.size)
    assert(resp.responses.head.original.getResponse.getSource.asScala.keySet === Set("name", "year"))
    assert(resp.responses.last.original.getResponse.getSource.asScala.keySet === Set("name"))
  }

  it should "retrieve documents by id and version" in {
    val resp = client.execute(
      multiget(
        get id 3 from "coldplay/albums" version 1,
        get id 3 from "coldplay/albums" version 4
      ) preference Preference.Local refresh true realtime true
    ).await
    assert(2 === resp.getResponses.size)
    assert(resp.getResponses.toSeq.head.isFailed)
    resp.getResponses.toSeq.head.getFailure.getFailure != null shouldBe true
    assert(resp.getResponses.toSeq(1).getResponse.isExists)
    assert(resp.getResponses.toSeq(1).getResponse.getVersion === 4)
  }
}
