name := "tlbot"
 
version := "1.0" 
      
lazy val `tlbot` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )
// https://mvnrepository.com/artifact/com.typesafe.play/play-json-joda
libraryDependencies += "com.typesafe.play" %% "play-json-joda" % "2.6.0-RC1"
libraryDependencies += "com.google.cloud" % "google-cloud-translate" % "1.12.0"


      