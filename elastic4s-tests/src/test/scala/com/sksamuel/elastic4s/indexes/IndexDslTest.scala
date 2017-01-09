//    val req = index into "twitter/tweets" fields Map(
//      "user" -> "sammy",
//      "post_date" -> "2009-11-15T14:12:12",
//      "message" -> "trying out Elastic Search Scala DSL"
//    )
//
//    checkRequest(req, "twitter", "tweets", "/json/index/simple_multiple.json")
//  }
//
//  it should "generate nested fields" in {
//    val req = index into "twitter/tweets" fields (
//      "user" -> Map(
//        "handle" -> "sammy",
//        "name" -> "Sam"
//      ),
//        "post_date" -> "2011-11-15T14:12:12",
//        "message" -> "Nested message"
//    )
//
//    checkRequest(req, "twitter", "tweets", "/json/index/nested.json")
//  }
//
//  it should "generate array fields" in {
//    val req = index into "twitter/tweets" fields (
//      "user" -> "sammy",
//      "post_date" -> "2011-11-15T14:12:12",
//      "message" -> "Array message",
//      "tags" -> Array(
//        "array",
//        "search",
//        "test"
//      )
//    )
//
//    checkRequest(req, "twitter", "tweets", "/json/index/array.json")
//  }
//
//  it should "generate array fields when using seqs" in {
//    val req = index into "twitter/tweets" fields (
//      "user" -> "sammy",
//      "post_date" -> "2011-11-15T14:12:12",
//      "message" -> "Array message",
//      "tags" -> Seq(
//        "array",
//        "search",
//        "test"
//      )
//    )
//
//    checkRequest(req, "twitter", "tweets", "/json/index/array.json")
//  }
//
//  it should "generate array of nested fields" in {
//    val req = index into "twitter/tweets" fields (
//      "user" -> "sammy",
//      "post_date" -> "2011-11-15T14:12:12",
//      "message" -> "Array of nested message",
//      "tags" -> Array(
//        Map(
//          "id" -> 642,
//          "text" -> "array"
//        ),
//        Map(
//          "id" -> 883,
//          "text" -> "search"
//        ),
//        Map(
//          "id" -> 231,
//          "text" -> "test"
//        )
//      )
//    )
//
//    checkRequest(req, "twitter", "tweets", "/json/index/array_nested.json")
//  }
//
//  it should "custom field values" in {
//    val cal = new GregorianCalendar()
//    cal.set(Calendar.YEAR, 2009)
//    cal.set(Calendar.MONTH, 10)
//    cal.set(Calendar.DAY_OF_MONTH, 15)
//    cal.set(Calendar.HOUR_OF_DAY, 14)
//    cal.set(Calendar.MINUTE, 12)
//    cal.set(Calendar.SECOND, 12)
//
//    val req = index into "twitter/tweets" fieldValues (
//      SimpleFieldValue("user", "sammy"),
//      CustomDateFieldValue("post_date", cal.getTime),
//      SimpleFieldValue("message", "trying out Elastic Search Scala DSL")
//    )
//
//    checkRequest(req, "twitter", "tweets", "/json/index/simple_multiple.json")
//  }
//
//
//  case class CustomDateFieldValue(name: String, date: Date) extends FieldValue {
//    private val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//
//    def output(source: XContentBuilder): Unit = {
//      source.field(name, dateFormat.format(date))
//    }
//  }
//}
