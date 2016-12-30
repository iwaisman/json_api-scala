package org.twistednoodle.json_api

// Created by iwaisman on 12/29/16.

/**
  * A trait containing all the type-classes easing construction of json:api structures.
  */
trait Entrance { self: JsonApi =>

  trait ToJsonApiDocument[T] {
    def from(t: T): JsonApiDocument
  }
  object ToJsonApiDocument {
    def apply[T](implicit writer: ToJsonApiDocument[T]): ToJsonApiDocument[T] = writer
    def from[T](t: T)(implicit writer: ToJsonApiDocument[T]): JsonApiDocument = writer.from(t)
  }

  trait ToResourceObject[T] {
    def from(t: T): ResourceObject
  }
  object ToResourceObject {
    def apply[T](implicit writer: ToResourceObject[T]): ToResourceObject[T] = writer
    def from[T](t: T)(implicit writer: ToResourceObject[T]): ResourceObject = writer.from(t)
  }

  trait ToIdentifiedResourceObject[T] {
    def from(t: T): IdentifiedResourceObject
  }
  object ToIdentifiedResourceObject {
    def apply[T](implicit writer: ToIdentifiedResourceObject[T]): ToIdentifiedResourceObject[T] = writer
    def from[T](t: T)(implicit writer: ToIdentifiedResourceObject[T]): IdentifiedResourceObject = writer.from(t)

    def derive[T](getId: T => String)(implicit tro: ToResourceObject[T]): ToIdentifiedResourceObject[T] =
      (t: T) => ResourceObject(getId(t), tro.from(t))
  }

  trait ToResourceIdentifier[T] {
    def from(t: T): ResourceIdentifier
  }
  object ToResourceIdentifier {
    def apply[T](implicit writer: ToResourceIdentifier[T]): ToResourceIdentifier[T] = writer
    def from[T](t: T)(implicit writer: ToResourceIdentifier[T]): ResourceIdentifier = writer.from(t)

    implicit def fromIRO[T](implicit iroWriter: ToIdentifiedResourceObject[T]): ToResourceIdentifier[T] =
      (t: T) => iroWriter.from(t)
  }
}
