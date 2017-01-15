package org.twistednoodle.json_api.integrations.circe

import java.net.URL

import scala.collection.immutable
import scala.util.Try

import io.circe.Decoder
import io.circe.generic.semiauto._

// Created by iwaisman on 12/30/16.

trait CirceDecoding { self: CirceApi =>

  private val idDecoder: Decoder[Option[String]] = Decoder.instance(_.downField("id").as[Option[String]])
  private val metaDecoder: Decoder[Option[JSON]] = Decoder.instance(_.downField("meta").as[Option[JSON]])

  implicit val versionDecoder: Decoder[Version] = deriveDecoder

  implicit val errorSourceDecoder: Decoder[ErrorSource] = deriveDecoder

  implicit val errorDecoder: Decoder[JsonApiError] = Decoder.instance[JsonApiError] { h =>
    for {
      id <- idDecoder(h)
      link <- h.downField("links").as[Option[Link]]
      status <- h.downField("status").as[Option[String]]
      code <- h.downField("code").as[Option[String]]
      title <- h.downField("title").as[Option[String]]
      detail <- h.downField("detail").as[Option[String]]
      source <- h.downField("source").as[Option[ErrorSource]]
      meta <- metaDecoder(h)
    } yield JsonApiError(
      id, link, status, code, title, detail, source, meta
    )
  }

  implicit val urlDecoder = Decoder.decodeString.emap(s => Try{ new URL(s) }.toEither.swap.map(_.getMessage).swap)
  implicit val linkDecoder: Decoder[Link] = Decoder.instance[Link]{ h =>
    for {
      href <- h.downField("href").as[URL]
      meta <- metaDecoder(h)
    } yield Link( href, meta)
  }


  implicit val resourceIdentifierDecoder =
    Decoder.instance[ResourceIdentifier]( h =>
                                            for{
                                              id <- h.downField("id").as[String]
                                              tpe <- h.downField("type").as[String]
                                              meta <- metaDecoder(h)
                                            } yield SimpleResourceIdentifier( id, tpe, meta)
    )

  implicit val relationshipDecoder = Decoder.instance[Relationship]{ h =>
    for{
      ls <- h.downField("links").as[Option[Map[String, Link]]]
      d <- h.downField("data").as[Option[immutable.Seq[ResourceIdentifier]]]
      meta <- metaDecoder(h)
    } yield Relationship(
      ls getOrElse Map.empty,
      d getOrElse Nil,
      meta
    )
  }

  implicit val resourceObjectDecoder: Decoder[ResourceObject] = Decoder.instance[ResourceObject]{ h =>
    for{
      id <- idDecoder(h)
      tpe <- h.downField("type").as[String]
      attributes <- h.downField("attributes").as[Option[JSON]]
      rels <- h.downField("relationships").as[Option[Map[String, Relationship]]]
      ls <- h.downField("links").as[Option[Links]]
      meta <- metaDecoder(h)
    } yield {
      val relationships = rels getOrElse Map.empty
      val links = ls getOrElse Map.empty
      val ro = ResourceObject( tpe, attributes, relationships, links, meta)

      id.map( ResourceObject(_, ro)) getOrElse ro
    }
  }

  implicit def arrayishDecoder[T](implicit decoder: Decoder[T]): Decoder[immutable.Seq[T]] =
    Decoder.decodeList[T] or decoder.map(List(_))

  private val dataDecoder = Decoder.instance[immutable.Seq[ResourceObject]](_.as[immutable.Seq[ResourceObject]])
  private val errorsDecoder = Decoder.instance[immutable.Seq[JsonApiError]](_.as[immutable.Seq[JsonApiError]])
  private val dataOrErrorsDecoder = Decoder.decodeEither("data", "errors")(dataDecoder, errorsDecoder)

  implicit val documentDecoder: Decoder[JsonApiDocument] = Decoder.instance{ h =>
    for{
      dataOrErrors <- dataOrErrorsDecoder(h)
      included <- h.downField("included").as[Option[immutable.Seq[ResourceObject]]]
      links <- h.downField("links").as[Option[Links]]
      meta <- metaDecoder(h)
      version <- h.downField("jsonapi").as[Option[Version]]
    } yield {
      val inc = included getOrElse Nil
      val ls = links getOrElse Map.empty
      dataOrErrors match {
        case Left(data) => DataDocument( Resources(data), inc, ls, meta, version)
        case Right(errors) => ErrorDocument( errors, inc, ls, meta, version)
      }
    }
  }
}
