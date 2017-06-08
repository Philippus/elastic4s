package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.common.xcontent.{NamedXContentRegistry, XContentFactory, XContentType}
import org.elasticsearch.index.query.{MoreLikeThisQueryBuilder, QueryBuilders}

object MoreLikeThisQueryBuilderFn {

  def apply(q: MoreLikeThisQueryDefinition): MoreLikeThisQueryBuilder = {

    val docs = q.likeDocs.map { item =>
      new MoreLikeThisQueryBuilder.Item(item.index, item.`type`, item.id).routing(item.routing.orNull)
    } ++ q.artificialDocs.map { doc =>

      val parser = XContentFactory.xContent(XContentType.JSON).createParser(NamedXContentRegistry.EMPTY, doc.doc.getBytes)
      parser.close()
      val builder = XContentFactory.jsonBuilder().copyCurrentStructure(parser)

      new MoreLikeThisQueryBuilder.Item(doc.index, doc.`type`, builder).routing(doc.routing.orNull)
    }

    println(docs)

    val builder = QueryBuilders.moreLikeThisQuery(
      q.fields.toArray,
      if (q.likeTexts.isEmpty) null else q.likeTexts.toArray,
      if (docs.isEmpty) null else docs.toArray
    )

    q.analyzer.foreach(builder.analyzer)
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.boostTerms.map(_.toFloat).foreach(builder.boostTerms)
    q.maxWordLength.foreach(builder.maxWordLength)
    q.failOnUnsupportedField.foreach(builder.failOnUnsupportedField)
    q.include.foreach(builder.include)
    q.maxDocFreq.foreach(builder.maxDocFreq)
    q.maxQueryTerms.foreach(builder.maxQueryTerms)
    q.minDocFreq.foreach(builder.minDocFreq)
    q.minShouldMatch.foreach(builder.minimumShouldMatch)
    q.minTermFreq.foreach(builder.minTermFreq)
    q.minWordLength.foreach(builder.minWordLength)
    q.queryName.foreach(builder.queryName)

    builder.unlike(q.unlikeDocs.toArray.map { item =>
      new MoreLikeThisQueryBuilder.Item(item.index, item.`type`, item.id).routing(item.routing.orNull)
    })
    builder.unlike(q.unlikeTexts.toArray)
    builder.stopWords(q.stopWords: _*)
    builder
  }
}
