package com.sksamuel.elastic4s.requests

import com.sksamuel.elastic4s.requests.mappings.FieldType.{BinaryType, BooleanType, ByteType, CompletionType, DateType, DoubleType, FloatType, GeoPointType, GeoShapeType, IntegerType, IpType, KeywordType, LongType, NestedType, ObjectType, PercolatorType, ShortType, TextType, TokenCountType}
import com.sksamuel.elastic4s.requests.mappings.{BasicField, CompletionField, GeoshapeField, JoinField, KeywordField, NestedField, ObjectField, RangeField, TextField}
import com.sksamuel.elastic4s.requests.script.{Script, ScriptField}

trait TypesApi {

  // string datatypes
  def keywordField(name: String): KeywordField = KeywordField(name)
  def textField(name: String): TextField       = TextField(name)

  // numeric datatypes
  def byteField(name: String): BasicField        = BasicField(name, "byte")
  def doubleField(name: String): BasicField      = BasicField(name, "double")
  def floatField(name: String): BasicField       = BasicField(name, "float")
  def halfFloatField(name: String): BasicField   = BasicField(name, "half_float")
  def intField(name: String): BasicField         = BasicField(name, "integer")
  def longField(name: String): BasicField        = BasicField(name, "long")
  def scaledFloatField(name: String): BasicField = BasicField(name, "scaled_float")
  def shortField(name: String): BasicField       = BasicField(name, "short")

  // booleans
  def booleanField(name: String): BasicField = BasicField(name, "boolean")

  // binaries
  def binaryField(name: String): BasicField = BasicField(name, "binary")

  // dates
  def dateField(name: String): BasicField = BasicField(name, "date")

  // geo
  def geopointField(name: String): BasicField    = BasicField(name, "geo_point")
  def geoshapeField(name: String): GeoshapeField = GeoshapeField(name)

  // range
  def integerRangeField(name: String): RangeField = RangeField(name, "integer_range")
  def floatRangeField(name: String): RangeField   = RangeField(name, "float_range")
  def longRangeField(name: String): RangeField    = RangeField(name, "long_range")
  def doubleRangeField(name: String): RangeField  = RangeField(name, "double_range")
  def dateRangeField(name: String): RangeField    = RangeField(name, "date_range")
  def ipRangeField(name: String): RangeField      = RangeField(name, "ip_range")

  // complex datatypes
  def nestedField(name: String): NestedField = NestedField(name)
  def objectField(name: String): ObjectField = ObjectField(name)

  // specialized
  def completionField(name: String): CompletionField = CompletionField(name)
  def ipField(name: String): BasicField              = BasicField(name, "ip")
  def tokenCountField(name: String): BasicField      = BasicField(name, "token_count")
  def percolatorField(name: String): BasicField      = BasicField(name, "percolator")
  def joinField(name: String): JoinField             = JoinField(name)

  def scriptField(name: String, script: String): ScriptField = ScriptField(name, script)
  def scriptField(name: String, script: Script): ScriptField = ScriptField(name, script)
  def scriptField(name: String): ExpectsScript               = ExpectsScript(name)
  case class ExpectsScript(name: String) {
    def script(script: String): ScriptField = ScriptField(name, script)
    def script(script: Script): ScriptField = ScriptField(name, script)
  }

  @deprecated("use binaryField(name)", "5.2.11")
  def field(name: String, ft: BinaryType.type): BasicField = binaryField(name)

  @deprecated("use booleanField(name)", "5.2.11")
  def field(name: String, ft: BooleanType.type): BasicField = booleanField(name)

  @deprecated("use byteField(name)", "5.2.11")
  def field(name: String, ft: ByteType.type): BasicField = byteField(name)

  @deprecated("use completionField(name)", "5.2.11")
  def field(name: String, ft: CompletionType.type): CompletionField = completionField(name)

  @deprecated("use dateField(name)", "5.2.11")
  def field(name: String, ft: DateType.type): BasicField = dateField(name)

  @deprecated("use doubleField(name)", "5.2.11")
  def field(name: String, ft: DoubleType.type): BasicField = doubleField(name)

  @deprecated("use floatField(name)", "5.2.11")
  def field(name: String, ft: FloatType.type): BasicField = floatField(name)

  @deprecated("use geopointField(name)", "5.2.11")
  def field(name: String, ft: GeoPointType.type): BasicField = geopointField(name)

  @deprecated("use geoshapeField(name)", "5.2.11")
  def field(name: String, ft: GeoShapeType.type): GeoshapeField = geoshapeField(name)

  @deprecated("use intField(name)", "5.2.11")
  def field(name: String, ft: IntegerType.type): BasicField = intField(name)

  @deprecated("use ipField(name)", "5.2.11")
  def field(name: String, ft: IpType.type): BasicField = ipField(name)

  @deprecated("use keywordField(name)", "5.2.11")
  def field(name: String, ft: KeywordType.type): KeywordField = keywordField(name)

  @deprecated("use longField(name)", "5.2.11")
  def field(name: String, ft: LongType.type): BasicField = longField(name)

  @deprecated("use nestedField(name)", "5.2.11")
  def field(name: String, ft: NestedType.type): NestedField = nestedField(name)

  @deprecated("use objectField(name)", "5.2.11")
  def field(name: String, ft: ObjectType.type): ObjectField = objectField(name)

