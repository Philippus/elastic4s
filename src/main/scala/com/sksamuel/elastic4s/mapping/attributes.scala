package com.sksamuel.elastic4s.mapping

import org.elasticsearch.common.xcontent.XContentBuilder
import com.sksamuel.elastic4s.Analyzer

/** @author Fehmi Can Saglam */
object attributes {

  sealed trait Attribute { self: TypedFieldDefinition =>

    type Self <: TypedFieldDefinition

    protected def insert(source: XContentBuilder): Unit
  }

  trait AttributeIndexName extends Attribute { self: TypedFieldDefinition =>

    private[this] var _indexName: Option[String] = None

    def indexName(indexName: String): Self = {
      _indexName = Some(indexName)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _indexName.foreach(source.field("index_name", _))
    }
  }

  trait AttributeEnabled extends Attribute { self: TypedFieldDefinition =>

    private[this] var _enabled: Option[Boolean] = None

    def enabled(enabled: Boolean): Self = {
      _enabled = Some(enabled)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _enabled.foreach(source.field("enabled", _))
    }
  }

  trait AttributeStore extends Attribute { self: TypedFieldDefinition =>

    private[this] var _store: Option[String] = None

    def store(store: YesNo): Self = {
      _store = Some(store.value)
      this.asInstanceOf[Self]
    }

    def store(param: Boolean): Self = {
      _store = Some(YesNo(param).value)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _store.foreach(source.field("store", _))
    }
  }

  trait AttributeIndex extends Attribute { self: TypedFieldDefinition =>

    private[this] var _index: Option[String] = None

    def index(index: String): Self = {
      _index = Some(index)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _index.foreach(source.field("index", _))
    }
  }

  trait AttributePrecisionStep extends Attribute { self: TypedFieldDefinition =>

    private[this] var _precisionStep: Option[Int] = None

    def precisionStep(precisionStep: Int): Self = {
      _precisionStep = Some(precisionStep)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _precisionStep.foreach(source.field("precision_step", _))
    }
  }

  trait AttributeBoost extends Attribute { self: TypedFieldDefinition =>

    private[this] var _boost: Option[Double] = None

    def boost(boost: Double): Self = {
      _boost = Some(boost)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _boost.foreach(source.field("boost", _))
    }
  }

  trait AttributeNullValue[T] extends Attribute { self: TypedFieldDefinition =>

    private[this] var _nullValue: Option[T] = None

    def nullValue(nullValue: T): Self = {
      _nullValue = Some(nullValue)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _nullValue.foreach(source.field("null_value", _))
    }
  }

  trait AttributeIncludeInAll extends Attribute { self: TypedFieldDefinition =>

    private[this] var _includeInAll: Option[Boolean] = None

    def includeInAll(includeInAll: Boolean): Self = {
      _includeInAll = Some(includeInAll)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _includeInAll.foreach(source.field("include_in_all", _))
    }
  }

  trait AttributeIgnoreMalformed extends Attribute { self: TypedFieldDefinition =>

    private[this] var _ignoreMalformed: Option[Boolean] = None

    def ignoreMalformed(ignoreMalformed: Boolean): Self = {
      _ignoreMalformed = Some(ignoreMalformed)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _ignoreMalformed.foreach(source.field("ignore_malformed", _))
    }
  }

  trait AttributeFormat extends Attribute { self: TypedFieldDefinition =>

    private[this] var _format: Option[String] = None

    def format(format: String): Self = {
      _format = Some(format)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _format.foreach(source.field("format", _))
    }
  }

  trait AttributeTermVector extends Attribute { self: TypedFieldDefinition =>

    private[this] var _termVector: Option[TermVector] = None

    def termVector(termVector: TermVector): Self = {
      _termVector = Some(termVector)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _termVector.foreach(arg => source.field("term_vector", arg.value))
    }
  }

  trait AttributeOmitNorms extends Attribute { self: TypedFieldDefinition =>

    private[this] var _omitNorms: Option[Boolean] = None

    def omitNorms(omitNorms: Boolean): Self = {
      _omitNorms = Some(omitNorms)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _omitNorms.foreach(source.field("omit_norms", _))
    }
  }

  trait AttributeIndexOptions extends Attribute { self: TypedFieldDefinition =>

    private[this] var _indexOptions: Option[IndexOptions] = None

    def indexOptions(indexOptions: IndexOptions): Self = {
      _indexOptions = Some(indexOptions)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _indexOptions.foreach(arg => source.field("index_options", arg.value))
    }
  }

  trait AttributeAnalyzer extends Attribute { self: TypedFieldDefinition =>

    private[this] var _analyzer: Option[String] = None

    def analyzer(analyzer: String): Self = {
      _analyzer = Some(analyzer)
      this.asInstanceOf[Self]
    }

    def analyzer(analyzer: Analyzer): Self = {
      _analyzer = Some(analyzer.name)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _analyzer.foreach(source.field("analyzer", _))
    }
  }

  trait AttributeIndexAnalyzer extends Attribute { self: TypedFieldDefinition =>

    private[this] var _indexAnalyzer: Option[String] = None

    def indexAnalyzer(analyzer: String): Self = {
      _indexAnalyzer = Some(analyzer)
      this.asInstanceOf[Self]
    }

    def indexAnalyzer(analyzer: Analyzer): Self = {
      _indexAnalyzer = Some(analyzer.name)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _indexAnalyzer.foreach(source.field("index_analyzer", _))
    }
  }

  trait AttributeSearchAnalyzer extends Attribute { self: TypedFieldDefinition =>

