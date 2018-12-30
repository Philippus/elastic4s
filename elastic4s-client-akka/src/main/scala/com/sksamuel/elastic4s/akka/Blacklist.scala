package com.sksamuel.elastic4s.akka

/**
  * List of 'bad' hosts.
  * Implementation must have expiration logic backed-in.
  */
private[akka] trait Blacklist {

  /**
    * Adds a host to the blacklist.
    *
    * @param host host
    * @return true if record is blacklisted for the first time
    */
  def add(host: String): Boolean

  /**
    * Removes a host from the blacklist.
    *
    * @param host host
    * @return true if host was blacklisted
    */
  def remove(host: String): Boolean

  /**
    * Checks if a host can be used.
    *
    * @param host host
    * @return true if host is not in a blacklist or temporary removed from it
    */
  def contains(host: String): Boolean

  /**
    * Number of hosts in blacklist
    */
  def size: Int

  /**
    * List all hosts in the blacklist
    *
    * @return
    */
  def list: List[String]
}

