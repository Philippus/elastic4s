## Java interoperability

### Indexing Arrays

The DSL does most of the work for you to not have to deal with Java type.
However a few corner cases remain where you have to take care to use the right
types.

Elasticsearch allows you to index an array of elements. Note that you cannot
specify this in the mapping but will be accepted after you setup your mappings.
To index an array you have to use Scala's `Array` type:

```scala
index into "threads" -> "users" fields (
  "name" -> "tony",
  "roles" -> Array("admin","root","user")
) id 1234
```
Alternatively you can also use Java's Array type which is bytecode equivalent
due to Scala's design.
If you have Scala `Lists` or `Seq` you can turn them into an Array with
`toArray`.

This applies of course to all parts of the API where you can pass multiple
values and not just the indexing part.

### Dealing with SearchResponse
The result for many `search` (and similarly `get` & `multiget`) queries will bring
you back into the Java world which means you have to deal with slightly less
intuitive code.

There is a few ways you can get your results into a nice `case class` but here
is one option:

```scala
// Necessary for the automatic conversion from java.util.List to Seq and obj.toMap
import scala.collection.JavaConversions._

case class User(
  name: String,
  roles: Seq[String]
  note: Option[String]
)

object User {
  /**
   * @see http://stackoverflow.com/questions/20684572/
   */
  def unapply(m: Map[String, Any]): Option[User] = try {
    val noteES = m("note").asInstanceOf[String]
    Some(User(
      id    = m("_id")  .asInstanceOf[String],
      name  = m("name") .asInstanceOf[String],
      roles = m("roles").asInstanceOf[java.util.List[String]],
      note  = if (noteES == "") None else Some(noteES)))
  } catch {
    case ex: Exception => None
  }

  /**
   * Generates a user given a Map (from ES)
   * @throws scala.MatchError if not applied properly
   */
  def apply(m: Map[String, Any]): User = {
    val User(u) = m
    u
  }

  /**
   * Generates a user given an Elasticsearch SearchHit
   * Note that it might be desirable to minimize dependencies and only allow to
   * construct this from a Map and not from a SearchHit
   * @throws scala.MatchError if not applied properly
   */
  def apply(sh: SearchHit): User = {
    User(sh.getSource.toMap)
  }
}

val user = client execute {
  import com.sksamuel.elastic4s.ElasticDsl._
  search in "threads" -> "users" query "username:bill"
}

val u = if( esUser.getHits.totalHits == 1) {
  val hit = esUser.getHits.getAt(0) // is of type SearchHit (ES's Java API)
  User(hit) // Note that this throws a MatchError if the format isn't right
} else {
  throw new Exception("No such user")
}
```

