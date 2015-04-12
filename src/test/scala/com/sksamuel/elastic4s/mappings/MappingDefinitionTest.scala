package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.mappings.MappingDefinition
import org.scalatest.{ WordSpec, Matchers }

class MappingDefinitionTest extends WordSpec with Matchers {

  "mapping definition" should {
    "not insert date detection by default" in {
      val mapping = new MappingDefinition("type")
      val output = mapping.build.string()
      output should not include "date"
    }
    "insert date detection when set to true" in {
      val mapping = new MappingDefinition("type").dateDetection(true)
      val output = mapping.build.string()
      output should include("""date_detection":true""")
    }
    "insert date detection when set to false" in {
      val mapping = new MappingDefinition("type").dateDetection(false)
      val output = mapping.build.string()
      output should include("""date_detection":false""")
    }
    "not insert numeric detection by default" in {
      val mapping = new MappingDefinition("type")
      val output = mapping.build.string()
      output should not include "numeric"
    }
    "insert numeric detection when set to true" in {
      val mapping = new MappingDefinition("type").numericDetection(true)
      val output = mapping.build.string()
      output should include("""numeric_detection":true""")
    }
    "insert numeric detection when set to false" in {
      val mapping = new MappingDefinition("type").numericDetection(false)
      val output = mapping.build.string()
      output should include("""numeric_detection":false""")
    }
  }
}
