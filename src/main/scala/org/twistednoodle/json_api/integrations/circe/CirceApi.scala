package org.twistednoodle.json_api.integrations.circe

import io.circe.JsonObject
import org.twistednoodle.json_api.JsonApi

// Created by iwaisman on 12/30/16.

trait CirceApi extends JsonApi with
                       CirceEncoding with
                       CirceDecoding {

  /**
    * The circe specific type representing free-form JSON used for meta and attribute objects.
    */
  override type JSON = JsonObject
}
