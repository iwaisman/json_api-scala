package org.twistednoodle.json_api.instances

import org.twistednoodle.json_api.JsonApi


// Created by iwaisman on 12/30/16.

trait ScalaInstances { self: JsonApi =>

  private[circe] object __ScalaInstances {
    implicit def eitherToJsonApiRoot[E, A](implicit
                                           ew: ToJsonApiDocument[E],
                                           aw: ToJsonApiDocument[A]): ToJsonApiDocument[E Either A] =
      new ToJsonApiDocument[E Either A] {
        override def from(thing: Either[E, A]): JsonApiDocument = thing match {
          case Left(e)  => ew.from(e)
          case Right(a) => aw.from(a)
        }
      }
  }
}
