package org.twistednoodle.json_api.integrations.circe

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.unmarshalling.Unmarshaller
import io.circe.syntax._
import io.circe.{Json, Printer, parser}

// Created by iwaisman on 12/30/16.

trait AkkaInstances { self: CirceApi =>

  private[circe] object __AkkaInstances {

    // Printers
    final val noSpacesNoNulls = Printer.noSpaces.copy(dropNullKeys = true)
    final val spaces4NoNulls = Printer.spaces4.copy(dropNullKeys = true)

    implicit def circeDocumentMarshaller(implicit printer: Json => String = noSpacesNoNulls.pretty): ToEntityMarshaller[JsonApiDocument] =
      Marshaller.StringMarshaller.wrap(`application/vnd.api+json`)(doc => printer(doc.asJson))

    implicit val circeDocumentUnmarshaller = Unmarshaller.
      stringUnmarshaller.
      forContentTypes(`application/vnd.api+json`, `application/json`).
      map(parser.decode[JsonApiDocument])
  }
}
