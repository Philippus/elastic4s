package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.mappings.FieldType._

trait DynamicTemplateDsl {
  self: ElasticDsl =>

  def dynamicTemplate(nameOfTemplate: String) = new {
    def mapping(mapping: TypedFieldDefinition) = DynamicTemplateDefinition(nameOfTemplate, mapping)
  }

  def dynamicTemplate(nameOfTemplate: String, mapping: TypedFieldDefinition): DynamicTemplateDefinition = {
    DynamicTemplateDefinition(nameOfTemplate, mapping)
  }

  def dynamicTemplateMapping(fieldType: FieldType) = fieldType match {
    case AttachmentType => attachmentField("")
    case BinaryType => binaryField("")
    case BooleanType => booleanField("")
    case ByteType => byteField("")
    case CompletionType => completionField("")
    case DateType => dateField("")
    case DoubleType => doubleField("")
    case FloatType => floatField("")
    case IntegerType => intField("")
    case IpType => ipField("")
    case GeoPointType => geopointField("")
    case GeoShapeType => geoshapeField("")
    case LongType => longField("")
    case MultiFieldType => multiField("")
    case NestedType => nestedField("")
    case ObjectType => objectField("")
    case ShortType => shortField("")
    case StringType => stringField("")
    case TokenCountType => tokenCountField("")
  }
}
