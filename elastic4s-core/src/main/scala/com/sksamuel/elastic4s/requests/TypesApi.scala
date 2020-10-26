package com.sksamuel.elastic4s.requests

import com.sksamuel.elastic4s.requests.mappings._
import com.sksamuel.elastic4s.requests.script.{Script, ScriptField}

trait TypesApi {

  def keywordField(name: String): KeywordField            = KeywordField(name)
  def textField(name: String): TextField                  = TextField(name)
  def wildcardField(name: String): WildcardField          = WildcardField(name)
  def searchAsYouType(name: String): SearchAsYouTypeField = SearchAsYouTypeField(name)

  def byteField(name: String): BasicField        = BasicField(name, "byte")
  def doubleField(name: String): BasicField      = BasicField(name, "double")
  def floatField(name: String): BasicField       = BasicField(name, "float")
  def halfFloatField(name: String): BasicField   = BasicField(name, "half_float")
  def intField(name: String): BasicField         = BasicField(name, "integer")
  def longField(name: String): BasicField        = BasicField(name, "long")
  def scaledFloatField(name: String): BasicField = BasicField(name, "scaled_float")
  def shortField(name: String): BasicField       = BasicField(name, "short")

  def booleanField(name: String): BasicField = BasicField(name, "boolean")

  def binaryField(name: String): BasicField = BasicField(name, "binary")

  def dateField(name: String): BasicField = BasicField(name, "date")

  def geopointField(name: String): BasicField    = BasicField(name, "geo_point")
  def geoshapeField(name: String): GeoshapeField = GeoshapeField(name)

  // range
  def integerRangeField(name: String): RangeField = RangeField(name, "integer_range")
  def floatRangeField(name: String): RangeField   = RangeField(name, "float_range")
  def longRangeField(name: String): RangeField    = RangeField(name, "long_range")
  def doubleRangeField(name: String): RangeField  = RangeField(name, "double_range")
  def dateRangeField(name: String): RangeField    = RangeField(name, "date_range")
  def ipRangeField(name: String): RangeField      = RangeField(name, "ip_range")

  def flattenedField(name: String): BasicField = BasicField(name, "flattened")

  // complex datatypes
  def nestedField(name: String): NestedField = NestedField(name)
  def objectField(name: String): ObjectField = ObjectField(name)

  // specialized
  def completionField(name: String): CompletionField = CompletionField(name)
  def ipField(name: String): BasicField              = BasicField(name, "ip")
  def tokenCountField(name: String): BasicField      = BasicField(name, "token_count")
  def percolatorField(name: String): BasicField      = BasicField(name, "percolator")
  def joinField(name: String): JoinField             = JoinField(name)

  def denseVectorField(name: String, dims: Int): DenseVectorField = DenseVectorField(name, dims)

  def scriptField(name: String, script: String): ScriptField = ScriptField(name, script)
  def scriptField(name: String, script: Script): ScriptField = ScriptField(name, script)

  @deprecated("use scriptField(name script)")
  def scriptField(name: String): ExpectsScript               = ExpectsScript(name)
  case class ExpectsScript(name: String) {
    @deprecated("use scriptField(name script)")
    def script(script: String): ScriptField = ScriptField(name, script)
    @deprecated("use scriptField(name script)")
    def script(script: Script): ScriptField = ScriptField(name, script)
  }
}
