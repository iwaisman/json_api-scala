organization := "org.twistednoodle"

name := "json_api-scala"

scalaVersion := "2.12.1"


scalacOptions ++= Seq(
"-target:jvm-1.8",
"-encoding", "utf8", 

"-feature", 
"-deprecation", 
"-explaintypes", 

"-Ywarn-dead-code",
"-Ywarn-unused",

"-language:implicitConversions", 
"-language:postfixOps", 
"-language:higherKinds", 
"-language:existentials"
)

resolvers ++= Seq(
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
)


libraryDependencies ++= {
  val circe_version = "0.7+"
  val akka_version = "2.4.16"
  val akka_http_version = "10.0.1"

  Seq(
    "com.typesafe.akka" %% "akka-actor"             % akka_version % "provided",
    "com.typesafe.akka" %% "akka-http-core"         % akka_http_version % "provided",
    "com.typesafe.akka" %% "akka-http"              % akka_http_version % "provided",
    "io.circe"          %% "circe-core"             % circe_version % "provided",
    "io.circe"          %% "circe-generic"          % circe_version % "provided",
    "io.circe"          %% "circe-parser"           % circe_version % "provided"
  )
}

lazy val root = project in file(".")
