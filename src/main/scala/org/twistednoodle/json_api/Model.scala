package org.twistednoodle.json_api

import java.net.URL

import scala.collection.immutable

// Created by iwaisman on 12/29/16.

/**
  * This trait encompasses all data structures representing json:api objects.
  *
  * There remain restrictions in the specification which have not yet been
  * encoded into the type system. For example, a relationship links object must contain
  * at least one of 'self' and/or 'related'. <shrug>
  *
  * @see http://http://jsonapi.org/ for details
  *
  */
trait Model { self: JsonApi =>

  type Links = Map[String, Link]

  type IdentifiedResourceObject = ResourceObject with ResourceIdentifier

  case class JsonApiError(id: Option[String] = None,
                          about: Option[Link] = None,
                          status: Option[String] = None,
                          code: Option[String] = None,
                          title: Option[String] = None,
                          detail: Option[String] = None,
                          source: Option[ErrorSource] = None,
                          meta: Option[JSON] = None)

  case class ErrorSource(pointer: Option[String] = None,
                         parameter: Option[String] = None)

  /**
    * A JsonApiObject. The name seemed more representative.
    */
  case class Version(version: Option[String] = None,
                     meta: Option[JSON] = None)

  case class Link(href: URL, meta: Option[JSON] = None)

  // Resources ============================================
  /**
    * Sealed trait representing the two ways to represent data: single [[Resource]] or a sequence of them as a [[Resources]]
    */
  sealed trait Data

  /**
    * A sealed trait comprised of [[ResourceIdentifier]]s and [[ResourceObject]]s
    */
  sealed trait Resource extends Data

  /**
    * A container for multiple resources. This allows for consistent serialization as a json array.
    * @param resources a sequence of [[Resource]]s
    */
  case class Resources( resources: immutable.Seq[Resource]) extends Data

  trait ResourceIdentifier extends Resource with RelationshipData {
    val id: String
    val tpe: String
    val meta: Option[JSON]
  }

  case class SimpleResourceIdentifier(id: String,
                                      tpe: String,
                                      meta: Option[JSON] = None) extends ResourceIdentifier

  case class ResourceObject(tpe: String,
                            attributes: Option[JSON] = None,
                            relationships: Map[String, Relationship] = Map.empty,
                            links: Links = Map.empty,
                            meta: Option[JSON] = None) extends Resource
  object ResourceObject {

    def apply(id: String,
              tpe: String,
              attributes: Option[JSON],
              relationships: Map[String, Relationship],
              links: Links,
              meta: Option[JSON]): ResourceObject with ResourceIdentifier = {
      val _id = id
      new ResourceObject(tpe, attributes, relationships, links, meta) with ResourceIdentifier {
        override val id: String = _id
      }
    }

    def apply(id: String, obj: ResourceObject): ResourceObject with ResourceIdentifier =
      ResourceObject(
        id = id,
        attributes = obj.attributes,
        relationships = obj.relationships,
        links = obj.links,
        tpe = obj.tpe,
        meta = obj.meta
      )
  }

  // Relationships ========================================
  /**
    * Sealed trait representing the two ways to represent relationship data: single [[ResourceIdentifier]] or a sequence of them as a [[ResourceIdentifiers]]
    */
  sealed trait RelationshipData

  /**
    * A container for multiple resource identifiers. This allows for consistent serialization as a json array.
    * This is intended for use in a [[Relationship]]
    * @param identifiers a sequence of [[ResourceIdentifiers]]s
    */
  case class ResourceIdentifiers( identifiers: immutable.Seq[ResourceIdentifier]) extends RelationshipData

  case class Relationship(links: Links = Map.empty,
                          data: Option[RelationshipData],
                          meta: Option[JSON] = None)

  // Document, top-level objects ==========================
  /**
    * A trait representing top-level json:api documents.
    * The two primary variants are [[DataDocument]] and [[ErrorDocument]].
    */
  sealed trait JsonApiDocument {
    val included: immutable.Seq[ResourceObject]
    val links: Links
    val meta: Option[JSON]
    val version: Option[Version]
  }

  /**
    * A Data document, as opposed to an Error document
    * @param data Either a single [[Resource]] or sequence of [[Resource]]s as a [[Resources]] object
    */
  case class DataDocument(data: Data,

                          override val included: immutable.Seq[ResourceObject] = immutable.Seq.empty,
                          override val links: Links = Map.empty,
                          override val meta: Option[JSON] = None,
                          override val version: Option[Version] = None
                         ) extends JsonApiDocument

  /**
    * An Error document as opposed to a Data document.
    * @param errors a sequence of [[JsonApiError]]s.
    */
  case class ErrorDocument(errors: immutable.Seq[JsonApiError],

                           override val included: immutable.Seq[ResourceObject] = immutable.Seq.empty,
                           override val links: Links = Map.empty,
                           override val meta: Option[JSON] = None,
                           override val version: Option[Version] = None
                          ) extends JsonApiDocument
}
