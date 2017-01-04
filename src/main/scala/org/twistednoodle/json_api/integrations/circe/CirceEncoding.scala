package org.twistednoodle.json_api.integrations.circe

import scala.collection.immutable

import io.circe.generic.semiauto._
import io.circe.syntax._
import io.circe.{Encoder, Json}

// Created by iwaisman on 12/30/16.

trait CirceEncoding { self: CirceApi =>

  implicit val versionEncoder: Encoder[Version] = deriveEncoder

  implicit val resourceIdentifier = Encoder.instance[ResourceIdentifier]{ r =>
    Json.fromFields( ignoreEmpty(
      "id" -> r.id.asJson,
      "type" -> r.tpe.asJson,
      "meta" -> r.meta.asJson
    ))
  }

  implicit val linkEncoder = Encoder.instance[Link]{ l =>
    Json.fromFields(ignoreEmpty(
      "href" -> l.href.toString.asJson,
      "meta" -> l.meta.asJson
    ))
  }
  implicit val linksEncoder = Encoder.instance[Links](l => Json.fromValues(l.map(_.asJson)))

  implicit val errorSourceEncoder: Encoder[ErrorSource] = deriveEncoder
  implicit val errorEncoder: Encoder[JsonApiError] = deriveEncoder


  implicit val relationshipEncoder = Encoder.instance[Relationship] { r =>
    Json.fromFields( ignoreEmpty(
      "data" -> Json.fromValues(r.data.map(_.asJson)),
      "links" -> r.links.asJson,
      "meta" -> r.meta.asJson
    ))
  }

  implicit val resourceObjectEncoder = Encoder.instance[ResourceObject]{ r =>
    val id: Seq[(String, Json)] = r match {
      case rr: ResourceIdentifier => Seq("id" -> rr.id.asJson)
      case _ => Seq.empty
    }
    val members = id ++ Seq(
      "type" -> r.tpe.asJson,
      "attributes" -> r.attributes.asJson,
      "relationships" -> r.relationships.asJson,
      "links" -> r.links.asJson,
      "meta" -> r.meta.asJson
    )

    Json.fromFields( ignoreEmpty( members: _*))
  }

  implicit val resourceEncoder = Encoder.instance[Resource]{
    case r: ResourceObject     => r.asJson
    case r: ResourceIdentifier => r.asJson
  }

  implicit val documentEncoder = Encoder.instance[JsonApiDocument] { document =>
    val head = document match {
      case doc: DataDocument   => "data" -> unwrappedJson( doc.data)
      case doc: ErrorDocument => "errors" -> doc.errors.asJson
    }

    val members = Seq(
      head,
      "included" -> document.included.asJson,
      "links" -> document.links.asJson,
      "meta" -> document.meta.asJson,
      "jsonapi" -> document.version.asJson
    )
    Json.fromFields(ignoreEmpty( members: _*))
  }

  // Utilities --------------------------------------------
  private def ignoreEmpty(coll: (String, Json)*): Seq[(String, Json)] = coll.filter{
    case (_, j: Json) if j.asObject.exists(_.isEmpty) => false
    case (_, j: Json) if j.asArray.exists(_.isEmpty) => false
    case _ => true
  }

  // If the collection has only a single element then unwrap it before conversion to JSON
  private def unwrappedJson[T](coll: immutable.Seq[T])(implicit encoder: Encoder[T]): Json =
    if(coll.size == 1) coll.head.asJson
    else coll.asJson
}
