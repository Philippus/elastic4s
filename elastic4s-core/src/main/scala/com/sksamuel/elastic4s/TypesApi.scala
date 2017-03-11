package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.mappings.FieldType._
import com.sksamuel.elastic4s.mappings._
import com.sksamuel.elastic4s.script.ScriptFieldDefinition

trait TypesApi {

  def binaryField(name: String): BasicFieldDefinition = BasicFieldDefinition(name, "binary")
  def booleanField(name: String): BasicFieldDefinition = BasicFieldDefinition(name, "boolean")
  def byteField(name: String): BasicFieldDefinition = BasicFieldDefinition(name, "byte")
  def completionField(name: String): CompletionFieldDefinition = CompletionFieldDefinition(name)
  def dateField(name: String): BasicFieldDefinition = BasicFieldDefinition(name, "date")
  def doubleField(name: String): BasicFieldDefinition = BasicFieldDefinition(name, "double")
  def floatField(name: String): BasicFieldDefinition = BasicFieldDefinition(name, "float")
  def halfFloatField(name: String): BasicFieldDefinition = BasicFieldDefinition(name, "half_float")
  def scaledFloatField(name: String): BasicFieldDefinition = BasicFieldDefinition(name, "scaled_float")
  def geopointField(name: String): BasicFieldDefinition = BasicFieldDefinition(name, "geo_point")
  def geoshapeField(name: String): GeoshapeFieldDefinition = GeoshapeFieldDefinition(name)
  def intField(name: String): BasicFieldDefinition = BasicFieldDefinition(name, "integer")
  def ipField(name: String): BasicFieldDefinition = BasicFieldDefinition(name, "ip")
  def keywordField(name: String): KeywordFieldDefinition = KeywordFieldDefinition(name)
  def longField(name: String): BasicFieldDefinition = BasicFieldDefinition(name, "long")
  def nestedField(name: String): NestedFieldDefinition = NestedFieldDefinition(name)
  def objectField(name: String): BasicFieldDefinition = BasicFieldDefinition(name, "object")
  def percolatorField(name: String): BasicFieldDefinition = BasicFieldDefinition(name, "percolator")

  def scriptField(name: String, script: String): ScriptFieldDefinition = ScriptFieldDefinition(name, script, None, None)
  def scriptField(n: String): ExpectsScript = ExpectsScript(field = n)
  case class ExpectsScript(field: String) {
    def script(script: String): ScriptFieldDefinition = ScriptFieldDefinition(field, script, None, None)
  }

  def shortField(name: String): BasicFieldDefinition = BasicFieldDefinition(name, "short")
  def textField(name: String): TextFieldDefinition = TextFieldDefinition(name)
  def tokenCountField(name: String): BasicFieldDefinition = BasicFieldDefinition(name, "token_count")

  @deprecated("use binaryField(name)", "5.2.11")
  def field(name: String, ft: BinaryType.type): BasicFieldDefinition = binaryField(name)

  @deprecated("use booleanField(name)", "5.2.11")
  def field(name: String, ft: BooleanType.type): BasicFieldDefinition = booleanField(name)

  @deprecated("use byteField(name)", "5.2.11")
  def field(name: String, ft: ByteType.type): BasicFieldDefinition = byteField(name)

  @deprecated("use completionField(name)", "5.2.11")
  def field(name: String, ft: CompletionType.type): CompletionFieldDefinition = completionField(name)

  @deprecated("use dateField(name)", "5.2.11")
  def field(name: String, ft: DateType.type): BasicFieldDefinition = dateField(name)

  @deprecated("use doubleField(name)", "5.2.11")
  def field(name: String, ft: DoubleType.type): BasicFieldDefinition = doubleField(name)

  @deprecated("use floatField(name)", "5.2.11")
  def field(name: String, ft: FloatType.type): BasicFieldDefinition = floatField(name)

  @deprecated("use geopointField(name)", "5.2.11")
  def field(name: String, ft: GeoPointType.type): BasicFieldDefinition = geopointField(name)

  @deprecated("use geoshapeField(name)", "5.2.11")
  def field(name: String, ft: GeoShapeType.type): GeoshapeFieldDefinition = geoshapeField(name)

  @deprecated("use intField(name)", "5.2.11")
  def field(name: String, ft: IntegerType.type): BasicFieldDefinition = intField(name)

  @deprecated("use ipField(name)", "5.2.11")
  def field(name: String, ft: IpType.type): BasicFieldDefinition = ipField(name)

  @deprecated("use keywordField(name)", "5.2.11")
  def field(name: String, ft: KeywordType.type): KeywordFieldDefinition = keywordField(name)

  @deprecated("use longField(name)", "5.2.11")
  def field(name: String, ft: LongType.type): BasicFieldDefinition = longField(name)

  @deprecated("use nestedField(name)", "5.2.11")
  def field(name: String, ft: NestedType.type): NestedFieldDefinition = nestedField(name)

  @deprecated("use objectField(name)", "5.2.11")
  def field(name: String, ft: ObjectType.type): BasicFieldDefinition = objectField(name)

  @deprecated("use percolatorField(name)", "5.2.11")
  def field(name: String, ft: PercolatorType.type): BasicFieldDefinition = percolatorField(name)

  @deprecated("use shortField(name)", "5.2.11")
  def field(name: String, ft: ShortType.type): BasicFieldDefinition = shortField(name)

  @deprecated("use textField(name)", "5.2.11")
  def field(name: String, ft: TextType.type): TextFieldDefinition = textField(name)

  @deprecated("use tokenCountField(name)", "5.2.11")
  def field(name: String, ft: TokenCountType.type): BasicFieldDefinition = tokenCountField(name)

  @deprecated("string type is deprecated in ES 5, use text or keyword types", "5.0.0")
  def stringField(name: String): BasicFieldDefinition = BasicFieldDefinition(name, "string")

