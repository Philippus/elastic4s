package com.sksamuel.elastic4s.termvectors

import com.sksamuel.elastic4s.Executable
import org.elasticsearch.action.termvectors.TermVectorsRequest.FilterSettings
import org.elasticsearch.action.termvectors.{TermVectorsRequestBuilder, TermVectorsResponse}
import org.elasticsearch.client.Client
import org.elasticsearch.index.VersionType

import scala.collection.JavaConverters._
import scala.concurrent.Future

trait TermVectorsExecutables {

  implicit object TermVectorExecutable
    extends Executable[TermVectorsDefinition, TermVectorsResponse, TermVectorsResult] {

    def builder(client: Client, t: TermVectorsDefinition): TermVectorsRequestBuilder = {
      val builder = client.prepareTermVectors(t.indexAndType.index, t.indexAndType.`type`, t.id)
      t.fieldStatistics.foreach(builder.setFieldStatistics)
      t.offsets.foreach(builder.setOffsets)
      t.parent.foreach(builder.setParent)
      t.payloads.foreach(builder.setPayloads)
      builder.setPerFieldAnalyzer(t.perFieldAnalyzer.asJava)
      t.positions.foreach(builder.setPositions)
      t.preference.foreach(builder.setPreference)
      t.realtime.foreach(b => builder.setRealtime(java.lang.Boolean.valueOf(b)))
      t.routing.foreach(builder.setRouting)
      if (t.fields.nonEmpty)
        builder.setSelectedFields(t.fields: _*)
      t.termStatistics.foreach(builder.setTermStatistics)
      t.version.foreach(builder.setVersion)
      t.versionType.map(VersionType.fromString).foreach(builder.setVersionType)

      val settings = new FilterSettings()
      t.maxNumTerms.foreach(settings.maxNumTerms = _)
      t.minTermFreq.foreach(settings.minTermFreq = _)
      t.maxTermFreq.foreach(settings.maxTermFreq = _)
      t.minDocFreq.foreach(settings.minDocFreq = _)
      t.maxDocFreq.foreach(settings.maxDocFreq = _)
      t.minWordLength.foreach(settings.minWordLength = _)
      t.maxWordLength.foreach(settings.maxWordLength = _)

      builder.setFilterSettings(settings)
      builder
    }

    override def apply(client: Client, t: TermVectorsDefinition): Future[TermVectorsResult] = {
      val _builder = builder(client, t)
      injectFutureAndMap(_builder.execute)(TermVectorsResult.apply)
    }
  }
}
