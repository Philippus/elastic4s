package com.sksamuel.elastic4s.requests.task

import com.fasterxml.jackson.annotation.JsonProperty

case class TaskError(`type`: String,
                     reason: String,
                     phase: String,
                     grouped: Boolean,
                     @JsonProperty("caused_by") causedBy: Option[Cause]
                    )
