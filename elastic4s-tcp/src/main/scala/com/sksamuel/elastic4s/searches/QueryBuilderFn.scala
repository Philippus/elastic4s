package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.ScriptBuilder
import com.sksamuel.elastic4s.searches.queries._
import org.elasticsearch.index.query.{BoolQueryBuilder, DisMaxQueryBuilder, MatchPhrasePrefixQueryBuilder, Operator, QueryBuilder, QueryBuilders, RangeQueryBuilder, RegexpQueryBuilder, ScriptQueryBuilder, SimpleQueryStringBuilder, TermsQueryBuilder}

object QueryBuilderFn {
  def apply(query: QueryDefinition): QueryBuilder = query match {
    case q: QueryStringQueryDefinition => QueryStringBuilder.builder(q)
    case q: MatchAllQueryDefinition => MatchAllQueryBuilder(q)
    case q: MatchQueryDefinition => MatchQueryBuilder(q)
    case q: IdQueryDefinition => IdQueryBuilder(q)
    case q: TermQueryDefinition => TermQueryBuilder(q)
    case q: PrefixQueryDefinition => PrefixQueryBuilderFn(q)
    case q: WildcardQueryDefinition => WildcardQueryBuilder(q)
    case q: ExistsQueryDefinition => ExistsQueryBuilder(q)
    case q: MatchPhraseDefinition => MatchPhraseBuilder(q)
    case q: BoostingQueryDefinition => BoostingQueryBuilder(q)
    case q: FuzzyQueryDefinition => FuzzyQueryBuilder(q)
    case q: HasChildQueryDefinition => HasChildQueryBuilder(q)
    case q: HasParentQueryDefinition => HasParentQueryBuilder(q)
    case q: RegexQueryDefinition => RegexQueryBuilder(q)
    case q: RangeQueryDefinition => RangeQueryBuilder(q)
    case q: SimpleStringQueryDefinition => SimpleStringQueryBuilder(q)
    case q: GeoPolygonQueryDefinition => GeoPolygonQueryBuilder(q)
    case q: TermsQueryDefinition[_] => TermsQueryBuilder(q)
    case q: DisMaxDefinition => DisMaxBuilder(q)
    case q: ScriptQueryDefinition => ScriptQueryBuilder(q)
    case q: BoolQueryDefinition => BoolQueryBuilder(q)
    case q: MatchPhrasePrefixDefinition => MatchPhrasePrefixBuilder(q)
    case q: TypeQueryDefinition => QueryBuilders.typeQuery(q.`type`)
  }
}

object MatchPhrasePrefixBuilder {
  def apply(q: MatchPhrasePrefixDefinition): MatchPhrasePrefixQueryBuilder = {
    val _builder = QueryBuilders.matchPhrasePrefixQuery(q.field, q.value.toString)
    q.queryName.foreach(_builder.queryName)
    q.boost.map(_.toFloat).foreach(_builder.boost)
    q.analyzer.foreach(_builder.analyzer)
    q.maxExpansions.foreach(_builder.maxExpansions)
    q.queryName.foreach(_builder.queryName)
    q.slop.foreach(_builder.slop)
    _builder
  }
}

object BoolQueryBuilder {
  def apply(q: BoolQueryDefinition): BoolQueryBuilder = {
    val builder = QueryBuilders.boolQuery()
    q.adjustPureNegative.foreach(builder.adjustPureNegative)
    q.minimumShouldMatch.foreach(builder.minimumNumberShouldMatch)
    q.disableCoord.foreach(builder.disableCoord)
    q.queryName.foreach(builder.queryName)
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.must.map(QueryBuilderFn.apply).foreach(builder.must)
    q.filters.map(QueryBuilderFn.apply).foreach(builder.filter)
    q.not.map(QueryBuilderFn.apply).foreach(builder.mustNot)
    q.should.map(QueryBuilderFn.apply).foreach(builder.should)
    builder
  }
}

object ScriptQueryBuilder {
  def apply(q: ScriptQueryDefinition): ScriptQueryBuilder = {
    val builder = QueryBuilders.scriptQuery(ScriptBuilder(q.script))
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.queryName.foreach(builder.queryName)
    builder
  }
}