  @deprecated("use percolatorField(name)", "5.2.11")
  def field(name: String, ft: PercolatorType.type): BasicField = percolatorField(name)

  @deprecated("use shortField(name)", "5.2.11")
  def field(name: String, ft: ShortType.type): BasicField = shortField(name)

  @deprecated("use textField(name)", "5.2.11")
  def field(name: String, ft: TextType.type): TextField = textField(name)

  @deprecated("use tokenCountField(name)", "5.2.11")
  def field(name: String, ft: TokenCountType.type): BasicField = tokenCountField(name)

  @deprecated("use field(name, type)", "5.0.0")
  def field(name: String) = new {

    @deprecated("use binaryField(name)", "5.2.11")
    def withType(ft: BinaryType.type): BasicField = binaryField(name)

    @deprecated("use booleanField(name)", "5.2.11")
    def withType(ft: BooleanType.type): BasicField = booleanField(name)

    @deprecated("use byteField(name)", "5.2.11")
    def withType(ft: ByteType.type): BasicField = byteField(name)

    @deprecated("use completionField(name)", "5.2.11")
    def withType(ft: CompletionType.type): CompletionField = completionField(name)

    @deprecated("use dateField(name)", "5.2.11")
    def withType(ft: DateType.type): BasicField = dateField(name)

    @deprecated("use doubleField(name)", "5.2.11")
    def withType(ft: DoubleType.type): BasicField = doubleField(name)

    @deprecated("use floatField(name)", "5.2.11")
    def withType(ft: FloatType.type): BasicField = floatField(name)

    @deprecated("use geopointField(name)", "5.2.11")
    def withType(ft: GeoPointType.type): BasicField = geopointField(name)

    @deprecated("use byteField(name)", "5.2.11")
    def withType(ft: GeoShapeType.type): GeoshapeField = geoshapeField(name)

    @deprecated("use byteField(name)", "5.2.11")
    def withType(ft: IntegerType.type): BasicField = intField(name)

    @deprecated("use ipField(name)", "5.2.11")
    def withType(ft: IpType.type): BasicField = ipField(name)

    @deprecated("use byteField(name)", "5.2.11")
    def withType(ft: LongType.type): BasicField = longField(name)

    @deprecated("use byteField(name)", "5.2.11")
    def withType(ft: NestedType.type): NestedField = nestedField(name)

    @deprecated("use byteField(name)", "5.2.11")
    def withType(ft: ObjectType.type): ObjectField = objectField(name)

    @deprecated("use byteField(name)", "5.2.11")
    def withType(ft: ShortType.type): BasicField = shortField(name)

    @deprecated("use textField(name)", "5.2.11")
    def withType(ft: TextType.type): TextField = textField(name)

    @deprecated("use tokenCountField(name)", "5.2.11")
    def withType(ft: TokenCountType.type): BasicField = tokenCountField(name)

    @deprecated("use binaryField(name)", "5.2.11")
    def typed(ft: BinaryType.type): BasicField = binaryField(name)

    @deprecated("use boolean(name)", "5.2.11")
    def typed(ft: BooleanType.type): BasicField = booleanField(name)

    @deprecated("use byteField(name)", "5.2.11")
    def typed(ft: ByteType.type): BasicField = byteField(name)

    @deprecated("use completionField(name)", "5.2.11")
    def typed(ft: CompletionType.type): CompletionField = completionField(name)

    @deprecated("use dateField(name)", "5.2.11")
    def typed(ft: DateType.type): BasicField = dateField(name)

    @deprecated("use doubleField(name)", "5.2.11")
    def typed(ft: DoubleType.type): BasicField = doubleField(name)

    @deprecated("use floatField(name)", "5.2.11")
    def typed(ft: FloatType.type): BasicField = floatField(name)

    @deprecated("use geopointField(name)", "5.2.11")
    def typed(ft: GeoPointType.type): BasicField = geopointField(name)

    @deprecated("use geoshapeField(name)", "5.2.11")
    def typed(ft: GeoShapeType.type): GeoshapeField = geoshapeField(name)

    @deprecated("use intField(name)", "5.2.11")
    def typed(ft: IntegerType.type): BasicField = intField(name)

    @deprecated("use ipField(name)", "5.2.11")
    def typed(ft: IpType.type): BasicField = ipField(name)

    @deprecated("use keywordField(name)", "5.2.11")
    def typed(ft: KeywordType.type): KeywordField = keywordField(name)

    @deprecated("use longField(name)", "5.2.11")
    def typed(ft: LongType.type): BasicField = longField(name)

    @deprecated("use nestedField(name)", "5.2.11")
    def typed(ft: NestedType.type): NestedField = nestedField(name)

    @deprecated("use objectField(name)", "5.2.11")
    def typed(ft: ObjectType.type): ObjectField = objectField(name)

    @deprecated("use shortField(name)", "5.2.11")
    def typed(ft: ShortType.type): BasicField = shortField(name)

    @deprecated("use textField(name)", "5.2.11")
    def typed(ft: TextType.type): TextField = textField(name)

    @deprecated("use tokenCountField(name)", "5.2.11")
    def typed(ft: TokenCountType.type): BasicField = tokenCountField(name)
  }
}
