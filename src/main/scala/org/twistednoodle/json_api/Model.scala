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

  case class Relationship(links: Links = Map.empty,
                          data: immutable.Seq[ResourceIdentifier],
                          meta: Option[JSON] = None)

  // Resources ============================================
  sealed trait Resource

  trait ResourceIdentifier extends Resource {
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

  // Document, top-level objects ==========================
  /**
    * A trait representing top-level json:api documents.
    * The two primary variants are Data and Error.
    */
  sealed trait JsonApiDocument {
    val included: immutable.Seq[ResourceObject]
    val links: Links
    val meta: Option[JSON]
    val version: Option[Version]
  }
  case class DataDocument(data: immutable.Seq[Resource],

                          override val included: immutable.Seq[ResourceObject] = immutable.Seq.empty,
                          override val links: Links = Map.empty,
                          override val meta: Option[JSON] = None,
                          override val version: Option[Version] = None
                         ) extends JsonApiDocument

  case class ErrorDocument(errors: immutable.Seq[JsonApiError],

                           override val included: immutable.Seq[ResourceObject] = immutable.Seq.empty,
                           override val links: Links = Map.empty,
                           override val meta: Option[JSON] = None,
                           override val version: Option[Version] = None
                          ) extends JsonApiDocument


}
