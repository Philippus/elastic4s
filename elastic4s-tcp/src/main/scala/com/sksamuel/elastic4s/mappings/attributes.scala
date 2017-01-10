package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.analyzers.Analyzer
import org.elasticsearch.common.xcontent.XContentBuilder

object attributes {

  sealed trait Attribute { self: FieldDefinition =>

    protected def insert(source: XContentBuilder): Unit
  }

  trait AttributeIndexName extends Attribute { self: TypedFieldDefinition =>

    private[this] var _indexName: Option[String] = None

    def indexName(indexName: String): this.type = {
      _indexName = Some(indexName)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _indexName.foreach(source.field("index_name", _))
    }
  }

  trait AttributeEnabled extends Attribute { self: TypedFieldDefinition =>

    private[this] var _enabled: Option[Boolean] = None

    def enabled(enabled: Boolean): this.type = {
      _enabled = Some(enabled)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _enabled.foreach(source.field("enabled", _))
    }
  }

  trait AttributeStore extends Attribute { self: TypedFieldDefinition =>

    private[this] var _store: Option[String] = None

    @deprecated("use stored(true) or stored(false)", "5.0.0")
    def stored(param: YesNo): this.type = store(param)

    @deprecated("use stored(true) or stored(false)", "5.0.0")
    def store(store: YesNo): this.type = {
      _store = Some(store.value)
      this
    }

    def stored(param: Boolean): this.type = store(param)
    def store(param: Boolean): this.type = {
      _store = Some(YesNo(param).value)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _store.foreach(source.field("store", _))
    }
  }

  trait AttributeIndex extends Attribute { self: TypedFieldDefinition =>

    private[this] var _index: Option[String] = None

    def index(index: String): this.type = {
      _index = Some(index)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _index.foreach(source.field("index", _))
    }
  }

  trait AttributePrecisionStep extends Attribute { self: TypedFieldDefinition =>

    private[this] var _precisionStep: Option[Int] = None

    def precisionStep(precisionStep: Int): this.type = {
      _precisionStep = Some(precisionStep)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _precisionStep.foreach(source.field("precision_step", _))
    }
  }

  trait AttributeBoost extends Attribute { self: TypedFieldDefinition =>

    private[this] var _boost: Option[Double] = None

    def boost(boost: Double): this.type = {
      _boost = Some(boost)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _boost.foreach(source.field("boost", _))
    }
  }

  trait AttributeNullValue[T] extends Attribute { self: TypedFieldDefinition =>

    private[this] var _nullValue: Option[T] = None

    def nullValue(nullValue: T): this.type = {
      _nullValue = Some(nullValue)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _nullValue.foreach(source.field("null_value", _))
    }
  }

  trait AttributeIncludeInAll extends Attribute { self: TypedFieldDefinition =>

    private[this] var _includeInAll: Option[Boolean] = None

    def includeInAll(includeInAll: Boolean): this.type = {
      _includeInAll = Some(includeInAll)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _includeInAll.foreach(source.field("include_in_all", _))
    }
  }

  trait AttributeIgnoreMalformed extends Attribute { self: TypedFieldDefinition =>

    private[this] var _ignoreMalformed: Option[Boolean] = None

    def ignoreMalformed(ignoreMalformed: Boolean): this.type = {
      _ignoreMalformed = Some(ignoreMalformed)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _ignoreMalformed.foreach(source.field("ignore_malformed", _))
    }
  }

  trait AttributeFormat extends Attribute { self: TypedFieldDefinition =>

    private[this] var _format: Option[String] = None

    def format(format: String): this.type = {
      _format = Some(format)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _format.foreach(source.field("format", _))
    }
  }

  trait AttributeTermVector extends Attribute {
    self: TypedFieldDefinition =>

    private[this] var _termVector: Option[TermVector] = None
    private[this] var _termVectorString: Option[String] = None

    def termVector(termVector: TermVector): this.type = {
      _termVector = Some(termVector)
      this
    }

    def termVector(termVector: String): this.type = {
      _termVectorString = Some(termVector)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _termVector.foreach(arg => source.field("term_vector", arg.value))
      _termVectorString.foreach(source.field("term_vector", _))
    }
  }

  trait AttributeNorms extends Attribute { self: TypedFieldDefinition =>

    private[this] var _norms: Option[Boolean] = None

    def norms(omitNorms: Boolean): this.type = {
      _norms = Some(omitNorms)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _norms.foreach(source.field("norms", _))
    }
  }

  trait AttributeOmitNorms extends Attribute { self: TypedFieldDefinition =>

    private[this] var _omitNorms: Option[Boolean] = None

    def omitNorms(omitNorms: Boolean): this.type = {
      _omitNorms = Some(omitNorms)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _omitNorms.foreach(source.field("omit_norms", _))
    }
  }

  trait AttributeIndexOptions extends Attribute { self: TypedFieldDefinition =>

    private[this] var _indexOptions: Option[String] = None

    def indexOptions(indexOptions: String): this.type = {
      _indexOptions = Some(indexOptions)
      this
    }

