import sbtrelease.ReleasePlugin._

organization := "org.scalaz.geo"

name := "scalaz-geo"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.2"

crossScalaVersions := Seq("2.9.2", "2.9.3", "2.10.2")

scalacOptions <++= (scalaVersion) map { sv =>
  val versionDepOpts =
    if (sv startsWith "2.9")
      Seq("-Ydependent-method-types", "-deprecation")
    else
      // does not contain -deprecation (because of ClassManifest)
      // contains -language:postfixOps (because 1+ as a parameter to a higher-order function is treated as a postfix op)
      Seq("-feature", "-language:implicitConversions", "-language:higherKinds", "-language:existentials", "-language:postfixOps")
  Seq("-unchecked") ++ versionDepOpts
}

// https://github.com/sbt/sbt/issues/603
conflictWarning ~= { cw =>
  cw.copy(filter = (id: ModuleID) => true, group = (id: ModuleID) => id.organization + ":" + id.name, level = Level.Error, failOnConflict = true)
}

libraryDependencies <++= (scalaVersion) { sv =>
  val specsVersion = if(sv startsWith "2.9") "1.12.4.1" else "1.12.3"
  Seq(
    "org.scalaz"     %% "scalaz-core"               % "7.0.3",
    "org.specs2"     %% "specs2"                    % specsVersion % "test",
    "org.scalacheck" %% "scalacheck"                % "1.10.0"     % "test",
    "org.typelevel"  %% "scalaz-specs2"             % "0.1.5"      % "test",
    "org.scalaz"     %% "scalaz-scalacheck-binding" % "7.0.3"      % "test"
  )
}

resolvers += Resolver.sonatypeRepo("releases")

publishTo <<= (version).apply { v =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("Snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("Releases" at nexus + "service/local/staging/deploy/maven2")
}

credentials += {
  Seq("build.publish.user", "build.publish.password").map(k => Option(System.getProperty(k))) match {
    case Seq(Some(user), Some(pass)) =>
      Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", user, pass)
    case _ =>
      Credentials(Path.userHome / ".ivy2" / ".credentials")
  }
}

pomIncludeRepository := Function.const(false)

pomExtra := (
  <url>http://typelevel.org/scalaz</url>
  <licenses>
    <license>
      <name>MIT</name>
      <url>http://www.opensource.org/licenses/mit-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/scalaz/scalaz-geo</url>
    <connection>scm:git:git://github.com/scalaz/scalaz-geo.git</connection>
    <developerConnection>scm:git:git@github.com:scalaz/scalaz-geo.git</developerConnection>
  </scm>
  <developers>
    <developer>
      <id>larsrh</id>
      <name>Lars Hupel</name>
      <url>https://github.com/larsrh</url>
    </developer>
    <developer>
      <id>folone</id>
      <name>George Leontiev</name>
      <url>https://github.com/folone</url>
    </developer>
  </developers>
)

// vim: expandtab:ts=2:sw=2
