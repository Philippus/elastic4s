package com.sksamuel.elastic4s.http.index

import com.fasterxml.jackson.annotation.JsonProperty

case class CreateIndexResponse(acknowledged: Boolean, shards_acknowledged: Boolean)

case class CreateIndexFailure(`type`: String, reason: String, @JsonProperty("index_uuid") indexUuid: String, index: String)

//{
//  "error": {
//    "root_cause":[
//      {  "type": "resource_already_exists_exception",
//         "reason": "index [foo/YWHF4HPDTl6L3rs7yInX4Q] already exists",
//         "index_uuid":"YWHF4HPDTl6L3rs7yInX4Q","index":"foo"
//      }
//     ],
//    "type":"resource_already_exists_exception",
//    "reason": "index [foo/YWHF4HPDTl6L3rs7yInX4Q] already exists",
//    "index_uuid":"YWHF4HPDTl6L3rs7yInX4Q",
//    "index":"foo"
//  },
//  "status":400
// }
