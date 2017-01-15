package org.twistednoodle.json_api

// Created by iwaisman on 12/29/16.

/**
  * A top level trait used to define the type of underlying JSON content.
  *
  * This organization allows us to avoid modeling general JSON which is already
  * being done by the associated JSON integration.
  *
  */
trait JsonApi extends Model with
                      Entrance with
                      Exit {
  type JSON
}
