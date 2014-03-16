package com.sksamuel.elastic4s

/** @author Stephen Samuel */
object DefinitionAttributes {

  trait DefinitionAttributeBoost {
    val _builder: { def boost(boost: Float): Any }

    def boost(boost: Double): this.type = {
      _builder.boost(boost.toFloat)
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

  trait DefinitionAttributeFilterName {
    val _builder: { def filterName(filterName: String): Any }

    def filterName(filterName: String): this.type = {
      _builder.filterName(filterName)
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
}