package com.sksamuel.elastic4s.examples

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.source.Indexable

// examples of the count API in dot notation
class UpdateDotDsl extends ElasticDsl {

  // update a doc by id in a given index / type.specifying a replacement doc
  update("id").in("index" / "type").doc("name" -> "sam", "occuptation" -> "build breaker")

  // update a doc by id in a given index / type specifying a replacement doc with the optimistic locking set
  update("id").in("index" / "type").version(3).doc("name" -> "sam", "occuptation" -> "build breaker")

  // update by id, using an implicit indexable for the updated doc
  case class Document(a: String, b: String, c: String)
  val somedoc = Document("a", "b", "c")
  implicit case object DocumentIndexable extends Indexable[Document] {
    override def json(t: Document): String = ???
  }
  update("id").in("index" / "type").source(somedoc)

  // update by id, using a script
  update("id").in("index" / "type").script("ctx._source.somefield = 'value'")

  // update by id, using a script, where the script language is specified
  update("id").in("index" / "type").script("ctx._source.somefield = 'value'").lang("sillylang")

  // update by id, using a script which has parameters which are passed in as a map
  update("id").in("index" / "type").script("ctx._source.somefield = myparam").params(Map("myparam" -> "sam"))
}

