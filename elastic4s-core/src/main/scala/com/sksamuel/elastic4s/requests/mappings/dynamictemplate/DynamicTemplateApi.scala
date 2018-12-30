package com.sksamuel.elastic4s.requests.mappings.dynamictemplate

import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.requests.mappings.FieldType._
import com.sksamuel.elastic4s.requests.mappings._
import com.sksamuel.elastic4s.requests.script.ScriptField

trait DynamicTemplateApi {
  self: ElasticApi =>

  def dynamicTemplate(name: String) = new DynamicTemplateExpectsMapping(name)
  class DynamicTemplateExpectsMapping(name: String) {
    def mapping(fielddef: FieldDefinition): DynamicTemplateRequest = DynamicTemplateRequest(name, fielddef)
  }

  def dynamicTemplate(nameOfTemplate: String, mapping: FieldDefinition): DynamicTemplateRequest =
    DynamicTemplateRequest(nameOfTemplate, mapping)

  def dynamicType(): BasicField                       = BasicField("", "{dynamic_type}")
  def dynamicBinaryField(): BasicField                = BasicField("", "binary")
  def dynamicBooleanField(): BasicField               = BasicField("", "boolean")
  def dynamicByteField(): BasicField                  = BasicField("", "byte")
  def dynamicCompletionField(): CompletionField       = CompletionField("")
  def dynamicDateField(): BasicField                  = BasicField("", "date")
  def dynamicDoubleField(): BasicField                = BasicField("", "double")
  def dynamicFloatField(): BasicField                 = BasicField("", "float")
  def dynamicHalfFloatField(): BasicField             = BasicField("", "half_float")
  def dynamicScaledFloatField(): BasicField           = BasicField("", "scaled_float")
  def dynamicGeopointField(): BasicField              = BasicField("", "geo_point")
  def dynamicGeoshapeField(): GeoshapeField           = GeoshapeField("")
  def dynamicIntField(): BasicField                   = BasicField("", "integer")
  def dynamicIpField(): BasicField                    = BasicField("", "ip")
  def dynamicKeywordField(): KeywordField             = KeywordField("")
  def dynamicLongField(): BasicField                  = BasicField("", "long")
  def dynamicNestedField(): NestedField               = NestedField("")
  def dynamicObjectField(): ObjectField               = ObjectField("")
  def dynamicPercolatorField(): BasicField            = BasicField("", "percolator")
  def dynamicScriptField(script: String): ScriptField = ScriptField("", script)
  def dynamicShortField(): BasicField                 = BasicField("", "short")
  def dynamicTextField(): TextField                   = TextField("")
  def dynamicTokenCountField(): BasicField            = BasicField("", "token_count")

  @deprecated("use dynamicIntField(), dynamicTextField() and so on", "5.2.12")
  def dynamicTemplateMapping(fieldType: FieldType): FieldDefinition = fieldType match {
    case BinaryType     => binaryField("")
    case BooleanType    => booleanField("")
    case ByteType       => byteField("")
    case CompletionType => completionField("")
    case DateType       => dateField("")
    case DoubleType     => doubleField("")
    case FloatType      => floatField("")
    case IntegerType    => intField("")
    case IpType         => ipField("")
    case GeoPointType   => geopointField("")
    case GeoShapeType   => geoshapeField("")
    case LongType       => longField("")
    case NestedType     => nestedField("")
    case ObjectType     => objectField("")
    case ShortType      => shortField("")
    case TextType       => textField("")
    case TokenCountType => tokenCountField("")
  }
}
