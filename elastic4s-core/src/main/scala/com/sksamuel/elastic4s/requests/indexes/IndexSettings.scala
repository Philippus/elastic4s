package com.sksamuel.elastic4s.requests.indexes

class IndexSettings {

  private val ShardsKey          = "number_of_shards"
  private val ReplicasKey        = "number_of_replicas"
  private val RefreshIntervalKey = "refresh_interval"

  val settings = scala.collection.mutable.Map.empty[String, Any]

  def shards: Option[Int]             = settings.get(ShardsKey).map(_.asInstanceOf[Int])
  def shards_=(s: Int): IndexSettings = add(ShardsKey, s)

  def replicas: Option[Int]             = settings.get(ReplicasKey).map(_.asInstanceOf[Int])
  def replicas_=(r: Int): IndexSettings = add(ReplicasKey, r)

  def refreshInterval: Option[String]             = settings.get(RefreshIntervalKey).map(_.asInstanceOf[String])
  def refreshInterval_=(i: String): IndexSettings = add(RefreshIntervalKey, i)

  def add(name: String, value: Any): IndexSettings = {
    settings += (name -> value)
    this
  }
}
