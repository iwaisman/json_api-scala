# JSON:API for Scala

Current Version: **0.0.7**

This is a small library providing a memory model for [{json:api}](http://jsonapi.org/).

This was inspired by [scala-jsonapi](https://github.com/zalando/scala-jsonapi).
A desire for a wee bit more type safety and
an ability to use the integrated JSON library's representation
for the free-form JSON making up _attributes_ and _meta_
was the impetus to make a separate implementation.

It is currently in a sufficiently usable state for me and
I will likely only be making changes as I personally find necessary.
However, I am happy to receive suggestions or contributions!

##### Supported Integrations:
* [Circe](https://circe.github.io/circe/)

## Setup - SBT
json_api-scala is currently being published via [jitpack.io](https://jitpack.io)
As such, you must add a jitpack resolver:

    "jitpack" at "https://jitpack.io"
and then the following lib:

    "com.github.iwaisman" % "json_api-scala" % "0.0.7"

## Usage
Currently, the only integration available is for [Circe](https://circe.github.io/circe/). Therefore, the relevant import is:

    import org.twistednoodle.json_api.integrations.circe._

Use of various useful instances can be achieved with:

    import org.twistednoodle.json_api.integrations.circe.instances.akka._
    import org.twistednoodle.json_api.integrations.circe.instances.scala._

### Generating json:api
The simplest approach is to implement ToJsonApiDocument[T] for your type, T. Something like:

    import java.time.Instant
    import io.circe.syntax._

    case class TotallySweetThing(...)

    implicit val toJsonApiDoc: ToJsonApiDocument[TotallySweetThing] =
        new ToJsonApiDocument[TotallySweetThing] {
            override def from(tst: TotallySweetThing): JsonApiDocument = {
                val rsc = ResourceObject(
                    tpe = ???,
                    attributes = ??? //perhaps just "tst.asJson"
                )
                DataDocument(
                    data = rsc,
                    meta = JsonObject.fromMap( Map( "timestamp" -> Instant.now.toString.asJson))
                )
            }
        }

How you turn your TotallySweetThing into a ResourceObject is up to you, but note how
the attributes value is just a _io.circe.JsonObject_ and so can take advantage of
existing Encoders. Similarly, the value for meta is a _io.circe.JsonObject_.

With the above you can explicitly convert to a document,

    val tst = TotallySweetThing(...)
    val doc = ToJsonApiDocument.from( tst)

and make use of the Circe Decoder instances to generate json.

### Licence
This library is licensed under the [MIT](https://opensource.org/licenses/MIT) licence.