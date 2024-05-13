package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.synonyms.{DeleteSynonymRuleRequest, DeleteSynonymsSetRequest, GetSynonymsSetRequest, ListSynonymsSetRequest, SynonymRule, CreateOrUpdateSynonymRuleRequest, CreateOrUpdateSynonymsSetRequest}

trait SynonymsApi {
  def createOrUpdateSynonymsSet(synonymsSet: String, synonymRules: Seq[SynonymRule]): CreateOrUpdateSynonymsSetRequest =
    CreateOrUpdateSynonymsSetRequest(synonymsSet, synonymRules)

  def getSynonymsSet(synonymsSet: String, from: Option[Int] = None, size: Option[Int] = None): GetSynonymsSetRequest =
    GetSynonymsSetRequest(synonymsSet, from, size)

  def listSynonymsSet(from: Option[Int] = None, size: Option[Int] = None): ListSynonymsSetRequest =
    ListSynonymsSetRequest(from, size)

  def deleteSynonymsSet(synonymsSet: String): DeleteSynonymsSetRequest = DeleteSynonymsSetRequest(synonymsSet)

  def upsertSynonymRule(synonymsSet: String, synonymRule: String, synonyms: String): CreateOrUpdateSynonymRuleRequest =
    CreateOrUpdateSynonymRuleRequest(synonymsSet, synonymRule, synonyms)

  def deleteSynonymRule(synonymsSet: String, synonymRule: String): DeleteSynonymRuleRequest =
    DeleteSynonymRuleRequest(synonymsSet, synonymRule)
}
