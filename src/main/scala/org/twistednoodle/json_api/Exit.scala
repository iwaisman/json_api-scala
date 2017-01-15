package org.twistednoodle.json_api


// Created by iwaisman on 1/14/17.

/** A trait containing some type-classes easing the construction of types from json:api structures.
  *
  */
trait Exit { self: JsonApi =>

  trait FromJsonApiResourceObject[T] {
    def from(resource: ResourceObject): String Either T
  }
  object FromJsonApiResourceObject {
    def apply[T](implicit reader: FromJsonApiResourceObject[T]): FromJsonApiResourceObject[T] = reader
    def from[T](resource: ResourceObject)(implicit reader: FromJsonApiResourceObject[T]): String Either T = reader.from(resource)
  }

  trait FromJsonApiDocument[T] {
    def from(document: DataDocument): Throwable Either T
  }
  object FromJsonApiDocument {
    def apply[T](implicit reader: FromJsonApiDocument[T]): FromJsonApiDocument[T] = reader
    def from[T](document: DataDocument)(implicit reader: FromJsonApiDocument[T]): Throwable Either T = reader.from(document)
  }

}