    def indexOptions(indexOptions: IndexOptions): this.type = {
      _indexOptions = Some(indexOptions.value)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _indexOptions.foreach(source.field("index_options", _))
    }
  }

  trait AttributeAnalyzer extends Attribute { self: FieldDefinition =>

    private[this] var _analyzer: Option[String] = None

    def analyzer(analyzer: String): this.type = {
      _analyzer = Some(analyzer)
      this
    }

    def analyzer(analyzer: Analyzer): this.type = {
      _analyzer = Some(analyzer.name)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _analyzer.foreach(source.field("analyzer", _))
    }
  }

  trait AttributeSearchAnalyzer extends Attribute { self: TypedFieldDefinition =>

    private[this] var _searchAnalyzer: Option[String] = None

    def searchAnalyzer(analyzer: String): this.type = {
      _searchAnalyzer = Some(analyzer)
      this
    }

    def searchAnalyzer(analyzer: Analyzer): this.type = {
      _searchAnalyzer = Some(analyzer.name)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _searchAnalyzer.foreach(source.field("search_analyzer", _))
    }
  }

  trait AttributeIgnoreAbove extends Attribute { self: TypedFieldDefinition =>

    private[this] var _ignoreAbove: Option[Int] = None

    def ignoreAbove(ignoreAbove: Int): this.type = {
      _ignoreAbove = Some(ignoreAbove)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _ignoreAbove.foreach(source.field("ignore_above", _))
    }
  }

  trait AttributePositionOffsetGap extends Attribute { self: TypedFieldDefinition =>

    private[this] var _positionOffsetGap: Option[Int] = None

    def positionOffsetGap(positionOffsetGap: Int): this.type = {
      _positionOffsetGap = Some(positionOffsetGap)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _positionOffsetGap.foreach(source.field("position_offset_gap", _))
    }
  }

  trait AttributePostingsFormat extends Attribute { self: TypedFieldDefinition =>

    private[this] var _postingsFormat: Option[PostingsFormat] = None

    def postingsFormat(postingsFormat: PostingsFormat): this.type = {
      _postingsFormat = Some(postingsFormat)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _postingsFormat.foreach(arg => source.field("postings_format", arg.value))
    }
  }

  trait AttributeDocValues extends Attribute { self: TypedFieldDefinition =>

    private[this] var _docValues: Option[Boolean] = None

    def docValuesFormat(b: Boolean): this.type = {
      _docValues = Some(b)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _docValues.foreach(source.field("doc_values", _))
    }
  }

  trait AttributeSimilarity extends Attribute { self: TypedFieldDefinition =>

    private[this] var _similarity: Option[Similarity] = None

    def similarity(similarity: Similarity): this.type = {
      _similarity = Some(similarity)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _similarity.foreach(arg => source.field("similarity", arg.value))
    }
  }

  trait AttributeLatLon extends Attribute { self: TypedFieldDefinition =>

    private[this] var _latLon: Option[Boolean] = None

    def latLon(latLon: Boolean): this.type = {
      _latLon = Some(latLon)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _latLon.foreach(source.field("lat_lon", _))
    }
  }

  trait AttributeGeohash extends Attribute { self: TypedFieldDefinition =>

    private[this] var _geohash: Option[Boolean] = None

    def geohash(geohash: Boolean): this.type = {
      _geohash = Some(geohash)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _geohash.foreach(source.field("geohash", _))
    }
  }

  trait AttributeGeohashPrecision extends Attribute { self: TypedFieldDefinition =>

    private[this] var _geohashPrecision: Option[String] = None

    def geohashPrecision(geohashPrecision: String): this.type = {
      _geohashPrecision = Some(geohashPrecision)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _geohashPrecision.foreach(source.field("geohash_precision", _))
    }
  }

  trait AttributeGeohashPrefix extends Attribute { self: TypedFieldDefinition =>

    private[this] var _geohashPrefix: Option[Boolean] = None

    def geohashPrefix(geohashPrefix: Boolean): this.type = {
      _geohashPrefix = Some(geohashPrefix)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _geohashPrefix.foreach(source.field("geohash_prefix", _))
    }
  }

  trait AttributeValidate extends Attribute { self: TypedFieldDefinition =>

    private[this] var _validate: Option[Boolean] = None

    def validate(validate: Boolean): this.type = {
      _validate = Some(validate)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _validate.foreach(source.field("validate", _))
    }
  }

  trait AttributeValidateLat extends Attribute { self: TypedFieldDefinition =>

    private[this] var _validateLat: Option[Boolean] = None

    def validateLat(validateLat: Boolean): this.type = {
      _validateLat = Some(validateLat)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _validateLat.foreach(source.field("validate_lat", _))
    }
  }

  trait AttributeValidateLon extends Attribute { self: TypedFieldDefinition =>

    private[this] var _validateLon: Option[Boolean] = None

    def validateLon(validateLon: Boolean): this.type = {
      _validateLon = Some(validateLon)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _validateLon.foreach(source.field("validate_lon", _))
    }
  }

