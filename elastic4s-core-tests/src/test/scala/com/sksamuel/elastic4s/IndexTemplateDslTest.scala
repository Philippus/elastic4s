package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.mappings.FieldType.StringType
import org.scalatest.FreeSpec
import org.scalatest.mock.MockitoSugar

/** @author Stephen Samuel */
class IndexTemplateDslTest extends FreeSpec with MockitoSugar with ElasticSugar with ElasticDsl {

  "a create index request" - {
    "should be well formed" in {
      create template "mytemplate" pattern "te*" mappings {
        "places" as {
          "name" typed StringType
        }
      }
    }
  }

  "a delete index request" - {
    "should be well formed" in {
      delete template "mytemplate"
    }
  }
}
