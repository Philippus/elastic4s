package com.sksamuel.elastic4s.shapeless

import shapeless._
import shapeless.labelled._
import shapeless.record._
import shapeless.ops.record._
import shapeless.ops.hlist.ToList
import com.sksamuel.elastic4s.{ HitAs, RichSearchHit }
import com.sksamuel.elastic4s.SearchDefinition

/**
 * This implementation requires the user to add the additional fields
 *
 * {{{
 * client.execute {
 *   search in "places" / "cities" fields("id", "name")
 * }
 * }}}
 * 
 * 
 * TODO provide fields name derivation as well
 * 
 * {{{
 * def fromDocument[T, Repr <: HList, KeysRepr <: HList](doc: T)(
 *     implicit
 *     gen: LabelledGeneric.Aux[T, Repr],
 *     keys: Keys.Aux[Repr, KeysRepr],
 *     ktl: ToList[KeysRepr, String]
 * ): List[String] = {
 *   gen.to(doc).keys.toList
 *   // keys().toList
 * }
 * }}}
 * 
 */
trait HitAsFromFieldsInstances {

  /** base representation */
  implicit def hitAsHNil: HitAs[HNil] = new HitAs[HNil] {
    override def as(hit: RichSearchHit): HNil = HNil
  }

  // primitive types and String

  implicit def stringHitAsHCons[K <: Symbol, Rest <: HList](
    implicit key: Witness.Aux[K],
    hitRest: HitAs[Rest]) = new HitAs[FieldType[K, String] :: Rest] {
    override def as(hit: RichSearchHit) = {
      val value = hit.field(key.value.name).getValue[String]
      field[K](value) :: hitRest.as(hit)
    }
  }

  implicit def anyValhitAsHCons[K <: Symbol, V <: AnyVal, Rest <: HList](
    implicit key: Witness.Aux[K],
    hitRest: HitAs[Rest]) = new HitAs[FieldType[K, V] :: Rest] {
    override def as(hit: RichSearchHit) = {
      val value = hit.field(key.value.name).value[V]
      field[K](value) :: hitRest.as(hit)
    }
  }

  // generic representation to case class

  implicit def hitAsGeneric[T, Repr](
    implicit gen: LabelledGeneric.Aux[T, Repr],
    hitsRepr: HitAs[Repr]) = new HitAs[T] {
    override def as(hit: RichSearchHit): T = gen.from(hitsRepr.as(hit))
  }
  
}


/**
 * This implementation gets the information directly from the source map.
 *
 * - No need for additional fields in search query 
 * - No need for field names derivation
 */
trait HitAsFromSourceInstances {

  /** base representation */
  implicit def hitAsHNil: HitAs[HNil] = new HitAs[HNil] {
    override def as(hit: RichSearchHit): HNil = HNil
  }

  // primitive types and String

  implicit def stringHitAsHCons[K <: Symbol, Rest <: HList](
    implicit key: Witness.Aux[K],
    hitRest: HitAs[Rest]) = new HitAs[FieldType[K, String] :: Rest] {
    override def as(hit: RichSearchHit) = {
      val value = hit.sourceAsMap(key.value.name).asInstanceOf[String]
      field[K](value) :: hitRest.as(hit)
    }
  }

  implicit def anyValhitAsHCons[K <: Symbol, V <: AnyVal, Rest <: HList](
    implicit key: Witness.Aux[K],
    hitRest: HitAs[Rest]) = new HitAs[FieldType[K, V] :: Rest] {
    override def as(hit: RichSearchHit) = {
      val value = hit.sourceAsMap(key.value.name).asInstanceOf[V]
      field[K](value) :: hitRest.as(hit)
    }
  }

  // generic representation to case class

  implicit def hitAsGeneric[T, Repr](
    implicit gen: LabelledGeneric.Aux[T, Repr],
    hitsRepr: HitAs[Repr]) = new HitAs[T] {
    override def as(hit: RichSearchHit): T = gen.from(hitsRepr.as(hit))
  }
}



// ---------------------------
// ------ Test Code ----------
// ---------------------------
object TestDerivation extends App {

  // https://github.com/sksamuel/elastic4s
  // https://github.com/sksamuel/elastic4s/blob/master/guide/search.md

  import com.sksamuel.elastic4s.ElasticClient
  import com.sksamuel.elastic4s.ElasticDsl._
  import com.sksamuel.elastic4s.mappings.FieldType._
  import com.sksamuel.elastic4s.source.Indexable
  import org.elasticsearch.common.settings.Settings

  import java.nio.file.{ Paths, Files }

  // model class
  case class City(id: Int, name: String)

  // this will get shapelessed soon, too
  implicit object CharacterIndexable extends Indexable[City] {
    override def json(t: City): String = s""" { "id" : ${t.id}, "name" : "${t.name}" } """
  }
  // ---

  val tmpDir = Files.createTempDirectory("es-test")

  val settings = Settings.builder().put("path.home", tmpDir.toAbsolutePath.toString)
  val client = ElasticClient.local(settings.build)

  println("-- node started")

  // create index
  client.execute {
    create index "places" mappings (
      mapping name "cities" fields (
        field name "id" typed IntegerType,
        field name "name" typed StringType
      )
    )
  }.await
  println("-- index created")

  // validate index is there
  val mappings = client.execute {
    getMapping("places" / "cities")
  }.await

  println("-- mappings for places/cities")
  println("  " + mappings.mappings("places")("cities").getSourceAsMap)

  // insert some documents
  List((1, "Munich"), (2, "Manchester"), (3, "London")).foreach {
    case (cityId, name) =>
      val indexResult = client.execute {
        index into "places" / "cities" id cityId source City(cityId, name)
      }.await

      println(s"  ${indexResult.index}/${indexResult.`type`}/${indexResult.id}: ${indexResult.created}")
  }

  // wait until ES persisted stuff
  Thread.sleep(1000)

  println("-- start search")

  object FromSource extends HitAsFromSourceInstances {

    def apply() = {
      println("----- from source")
      val resp = client.execute {
        search in "places" / "cities"
      }.await

      println(s"  Found: ${resp.hits.length}")

      val cities = resp.as[City]
      cities.foreach(c => println("  " + c))
    }
  }

  object FromFields extends HitAsFromFieldsInstances {

    def apply() = {
    	println("----- from fields")
    	
    	// TODO derive field names from document class as well
      val resp = client.execute {
        search in "places" / "cities" fields ("id", "name")
      }.await

      println(s"  Found: ${resp.hits.length}")

      val cities = resp.as[City]
      cities.foreach(c => println("  " + c))
    }
  }
  
  // demonstrate both variations
  FromSource()
  FromFields()

  
  // -------------------------------
  // test implicits
  
  // implicitly[HitAs[HNil]]
  // implicitly[HitAs[City]]

  // this should never work
  // implicitly[HitAs[Int :: HNil]]

}
