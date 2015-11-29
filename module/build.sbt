name := """play-rds"""

version := "0.1.0"

scalaVersion := "2.11.7"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

//*******************************
// Maven settings
//*******************************

sonatypeSettings

publishMavenStyle := true

organization := "com.enalmada"

description := "This is a collection of helpers to restore AWS RDS database."

homepage := Some(url("https://github.com/Enalmada/play-rds"))

licenses := Seq("Apache License" -> url("https://github.com/Enalmada/play-rds/blob/master/LICENSE"))

startYear := Some(2015)

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <scm>
    <url>git@github.com:enalmada/play-rds.git</url>
    <connection>scm:git:git@github.com:enalmada/play-rds.git</connection>
  </scm>
  <developers>
    <developer>
      <id>enalmada</id>
      <name>Adam Lane</name>
      <url>https://github.com/enalmada</url>
    </developer>
  </developers>
)

credentials += Credentials(Path.userHome / ".sbt" / "sonatype.credentials")
