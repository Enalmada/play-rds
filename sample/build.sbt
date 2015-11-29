name := """play-rds-sample"""

version := "0.1.0"

scalaVersion := "2.11.7"

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)


resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
	"com.enalmada" %% "play-rds" % "0.1.0-SNAPSHOT",
	"org.webjars" % "bootstrap" % "3.3.5"
)


