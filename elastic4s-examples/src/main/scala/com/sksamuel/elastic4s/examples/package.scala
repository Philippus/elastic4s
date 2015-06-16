package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.source.Indexable

package object examples {
  implicit case object DocumentIndexable extends Indexable[Document] {
    override def json(t: Document): String = ???
  }
}