  @deprecated("use field(name, type)", "5.0.0")
  def field(name: String) = new {

    @deprecated("use binaryField(name)", "5.2.11")
    def withType(ft: BinaryType.type): BasicFieldDefinition = binaryField(name)

    @deprecated("use booleanField(name)", "5.2.11")
    def withType(ft: BooleanType.type): BasicFieldDefinition = booleanField(name)

    @deprecated("use byteField(name)", "5.2.11")
    def withType(ft: ByteType.type): BasicFieldDefinition = byteField(name)

    @deprecated("use completionField(name)", "5.2.11")
    def withType(ft: CompletionType.type): CompletionFieldDefinition = completionField(name)

    @deprecated("use dateField(name)", "5.2.11")
    def withType(ft: DateType.type): BasicFieldDefinition = dateField(name)

    @deprecated("use doubleField(name)", "5.2.11")
    def withType(ft: DoubleType.type): BasicFieldDefinition = doubleField(name)

    @deprecated("use floatField(name)", "5.2.11")
    def withType(ft: FloatType.type): BasicFieldDefinition = floatField(name)

    @deprecated("use geopointField(name)", "5.2.11")
    def withType(ft: GeoPointType.type): BasicFieldDefinition = geopointField(name)

    @deprecated("use byteField(name)", "5.2.11")
    def withType(ft: GeoShapeType.type): GeoshapeFieldDefinition = geoshapeField(name)

    @deprecated("use byteField(name)", "5.2.11")
    def withType(ft: IntegerType.type): BasicFieldDefinition = intField(name)

    @deprecated("use ipField(name)", "5.2.11")
    def withType(ft: IpType.type): BasicFieldDefinition = ipField(name)

    @deprecated("use byteField(name)", "5.2.11")
    def withType(ft: LongType.type): BasicFieldDefinition = longField(name)

    @deprecated("use byteField(name)", "5.2.11")
    def withType(ft: NestedType.type): NestedFieldDefinition = nestedField(name)

    @deprecated("use byteField(name)", "5.2.11")
    def withType(ft: ObjectType.type): BasicFieldDefinition = objectField(name)

    @deprecated("use byteField(name)", "5.2.11")
    def withType(ft: ShortType.type): BasicFieldDefinition = shortField(name)

    @deprecated("string type is deprecated in ES 5, use text or keyword types", "5.0.0")
    def withType(ft: StringType.type): BasicFieldDefinition = stringField(name)

    @deprecated("use textField(name)", "5.2.11")
    def withType(ft: TextType.type): TextFieldDefinition = textField(name)

    @deprecated("use tokenCountField(name)", "5.2.11")
    def withType(ft: TokenCountType.type): BasicFieldDefinition = tokenCountField(name)

    @deprecated("use binaryField(name)", "5.2.11")
    def typed(ft: BinaryType.type): BasicFieldDefinition = binaryField(name)

    @deprecated("use boolean(name)", "5.2.11")
    def typed(ft: BooleanType.type): BasicFieldDefinition = booleanField(name)

    @deprecated("use byteField(name)", "5.2.11")
    def typed(ft: ByteType.type): BasicFieldDefinition = byteField(name)

    @deprecated("use completionField(name)", "5.2.11")
    def typed(ft: CompletionType.type): CompletionFieldDefinition = completionField(name)

    @deprecated("use dateField(name)", "5.2.11")
    def typed(ft: DateType.type): BasicFieldDefinition = dateField(name)

    @deprecated("use doubleField(name)", "5.2.11")
    def typed(ft: DoubleType.type): BasicFieldDefinition = doubleField(name)

    @deprecated("use floatField(name)", "5.2.11")
    def typed(ft: FloatType.type): BasicFieldDefinition = floatField(name)

    @deprecated("use geopointField(name)", "5.2.11")
    def typed(ft: GeoPointType.type): BasicFieldDefinition = geopointField(name)

    @deprecated("use geoshapeField(name)", "5.2.11")
    def typed(ft: GeoShapeType.type): GeoshapeFieldDefinition = geoshapeField(name)

    @deprecated("use intField(name)", "5.2.11")
    def typed(ft: IntegerType.type): BasicFieldDefinition = intField(name)

    @deprecated("use ipField(name)", "5.2.11")
    def typed(ft: IpType.type): BasicFieldDefinition = ipField(name)

    @deprecated("use keywordField(name)", "5.2.11")
    def typed(ft: KeywordType.type): KeywordFieldDefinition = keywordField(name)

    @deprecated("use longField(name)", "5.2.11")
    def typed(ft: LongType.type): BasicFieldDefinition = longField(name)

    @deprecated("use nestedField(name)", "5.2.11")
    def typed(ft: NestedType.type): NestedFieldDefinition = nestedField(name)

    @deprecated("use objectField(name)", "5.2.11")
    def typed(ft: ObjectType.type): BasicFieldDefinition = objectField(name)

    @deprecated("use shortField(name)", "5.2.11")
    def typed(ft: ShortType.type): BasicFieldDefinition = shortField(name)

    @deprecated("string type is deprecated in ES 5, use text or keyword types", "5.0.0")
    def typed(ft: StringType.type): BasicFieldDefinition = stringField(name)

    @deprecated("use textField(name)", "5.2.11")
    def typed(ft: TextType.type): TextFieldDefinition = textField(name)

    @deprecated("use tokenCountField(name)", "5.2.11")
    def typed(ft: TokenCountType.type): BasicFieldDefinition = tokenCountField(name)
  }

  @deprecated("string type is deprecated in ES 5, use text or keyword types", "5.0.0")
  def field(name: String, ft: StringType.type): BasicFieldDefinition = stringField(name)
}
