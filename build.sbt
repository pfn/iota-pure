enablePlugins(AndroidJar)

scalaVersion in Global := "2.11.7"

crossScalaVersions += "2.10.6"

javacOptions in Global ++= "-target" :: "1.7" :: "-source" :: "1.7" :: Nil

libraryDependencies ++= "com.hanhuy.android" %% "iota" % "2.0.0-RC3" ::
  "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided" ::
  "com.android.support" % "support-v4" % "23.1.1" % "provided" ::
  Nil

name := "iota-pure"

organization := "com.hanhuy.android"

sonatypeProfileName := "com.hanhuy"

version := "0.2"

platformTarget in Android := "android-25"

// sonatype publishing options follow
publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomIncludeRepository := { _ => false }

pomExtra :=
  <scm>
    <url>git@github.com:pfn/iota-pure.git</url>
    <connection>scm:git:git@github.com:pfn/iota-pure.git</connection>
  </scm>
  <developers>
    <developer>
      <id>pfnguyen</id>
      <name>Perry Nguyen</name>
      <url>https://github.com/pfn</url>
    </developer>
  </developers>

licenses := Seq("BSD-style" -> url("http://www.opensource.org/licenses/bsd-license.php"))

homepage := Some(url("https://github.com/pfn/iota-pure"))
