package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.{AggregateMetricField, AliasField, AnnotatedTextField, BinaryField, BooleanField, CompletionField, ConstantKeywordField, DateField, DateNanosField, DenseVectorField, DynamicField, ElasticField, FlattenedField, GeoPointField, GeoShapeField, HistogramField, IcuCollationKeywordField, IpField, IpRangeField, JoinField, KeywordField, MatchOnlyTextField, Murmur3Field, NestedField, NumberField, ObjectField, PercolatorField, RangeField, RankFeatureField, RankFeaturesField, SearchAsYouTypeField, SparseVectorField, TextField, TokenCountField, VersionField, WildcardField}
import com.sksamuel.elastic4s.json.XContentBuilder

object ElasticFieldBuilderFn {

  def apply(field: ElasticField): XContentBuilder = {
    field match {
      case f: AggregateMetricField => AggregateMetricFieldBuilderFn.build(f)
      case f: AliasField => AliasFieldBuilderFn.build(f)
      case f: AnnotatedTextField => AnnotatedTextFieldBuilderFn.build(f)
      case f: BinaryField => BinaryFieldBuilderFn.build(f)
      case f: BooleanField => BooleanFieldBuilderFn.build(f)
      case f: ConstantKeywordField => ConstantKeywordFieldBuilderFn.build(f)
      case f: CompletionField => CompletionFieldBuilderFn.build(f)
      case f: DateField => DateFieldBuilderFn.build(f)
      case f: DateNanosField => DateNanosFieldBuilderFn.build(f)
      case f: DenseVectorField => DenseVectorFieldBuilderFn.build(f)
      case f: DynamicField => DynamicFieldBuilderFn.build(f)
      case f: FlattenedField => FlattenedFieldBuilderFn.build(f)
      case f: GeoPointField => GeoPointFieldBuilderFn.build(f)
      case f: GeoShapeField => GeoShapeFieldBuilderFn.build(f)
      case f: HistogramField => HistogramFieldBuilderFn.build(f)
      case f: IcuCollationKeywordField => IcuCollationKeywordFieldBuilderFn.build(f)
      case f: IpField => IpFieldBuilderFn.build(f)
      case f: IpRangeField => IpRangeFieldBuilderFn.build(f)
      case f: JoinField => JoinFieldBuilderFn.build(f)
      case f: KeywordField => KeywordFieldBuilderFn.build(f)
      case f: MatchOnlyTextField => MatchOnlyTextFieldBuilderFn.build(f)
      case f: Murmur3Field => Murmur3FieldBuilderFn.build(f)
      case f: NestedField => NestedFieldBuilderFn.build(f)
      case f: NumberField[_] => NumberFieldBuilderFn.build(f)
      case f: ObjectField => ObjectFieldBuilderFn.build(f)
      case f: PercolatorField => PercolatorFieldBuilderFn.build(f)
      case f: RangeField => RangeFieldBuilderFn.build(f)
      case f: RankFeatureField => RankFeatureFieldBuilderFn.build(f)
      case f: RankFeaturesField => RankFeaturesFieldBuilderFn.build(f)
      case f: SearchAsYouTypeField => SearchAsYouTypeFieldBuilderFn.build(f)
      case f: SparseVectorField => SparseVectorFieldBuilderFn.build(f)
      case f: TextField => TextFieldBuilderFn.build(f)
      case f: TokenCountField => TokenCountFieldBuilderFn.build(f)
      case f: VersionField => VersionFieldBuilderFn.build(f)
      case f: WildcardField => WildcardFieldBuilderFn.build(f)
    }
  }

  def construct(name: String, values: Map[String, Any]): ElasticField = {
    values.get("type").collect {
      case AggregateMetricField.`type` => AggregateMetricFieldBuilderFn.toField(name, values)
      case AliasField.`type` => AliasFieldBuilderFn.toField(name, values)
      case AnnotatedTextField.`type` => AnnotatedTextFieldBuilderFn.toField(name, values)
      case BinaryField.`type` => BinaryFieldBuilderFn.toField(name, values)
      case BooleanField.`type` => BooleanFieldBuilderFn.toField(name, values)
      case ConstantKeywordField.`type` => ConstantKeywordFieldBuilderFn.toField(name, values) // *
      case CompletionField.`type` => CompletionFieldBuilderFn.toField(name, values)
      case DateField.`type` => DateFieldBuilderFn.toField(name, values)
      case DateNanosField.`type` => DateNanosFieldBuilderFn.toField(name, values)
      case DenseVectorField.`type` => DenseVectorFieldBuilderFn.toField(name, values)
      case FlattenedField.`type` => FlattenedFieldBuilderFn.toField(name, values)
      case GeoPointField.`type` => GeoPointFieldBuilderFn.toField(name, values)
      case GeoShapeField.`type` => GeoShapeFieldBuilderFn.toField(name, values)
      case HistogramField.`type` => HistogramFieldBuilderFn.toField(name, values)
      case IpField.`type` => IpFieldBuilderFn.toField(name, values)
      case IpRangeField.`type` => IpRangeFieldBuilderFn.toField(name, values)
      case JoinField.`type` => JoinFieldBuilderFn.toField(name, values)
      case KeywordField.`type` => KeywordFieldBuilderFn.toField(name, values)
      case MatchOnlyTextField.`type` => MatchOnlyTextFieldBuilderFn.toField(name, values)
      case Murmur3Field.`type` => Murmur3FieldBuilderFn.toField(name, values)
      case NestedField.`type` => NestedFieldBuilderFn.toField(name, values)
      case ObjectField.`type` => ObjectFieldBuilderFn.toField(name, values)
      case PercolatorField.`type` => PercolatorFieldBuilderFn.toField(name, values)
      case RankFeatureField.`type` => RankFeatureFieldBuilderFn.toField(name, values)
      case RankFeaturesField.`type` => RankFeaturesFieldBuilderFn.toField(name, values)
      case SearchAsYouTypeField.`type` => SearchAsYouTypeFieldBuilderFn.toField(name, values)
      case SparseVectorField.`type` => SparseVectorFieldBuilderFn.toField(name, values)
      case TextField.`type` => TextFieldBuilderFn.toField(name, values)
      case TokenCountField.`type` => TokenCountFieldBuilderFn.toField(name, values)
      case VersionField.`type` => VersionFieldBuilderFn.toField(name, values)
      case WildcardField.`type` => WildcardFieldBuilderFn.toField(name, values)
      case rangeType: String if RangeFieldBuilderFn.supportedTypes.contains(rangeType) => RangeFieldBuilderFn.toField(rangeType, name, values)
      case numberType: String if NumberFieldBuilderFn.supportedTypes.contains(numberType) => NumberFieldBuilderFn.toField(numberType, name, values)
    }.orElse(values.get("properties").map(_ => ObjectFieldBuilderFn.toField(name, values))).getOrElse(throw new RuntimeException(s"Could not convert mapping for '$name' to an ElasticField"))
  }
}
