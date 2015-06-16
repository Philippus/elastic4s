package com.sksamuel.elastic4s.examples

import com.sksamuel.elastic4s._

class ClusterSettingsDotDsl extends ElasticDsl {

  clusterPersistentSettings(Map("a" -> "b", "c" -> "d"))

  clusterTransientSettings(Map("f" -> "g"))

  clusterPersistentSettings(Map("a" -> "b", "c" -> "d")).transientSettings(Map("f" -> "g"))

  clusterTransientSettings(Map("f" -> "g")).persistentSettings(Map("f" -> "g"))

}