  trait AttributeNormalize extends Attribute { self: TypedFieldDefinition =>

    private[this] var _normalize: Option[Boolean] = None

    def normalize(normalize: Boolean): this.type = {
      _normalize = Some(normalize)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _normalize.foreach(source.field("normalize", _))
    }
  }

  trait AttributeNormalizeLat extends Attribute { self: TypedFieldDefinition =>

    private[this] var _normalizeLat: Option[Boolean] = None

    def normalizeLat(normalizeLat: Boolean): this.type = {
      _normalizeLat = Some(normalizeLat)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _normalizeLat.foreach(source.field("normalize_lat", _))
    }
  }

  trait AttributeNormalizeLon extends Attribute { self: TypedFieldDefinition =>

    private[this] var _normalizeLon: Option[Boolean] = None

    def normalizeLon(normalizeLon: Boolean): this.type = {
      _normalizeLon = Some(normalizeLon)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _normalizeLon.foreach(source.field("normalize_lon", _))
    }
  }

  trait AttributeTree extends Attribute { self: TypedFieldDefinition =>

    private[this] var _tree: Option[PrefixTree] = None

    def tree(tree: PrefixTree): this.type = {
      _tree = Some(tree)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _tree.foreach(arg => source.field("tree", arg.value))
    }
  }

  trait AttributePrecision extends Attribute { self: TypedFieldDefinition =>

    private[this] var _precision: Option[String] = None

    def precision(precision: String): this.type = {
      _precision = Some(precision)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _precision.foreach(source.field("precision", _))
    }
  }

  trait AttributePath extends Attribute { self: TypedFieldDefinition =>

    private[this] var _path: Option[String] = None

    def path(path: String): this.type = {
      _path = Some(path)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _path.foreach(source.field("path", _))
    }
  }

  trait AttributePayloads extends Attribute { self: TypedFieldDefinition =>

    private[this] var _payloads: Option[Boolean] = None

    def payloads(payloads: Boolean): this.type = {
      _payloads = Some(payloads)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _payloads.foreach(source.field("payloads", _))
    }
  }

  trait AttributePreserveSeparators extends Attribute { self: TypedFieldDefinition =>

    private[this] var _preserveSeparators: Option[Boolean] = None

    def preserveSeparators(preserveSeparators: Boolean): this.type = {
      _preserveSeparators = Some(preserveSeparators)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _preserveSeparators.foreach(source.field("preserve_separators", _))
    }
  }

  trait AttributePreservePositionIncrements extends Attribute { self: TypedFieldDefinition =>

    private[this] var _preservePositionIncrements: Option[Boolean] = None

    def preservePositionIncrements(preservePositionIncrements: Boolean): this.type = {
      _preservePositionIncrements = Some(preservePositionIncrements)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _preservePositionIncrements.foreach(source.field("preserve_position_increments", _))
    }
  }

  trait AttributeMaxInputLen extends Attribute { self: TypedFieldDefinition =>

    private[this] var _maxInputLen: Option[Int] = None

    def maxInputLen(maxInputLen: Int): this.type = {
      _maxInputLen = Some(maxInputLen)
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _maxInputLen.foreach(source.field("max_input_len", _))
    }
  }

  trait AttributeCopyTo extends Attribute { self: TypedFieldDefinition =>

    private[this] var _copyTo: Option[Seq[String]] = None

    def copyTo(copyTo: String*): this.type = {
      _copyTo = Some(copyTo)
      this
    }

    import scala.collection.JavaConverters._

    protected override def insert(source: XContentBuilder): Unit = {
      _copyTo.foreach(xs => source.field("copy_to", xs.asJava))
    }
  }

  trait AttributeFields extends Attribute { self: TypedFieldDefinition =>

    private[this] var _fields: Seq[TypedFieldDefinition] = Nil

    def fields(fields: TypedFieldDefinition*): this.type = {
      _fields = fields
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      if (_fields.nonEmpty) {
        source.startObject("fields")
        for (field <- _fields) {
          field.build(source)
        }
        source.endObject()
      }
    }
  }

  trait AttributeIncludeInRoot extends Attribute { self: TypedFieldDefinition =>

    private[this] var _includeInRoot: Option[String] = None

    def includeInRoot(includeInRoot: YesNo): this.type = {
      _includeInRoot = Some(includeInRoot.value)
      this
    }

    def includeInRoot(param: Boolean): this.type = {
      includeInRoot(YesNo(param))
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _includeInRoot.foreach(source.field("include_in_root", _))
    }
  }

  trait AttributeIncludeInParent extends Attribute { self: TypedFieldDefinition =>

    private[this] var _includeInParent: Option[String] = None

    def includeInParent(includeInParent: YesNo): this.type = {
      _includeInParent = Some(includeInParent.value)
      this
    }

    def includeInParent(param: Boolean): this.type = {
      includeInParent(YesNo(param))
      this
    }

    protected override def insert(source: XContentBuilder): Unit = {
      _includeInParent.foreach(source.field("include_in_parent", _))
    }
  }

}
