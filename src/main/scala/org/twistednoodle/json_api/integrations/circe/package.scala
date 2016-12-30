package org.twistednoodle.json_api.integrations

import org.twistednoodle.json_api.instances.ScalaInstances

// Created by iwaisman on 12/30/16.

package object circe extends CirceApi with
                             ScalaInstances with
                             AkkaInstances {

  object instances {
    val scala = __ScalaInstances
    val akka = __AkkaInstances
  }
}