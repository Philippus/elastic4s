package com.sksamuel.elastic4s.handlers.fields

import com.sksamuel.elastic4s.fields.{AggregateMetricField, AliasField, AnnotatedTextField, BinaryField, BooleanField, CompletionField, ConstantKeywordField, DateField, DateNanosField, DenseVectorField, DynamicField, ElasticField, FlattenedField, GeoPointField, GeoShapeField, HistogramField, IpField, IpRangeField, JoinField, KeywordField, Murmur3Field, NestedField, NumberField, ObjectField, PercolatorField, RangeField, RankFeatureField, RankFeaturesField, SearchAsYouTypeField, TextField, TokenCountField, VersionField, WildcardField}
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
      case f: IpField => IpFieldBuilderFn.build(f)
      case f: IpRangeField => IpRangeFieldBuilderFn.build(f)
      case f: JoinField => JoinFieldBuilderFn.build(f)
      case f: KeywordField => KeywordFieldBuilderFn.build(f)
      case f: Murmur3Field => Murmur3FieldBuilderFn.build(f)
      case f: NestedField => NestedFieldBuilderFn.build(f)
      case f: NumberField[_] => NumberFieldBuilderFn.build(f)
      case f: ObjectField => ObjectFieldBuilderFn.build(f)
      case f: PercolatorField => PercolatorFieldBuilderFn.build(f)
      case f: RangeField => RangeFieldBuilderFn.build(f)
      case f: RankFeatureField => RankFeatureFieldBuilderFn.build(f)
      case f: RankFeaturesField => RankFeaturesFieldBuilderFn.build(f)
      case f: SearchAsYouTypeField => SearchAsYouTypeFieldBuilderFn.build(f)
      case f: TextField => TextFieldBuilderFn.build(f)
      case f: TokenCountField => TokenCountFieldBuilderFn.build(f)
      case f: VersionField => VersionFieldBuilderFn.build(f)
      case f: WildcardField => WildcardFieldBuilderFn.build(f)
    }
  }
}
