name := """play-rds-sample"""

version := "0.1.0"

scalaVersion := "2.11.7"

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)


resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
	"com.enalmada" %% "play-rds" % "0.1.0-SNAPSHOT",
	"org.webjars" %% "webjars-play" % "2.4.0-1",
	"org.webjars" % "bootswatch-superhero" % "3.3.5"  // Bootstrap and jquery come with it
)