object DisMaxBuilder {
  def apply(q: DisMaxDefinition): DisMaxQueryBuilder = {
    val builder = QueryBuilders.disMaxQuery()
    q.queries.foreach(q => builder.add(QueryBuilderFn(q)))
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.tieBreaker.map(_.toFloat).foreach(builder.tieBreaker)
    q.queryName.foreach(builder.queryName)
    builder
  }
}

trait BuildableTermsQueryImplicits {

  implicit object IntBuildableTermsQuery extends BuildableTermsQuery[Int] {
    override def build(q: TermsQueryDefinition[Int]): TermsQueryBuilder =
      QueryBuilders.termsQuery(q.field, q.values.toSeq: _*)
  }

  implicit object LongBuildableTermsQuery extends BuildableTermsQuery[Long] {
    override def build(q: TermsQueryDefinition[Long]): TermsQueryBuilder =
      QueryBuilders.termsQuery(q.field, q.values.toSeq: _*)
  }

  implicit object FloatBuildableTermsQuery extends BuildableTermsQuery[Float] {
    override def build(q: TermsQueryDefinition[Float]): TermsQueryBuilder =
      QueryBuilders.termsQuery(q.field, q.values.toSeq: _*)
  }

  implicit object DoubleBuildableTermsQuery extends BuildableTermsQuery[Double] {
    override def build(q: TermsQueryDefinition[Double]): TermsQueryBuilder =
      QueryBuilders.termsQuery(q.field, q.values.toSeq: _*)
  }

  implicit object StringBuildableTermsQuery extends BuildableTermsQuery[String] {
    override def build(q: TermsQueryDefinition[String]): TermsQueryBuilder =
      QueryBuilders.termsQuery(q.field, q.values.toSeq: _*)
  }

  implicit object AnyRefBuildableTermsQuery extends BuildableTermsQuery[AnyRef] {
    override def build(q: TermsQueryDefinition[AnyRef]): TermsQueryBuilder =
      QueryBuilders.termsQuery(q.field, q.values.map(_.toString).toSeq: _*)
  }
}

object TermsQueryBuilder {
  def apply[T](q: TermsQueryDefinition[T]): TermsQueryBuilder = {
    val builder = q.buildable.build(q).asInstanceOf[TermsQueryBuilder]
    q.queryName.foreach(builder.queryName)
    q.boost.map(_.toFloat).foreach(builder.boost)
    builder
  }
}

object SimpleStringQueryBuilder {
  def apply(q: SimpleStringQueryDefinition): SimpleQueryStringBuilder = {
    val builder = QueryBuilders.simpleQueryStringQuery(q.query)
    q.queryName.foreach(builder.queryName)
    q.analyzer.foreach(builder.analyzer)
    q.analyzeWildcard.foreach(builder.analyzeWildcard)
    q.fields.foreach {
      case (name, -1D) => builder.field(name)
      case (name, boost) => builder.field(name, boost.toFloat)
    }
    q.lenient.foreach(builder.lenient)
    q.minimumShouldMatch.map(_.toString).foreach(builder.minimumShouldMatch)
    q.operator.map(Operator.fromString).foreach(builder.defaultOperator)
    builder
  }
}

object RangeQueryBuilder {
  def apply(q: RangeQueryDefinition): RangeQueryBuilder = {
    val builder = QueryBuilders.rangeQuery(q.field)
    q.queryName.foreach(builder.queryName)
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.from.foreach(builder.from)
    q.to.foreach(builder.to)
    q.gte.foreach(builder.gte)
    q.lte.foreach(builder.lte)
    q.includeLower.foreach(builder.includeLower)
    q.includeUpper.foreach(builder.includeUpper)
    q.timeZone.foreach(builder.timeZone)
    builder
  }
}

object RegexQueryBuilder {
  def apply(q: RegexQueryDefinition): RegexpQueryBuilder = {
    val builder = QueryBuilders.regexpQuery(q.field, q.regex)
    if (q.flags.nonEmpty)
      builder.flags(q.flags.map(org.elasticsearch.index.query.RegexpFlag.valueOf): _*)
    q.queryName.foreach(builder.queryName)
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.rewrite.foreach(builder.rewrite)
    builder
  }
}












