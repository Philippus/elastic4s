package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.script.ScriptBuilderFn

object IntervalsFilterBuilderFn {
  def apply(f: IntervalsFilter): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    f.after.foreach{ r => builder.rawField("after", IntervalsRuleBuilderFn(r)) }
    f.before.foreach{ r => builder.rawField("before", IntervalsRuleBuilderFn(r)) }
    f.containedBy.foreach{ r => builder.rawField("contained_by", IntervalsRuleBuilderFn(r)) }
    f.containing.foreach{ r => builder.rawField("containing", IntervalsRuleBuilderFn(r)) }
    f.notContainedBy.foreach{ r => builder.rawField("not_contained_by", IntervalsRuleBuilderFn(r)) }
    f.notContaining.foreach{ r => builder.rawField("not_containing", IntervalsRuleBuilderFn(r)) }
    f.notOverlapping.foreach{ r => builder.rawField("not_overlapping", IntervalsRuleBuilderFn(r)) }
    f.overlapping.foreach{ r => builder.rawField("overlapping", IntervalsRuleBuilderFn(r)) }
    f.script.foreach{ s => builder.rawField("script", ScriptBuilderFn(s)) }
    builder
  }
}

object IntervalsRuleBuilderFn {
  def apply(r: IntervalsRule): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    r match {
      case Match(query: String, maxGaps: Option[Int], ordered: Option[Boolean], analyzer: Option[String], filter: Option[IntervalsFilter], useField: Option[String]) =>
        builder.startObject("match")
        builder.field("query", query)
        maxGaps.foreach(builder.field("max_gaps", _))
        ordered.foreach(builder.field("ordered", _))
        analyzer.foreach(builder.field("analyzer", _))
        filter.foreach{ f => builder.rawField("filter", IntervalsFilterBuilderFn(f)) }
        useField.foreach(builder.field("use_field", _))
        builder.endObject()
      case Prefix(prefix: String, analyzer: Option[String], useField: Option[String]) =>
        builder.startObject("prefix")
        builder.field("prefix", prefix)
        analyzer.foreach(builder.field("analyzer", _))
        useField.foreach(builder.field("use_field", _))
        builder.endObject()
      case Wildcard(pattern: String, analyzer: Option[String], useField: Option[String]) =>
        builder.startObject("wildcard")
        builder.field("pattern", pattern)
        analyzer.foreach(builder.field("analyzer", _))
        useField.foreach(builder.field("use_field", _))
        builder.endObject()
      case Fuzzy(term: String, prefixLength: Option[String], transpositions: Option[Boolean], fuzziness: Option[String], analyzer: Option[String], useField: Option[String]) =>
        builder.startObject("fuzzy")
        builder.field("term", term)
        prefixLength.foreach(builder.field("prefix_length", _))
        transpositions.foreach(builder.field("transpositions", _))
        fuzziness.foreach(builder.field("fuzziness", _))
        analyzer.foreach(builder.field("analyzer", _))
        useField.foreach(builder.field("use_field", _))
        builder.endObject()
      case AllOf(intervals: List[IntervalsRule], maxGaps: Option[Int], ordered: Option[Boolean], filter: Option[IntervalsFilter]) =>
        builder.startObject("all_of")

        if (intervals.nonEmpty) {
          builder.startArray("intervals")
          intervals.foreach{ r => builder.rawValue(IntervalsRuleBuilderFn(r)) }
          builder.endArray()
        }

        maxGaps.foreach(builder.field("max_gaps", _))
        ordered.foreach(builder.field("ordered", _))

        filter.foreach{ f => builder.rawField("filter", IntervalsFilterBuilderFn(f)) }
        builder.endObject()
      case AnyOf(intervals: List[IntervalsRule], filter: Option[IntervalsFilter]) =>
        builder.startObject("any_of")

        if (intervals.nonEmpty) {
          builder.startArray("intervals")
          intervals.foreach{ r => builder.rawValue(IntervalsRuleBuilderFn(r)) }
          builder.endArray()
        }

        filter.foreach{ f => builder.rawField("filter", IntervalsFilterBuilderFn(f)) }

        builder.endObject()
    }
    builder
  }
}

object IntervalsQueryBuilderFn {
  def apply(q: IntervalsQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("intervals")
    builder.rawField(q.field, IntervalsRuleBuilderFn(q.rule))
    builder.endObject()
  }
}
