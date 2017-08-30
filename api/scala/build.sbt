// *****************************************************************************
// Projects
// *****************************************************************************

version := IO.read(file("../proto/version"))

lazy val `passenger-proto-scala` =
  project
    .in(file("."))
    .settings(settings)

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val scalaPb        = com.trueaccord.scalapb.compiler.Version.scalapbVersion
    }
    val scalaPbRuntime     = "com.trueaccord.scalapb"   %% "scalapb-runtime"      % Version.scalaPb % "protobuf"
    val scalaPbRuntimeGrpc = "com.trueaccord.scalapb"   %% "scalapb-runtime-grpc" % Version.scalaPb
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  bintraySettings ++
  commonSettings ++
  pbSettings

lazy val commonSettings =
  Seq(
    scalaVersion := "2.12.3",
    crossScalaVersions := Seq(scalaVersion.value, "2.11.8"),
    organization := "io.moia",
    licenses := Seq(
      "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
    ),
    mappings.in(Compile, packageBin) +=
      baseDirectory.in(ThisBuild).value / "LICENSE" -> "LICENSE",
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "UTF-8",
      "-Ywarn-unused-import"
    ),
    javacOptions ++= Seq(
      "-source", "1.8",
      "-target", "1.8"
    ),
    unmanagedSourceDirectories.in(Compile) := Seq(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) := Seq(scalaSource.in(Test).value)
  )

lazy val pbSettings =
  Seq(
    PB.protoSources.in(Compile) := Seq(file("..") / "proto"),
    PB.targets.in(Compile) := Seq(scalapb.gen() -> sourceManaged.in(Compile).value),
    libraryDependencies ++= Seq(
      library.scalaPbRuntime,
      library.scalaPbRuntimeGrpc
    )
  )

lazy val bintraySettings =
  Seq(
    bintrayOrganization := Some("markusjura")
  )
