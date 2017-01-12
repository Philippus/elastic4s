package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.index.query.{MoreLikeThisQueryBuilder, QueryBuilders}

object MoreLikeThisQueryBuilderFn {

  def apply(q: MoreLikeThisQueryDefinition): MoreLikeThisQueryBuilder = {

    val builder = QueryBuilders.moreLikeThisQuery(
      q.fields.toArray,
      q.likeTexts.toArray,
      q.likeDocs.map { doc => new MoreLikeThisQueryBuilder.Item(doc.index, doc.`type`, doc.id) }.toArray
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

    builder.unlike(q.unlikeDocs.toArray.map { doc => new MoreLikeThisQueryBuilder.Item(doc.index, doc.`type`, doc.id) })
    builder.unlike(q.unlikeTexts.toArray)
    builder.stopWords(q.stopWords: _*)
    builder
  }
}
