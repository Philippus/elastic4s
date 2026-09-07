package com.sksamuel.elastic4s.requests.mappings

import com.sksamuel.elastic4s.analysis.LanguageAnalyzers
import com.sksamuel.elastic4s.fields.{DoubleField, DynamicField, TextField}
import com.sksamuel.elastic4s.handlers.index.mapping.DynamicTemplateBodyFn
import com.sksamuel.elastic4s.requests.mappings.dynamictemplate.DynamicTemplateRequest
import com.sksamuel.elastic4s.{ElasticApi, JsonSugar}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class DynamicTemplateApiTest extends AnyWordSpec with Matchers with JsonSugar with ElasticApi {

  "dynamic templates" should {
    "support match" in {
      val temp = DynamicTemplateRequest(
        "es",
        TextField("match_*_es").analyzer(LanguageAnalyzers.spanish)
      ).matchMappingType("string").`match`("*_es")
      DynamicTemplateBodyFn.build(temp).string should matchJsonResource("/json/mappings/dynamic_template_match.json")
    }
    "support match as iterable" in {
      val temp = DynamicTemplateRequest(
        "es",
        TextField("match_*_es").analyzer(LanguageAnalyzers.spanish)
      ).matchMappingType("string").`match`(Seq("*_es", "spanish"))
      DynamicTemplateBodyFn.build(temp).string should
        matchJsonResource("/json/mappings/dynamic_template_match_iterable.json")
    }
    "support unmatch" in {
      val temp = DynamicTemplateRequest(
        "es",
        TextField("match_*_es").analyzer(LanguageAnalyzers.spanish)
      ).matchMappingType("string").unmatch("*_nl")
      DynamicTemplateBodyFn.build(temp).string should matchJsonResource("/json/mappings/dynamic_template_unmatch.json")
    }
    "support unmatch as iterable" in {
      val temp = DynamicTemplateRequest(
        "es",
        TextField("match_*_es").analyzer(LanguageAnalyzers.spanish)
      ).matchMappingType("string").unmatch(Seq("*_nl", "*_en"))
      DynamicTemplateBodyFn.build(temp).string should
        matchJsonResource("/json/mappings/dynamic_template_unmatch_iterable.json")
    }
    "support path match and unmatch" in {
      val temp = DynamicTemplateRequest(
        "full_name",
        TextField("full_name").copyTo("full_name")
      ).pathMatch("name.*").pathUnmatch("*.middle")
      DynamicTemplateBodyFn.build(temp).string should
        matchJsonResource("/json/mappings/dynamic_template_pathmatch_and_pathunmatch.json")
    }
    "support path match and unmatch as iterables" in {
      val temp = DynamicTemplateRequest(
        "full_name",
        TextField("full_name").copyTo("full_name")
      ).pathMatch(Seq("name.*", "user.name.*")).pathUnmatch(Seq("*.middle", "*.midinitial"))
      DynamicTemplateBodyFn.build(temp).string should
        matchJsonResource("/json/mappings/dynamic_template_pathmatch_and_pathunmatch_iterable.json")
    }
    "support match mapping type and unmatch mapping type" in {
      val temp = DynamicTemplateRequest(
        "numeric_counts",
        DoubleField("whatever")
      ).matchMappingType("long").unmatchMappingType("string")
      DynamicTemplateBodyFn.build(temp).string should
        matchJsonResource("/json/mappings/dynamic_template_matchmappingtype_and_unmatchmappingtype.json")
    }
    "support match mapping type and unmatch mapping type as iterables" in {
      val temp = DynamicTemplateRequest(
        "numeric_counts",
        DoubleField("whatever")
      ).matchMappingType(Seq("long", "double")).unmatchMappingType(Seq("string", "text"))
      DynamicTemplateBodyFn.build(temp).string should
        matchJsonResource("/json/mappings/dynamic_template_matchmappingtype_and_unmatchmappingtype_iterable.json")
    }
    "support match pattern" in {
      val temp = DynamicTemplateRequest("es", TextField("matchPattern_*_es").analyzer(LanguageAnalyzers.spanish))
        .matchMappingType("string").matchPattern("*_es")
      DynamicTemplateBodyFn.build(temp).string should matchJsonResource(
        "/json/mappings/dynamic_template_match_pattern.json"
      )
    }
    "support dynamic type" in {
      val temp = DynamicTemplateRequest("es", DynamicField("", docValues = Some(false))).matchPattern("*_es")
      DynamicTemplateBodyFn.build(temp).string should matchJsonResource(
        "/json/mappings/dynamic_template_dynamic_type.json"
      )
    }
  }
}
