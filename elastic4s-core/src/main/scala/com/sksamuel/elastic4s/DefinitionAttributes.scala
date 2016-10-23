package com.sksamuel.elastic4s

import org.elasticsearch.action.support.IndicesOptions
import org.elasticsearch.common.unit.TimeValue

import scala.language.reflectiveCalls

/** @author Stephen Samuel */
object DefinitionAttributes {

  trait DefinitionAttributeIgnoreConflicts {
    val _builder: { def setIgnoreConflicts(ignoreConflicts: Boolean): Any }

    def ignoreConflicts(ignore: Boolean): this.type = {
      _builder.setIgnoreConflicts(ignore)
      this
    }
  }

  trait DefinitionAttributeIndexesOptions {
    val _builder: { def setIndicesOptions(options: IndicesOptions): Any }

    def indexesOptions(options: IndicesOptions): this.type = {
      _builder.setIndicesOptions(options)
      this
    }
  }

  trait DefinitionAttributeBoost {
    val _builder: { def boost(boost: Float): Any }

    def boost(boost: Double): this.type = {
      _builder.boost(boost.toFloat)
      this
    }
  }

  trait DefinitionAttributeBoostMode {
    val _builder: { def boostMode(mode: String): Any }

    def boostMode(mode: String): this.type = {
      _builder.boostMode(mode)
      this
    }
  }

  trait DefinitionAttributeScoreMode {
    val _builder: { def scoreMode(scoreMode: String): Any }

    def scoreMode(scoreMode: String): this.type = {
      _builder.scoreMode(scoreMode)
      this
    }
  }

  trait DefinitionAttributeMinScore {
    val _builder: { def setMinScore(minScore: Float): Any }

    def minScore(min: Double): this.type = {
      _builder.setMinScore(min.toFloat)
      this
    }
  }

  trait DefinitionAttributeMaxBoost {
    val _builder: { def maxBoost(maxBoost: Float): Any }

    def maxBoost(max: Double): this.type = {
      _builder.maxBoost(max.toFloat)
      this
    }
  }

  trait DefinitionAttributeFuzziness {
    val _builder: { def fuzziness(a: AnyRef): Any }

    def fuzziness(a: Any): this.type = {
      _builder.fuzziness(a.toString)
      this
    }
  }

  trait DefinitionAttributeCutoffFrequency {
    val _builder: { def cutoffFrequency(cutoffFrequency: Float): Any }
    def cutoffFrequency(cutoffFrequency: Double): this.type = {
      _builder.cutoffFrequency(cutoffFrequency.toFloat)
      this
    }
  }

  trait DefinitionAttributeFuzzyRewrite {
    val _builder: { def fuzzyRewrite(a: String): Any }

    def fuzzyRewrite(a: String): this.type = {
      _builder.fuzzyRewrite(a)
      this
    }
  }

  trait DefinitionAttributeFrom {
    val _builder: { def from(a: AnyRef): Any }

    def from(from: Any): this.type = {
      _builder.from(from.toString)
      this
    }
  }

  trait DefinitionAttributeTo {
    val _builder: { def to(a: AnyRef): Any }

    def to(to: Any): this.type = {
      _builder.to(to.toString)
      this
    }
  }

  trait DefinitionAttributeLt {
    val _builder: { def lt(a: AnyRef): Any }

    def lt(lt: Any): this.type = {
      _builder.lt(lt.toString)
      this
    }
  }

  trait DefinitionAttributeGt {
    val _builder: { def gt(a: AnyRef): Any }

    def gt(gt: Any): this.type = {
      _builder.gt(gt.toString)
      this
    }
  }

  trait DefinitionAttributePrefixLength {
    val _builder: { def prefixLength(f: Int): Any }

    def prefixLength(a: Int): this.type = {
      _builder.prefixLength(a)
      this
    }
  }

  trait DefinitionAttributeRewrite {
    val _builder: { def rewrite(f: String): Any }

    def rewrite(a: String): this.type = {
      _builder.rewrite(a)
      this
    }
  }

  trait DefinitionAttributePreference {
    val _builder: { def setPreference(preference: String): Any }

    def preference(preference: String): this.type = {
      _builder.setPreference(preference)
      this
    }
    def preference(pref: Preference): this.type = preference(pref.elastic)
  }

  trait DefinitionAttributeLon {
    val _builder: { def lon(l: Double): Any }

    def lon(l: Double): this.type = {
      _builder.lon(l)
      this
    }
  }

  trait DefinitionAttributeLat {
    val _builder: { def lat(l: Double): Any }

    def lat(lat: Double): this.type = {
      _builder.lat(lat)
      this
    }
  }

  trait DefinitionAttributeCache {
    val _builder: { def cache(b: Boolean): Any }

    def cache(b: Boolean): this.type = {
      _builder.cache(b)
      this
    }
  }

  trait DefinitionAttributeCacheKey {
    val _builder: { def cacheKey(cacheKey: String): Any }

    def cacheKey(cacheKey: String): this.type = {
      _builder.cacheKey(cacheKey)
      this
    }
  }

  trait DefinitionAttributePoint {
    val _builder: { def point(lat: Double, lon: Double): Any }

    def point(lat: Double, lon: Double): this.type = {
      _builder.point(lat, lon)
      this
    }
  }

  trait DefinitionAttributeRouting {
    val _builder: { def setRouting(preference: String): Any }

    def routing(routing: String): this.type = {
      _builder.setRouting(routing)
      this
    }
  }

  trait DefinitionAttributeRefresh {
    val _builder: { def setRefresh(refresh: Boolean): Any }

    def refresh(refresh: Boolean): this.type = {
      _builder.setRefresh(refresh)
      this
    }
  }

  trait DefinitionAttributeRealtime {
    val _builder: { def setRealtime(realtime: Boolean): Any }

    def realtime(realtime: Boolean): this.type = {
      _builder.setRealtime(realtime)
      this
    }
  }

  trait DefinitionAttributeTimeout {
    val _builder: { def setTimeout(value: String): Any; def setTimeout(value: TimeValue): Any }

    def timeout(value: TimeValue): this.type = {
      _builder.setTimeout(value)
      this
    }

    def timeout(value: String): this.type = {
      _builder.setTimeout(value)
      this
    }
  }
}