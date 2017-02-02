package org.twistednoodle.json_api.integrations.circe

import scala.collection.immutable

import io.circe.generic.semiauto._
import io.circe.syntax._
import io.circe._

// Created by iwaisman on 12/30/16.

trait CirceEncoding { self: CirceApi =>

  implicit val versionEncoder: Encoder[Version] = deriveEncoder

  implicit val resourceIdentifier: Encoder[ResourceIdentifier] = Encoder.instance[ResourceIdentifier]{ r =>
    Json.fromFields( ignoreEmpty(
      "id" -> r.id.asJson,
      "type" -> r.tpe.asJson,
      "meta" -> r.meta.asJson
    ))
  }

  implicit val linkEncoder: Encoder[Link] = Encoder.instance[Link]{ l =>
    Json.fromFields(ignoreEmpty(
      "href" -> l.href.toString.asJson,
      "meta" -> l.meta.asJson
    ))
  }

  implicit val linksEncoder: Encoder[Links] =
    Encoder.instance[Links](l => Json.fromJsonObject(JsonObject.fromMap(l.mapValues(_.asJson))))

  implicit val errorSourceEncoder: Encoder[ErrorSource] = deriveEncoder
  implicit val errorEncoder: Encoder[JsonApiError] = deriveEncoder


  implicit val relationshipDataEncoder: Encoder[RelationshipData] = Encoder.instance[RelationshipData]{
    case r: ResourceIdentifier => resourceIdentifier(r)
    case ResourceIdentifiers(rs) => Json.fromValues(rs.map(resourceIdentifier.apply))
  }

  implicit val relationshipEncoder: Encoder[Relationship] = Encoder.instance[Relationship] { r =>
    val _data = r.data.map(d => "data" -> relationshipDataEncoder(d)).toList ++ ignoreEmpty(
      "links" -> r.links.asJson,
      "meta" -> r.meta.asJson
    )
    Json.fromFields( _data)
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

  implicit val resourceEncoder: Encoder[Resource] = Encoder.instance[Resource]{
    case r: ResourceObject     => r.asJson
    case r: ResourceIdentifier => r.asJson
  }

  implicit val dataEncoder = Encoder.instance[Data]{
    case r: Resource => resourceEncoder(r)
    case Resources(rs) => Json.fromValues(rs.map(resourceEncoder.apply))
  }

  implicit val documentEncoder = Encoder.instance[JsonApiDocument] { document =>
    val head = document match {
      case doc: DataDocument   => "data" -> doc.data.asJson
      case doc: ErrorDocument => "errors" -> doc.errors.asJson
    }

    val members = Seq(
      "included" -> document.included.asJson,
      "links" -> document.links.asJson,
      "meta" -> document.meta.asJson,
      "jsonapi" -> document.version.asJson
    )
    val nonEmpty = head +: ignoreEmpty( members: _*)
    Json.fromFields(nonEmpty)
  }

  // Utilities --------------------------------------------
  private def ignoreEmpty(coll: (String, Json)*): Seq[(String, Json)] = coll.filter{
    case (_, j: Json) if j.asObject.exists(_.isEmpty) => false
    case (_, j: Json) if j.asArray.exists(_.isEmpty) => false
    case _ => true
  }
}