    private[this] var _searchAnalyzer: Option[String] = None

    def searchAnalyzer(analyzer: String): Self = {
      _searchAnalyzer = Some(analyzer)
      this.asInstanceOf[Self]
    }

    def searchAnalyzer(analyzer: Analyzer): Self = {
      _searchAnalyzer = Some(analyzer.name)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _searchAnalyzer.foreach(source.field("search_analyzer", _))
    }
  }

  trait AttributeIgnoreAbove extends Attribute { self: TypedFieldDefinition =>

    private[this] var _ignoreAbove: Option[Int] = None

    def ignoreAbove(ignoreAbove: Int): Self = {
      _ignoreAbove = Some(ignoreAbove)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _ignoreAbove.foreach(source.field("ignore_above", _))
    }
  }

  trait AttributePositionOffsetGap extends Attribute { self: TypedFieldDefinition =>

    private[this] var _positionOffsetGap: Option[Int] = None

    def positionOffsetGap(positionOffsetGap: Int): Self = {
      _positionOffsetGap = Some(positionOffsetGap)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _positionOffsetGap.foreach(source.field("position_offset_gap", _))
    }
  }

  trait AttributePostingsFormat extends Attribute { self: TypedFieldDefinition =>

    private[this] var _postingsFormat: Option[PostingsFormat] = None

    def postingsFormat(postingsFormat: PostingsFormat): Self = {
      _postingsFormat = Some(postingsFormat)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _postingsFormat.foreach(arg => source.field("postings_format", arg.value))
    }
  }

  trait AttributeSimilarity extends Attribute { self: TypedFieldDefinition =>

    private[this] var _similarity: Option[Similarity] = None

    def similarity(similarity: Similarity): Self = {
      _similarity = Some(similarity)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _similarity.foreach(arg => source.field("similarity", arg.value))
    }
  }

  trait AttributeLatLon extends Attribute { self: TypedFieldDefinition =>

    private[this] var _latLon: Option[Boolean] = None

    def latLon(latLon: Boolean): Self = {
      _latLon = Some(latLon)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _latLon.foreach(source.field("lat_lon", _))
    }
  }

  trait AttributeGeohash extends Attribute { self: TypedFieldDefinition =>

    private[this] var _geohash: Option[Boolean] = None

    def geohash(geohash: Boolean): Self = {
      _geohash = Some(geohash)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _geohash.foreach(source.field("geohash", _))
    }
  }

  trait AttributeGeohashPrecision extends Attribute { self: TypedFieldDefinition =>

    private[this] var _geohashPrecision: Option[String] = None

    def geohashPrecision(geohashPrecision: String): Self = {
      _geohashPrecision = Some(geohashPrecision)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _geohashPrecision.foreach(source.field("geohash_precision", _))
    }
  }

  trait AttributeGeohashPrefix extends Attribute { self: TypedFieldDefinition =>

    private[this] var _geohashPrefix: Option[Boolean] = None

    def geohashPrefix(geohashPrefix: Boolean): Self = {
      _geohashPrefix = Some(geohashPrefix)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _geohashPrefix.foreach(source.field("geohash_prefix", _))
    }
  }

  trait AttributeValidate extends Attribute { self: TypedFieldDefinition =>

    private[this] var _validate: Option[Boolean] = None

    def validate(validate: Boolean): Self = {
      _validate = Some(validate)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _validate.foreach(source.field("validate", _))
    }
  }

  trait AttributeValidateLat extends Attribute { self: TypedFieldDefinition =>

    private[this] var _validateLat: Option[Boolean] = None

    def validateLat(validateLat: Boolean): Self = {
      _validateLat = Some(validateLat)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _validateLat.foreach(source.field("validate_lat", _))
    }
  }

  trait AttributeValidateLon extends Attribute { self: TypedFieldDefinition =>

    private[this] var _validateLon: Option[Boolean] = None

    def validateLon(validateLon: Boolean): Self = {
      _validateLon = Some(validateLon)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _validateLon.foreach(source.field("validate_lon", _))
    }
  }

  trait AttributeNormalize extends Attribute { self: TypedFieldDefinition =>

    private[this] var _normalize: Option[Boolean] = None

    def normalize(normalize: Boolean): Self = {
      _normalize = Some(normalize)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _normalize.foreach(source.field("normalize", _))
    }
  }

  trait AttributeNormalizeLat extends Attribute { self: TypedFieldDefinition =>

    private[this] var _normalizeLat: Option[Boolean] = None

    def normalizeLat(normalizeLat: Boolean): Self = {
      _normalizeLat = Some(normalizeLat)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _normalizeLat.foreach(source.field("normalize_lat", _))
    }
  }

  trait AttributeNormalizeLon extends Attribute { self: TypedFieldDefinition =>

    private[this] var _normalizeLon: Option[Boolean] = None

    def normalizeLon(normalizeLon: Boolean): Self = {
      _normalizeLon = Some(normalizeLon)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _normalizeLon.foreach(source.field("normalize_lon", _))
    }
  }

  trait AttributeTree extends Attribute { self: TypedFieldDefinition =>

    private[this] var _tree: Option[PrefixTree] = None

    def tree(tree: PrefixTree): Self = {
      _tree = Some(tree)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _tree.foreach(arg => source.field("tree", arg.value))
    }
  }

  trait AttributePrecision extends Attribute { self: TypedFieldDefinition =>

    private[this] var _precision: Option[String] = None

    def precision(precision: String): Self = {
      _precision = Some(precision)
      this.asInstanceOf[Self]
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _precision.foreach(source.field("precision", _))
    }
  }

}
