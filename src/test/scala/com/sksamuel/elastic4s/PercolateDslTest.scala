package com.sksamuel.elastic4s

import org.scalatest.{FlatSpec, OneInstancePerTest}
import org.scalatest.mock.MockitoSugar
import PercolateDsl._
import com.fasterxml.jackson.databind.ObjectMapper

/** @author Stephen Samuel */
class PercolateDslTest extends FlatSpec with MockitoSugar with OneInstancePerTest {

    val mapper = new ObjectMapper()

    "the percolate dsl" should "should generate json for a register query" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/percolate_register.json"))
        val req = register into "captains" query "cook"
        assert(json === mapper.readTree(req.build.source().toUtf8))
    }

    it should "should generate fields json for a percolate request" in {
        val json = mapper.readTree(getClass.getResource("/com/sksamuel/elastic4s/percolate_request.json"))
        val req = percolate in "captains" fields "name" -> "cook"
        assert(json === mapper.readTree(req._source.string))
    }
}
