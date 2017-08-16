// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `passenger-server-grpc` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .settings(settings)
    .settings(
      resolvers += resolver.markusjura,
      libraryDependencies ++= Seq(
        library.akkaStream,
        library.grpcNetty,
        library.log4jApi,
        library.log4jCore,
        library.log4jSlf4jImpl,
        library.passengerProtoScala,
        library.typesafeConfig,
        library.mockito    % Test,
        library.scalaCheck % Test,
        library.scalaTest  % Test
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val resolver =
  new {
    val markusjura = sbt.Resolver.bintrayRepo("markusjura", "maven")
  }

lazy val library =
  new {
    object Version {
      val akka           = "2.5.4"
      val grpcJava       = "1.4.0"
      val log4j          = "2.8.2"
      val mockito        = "2.8.47"
      val passengerProto = "1.0.0"
      val scalaCheck     = "1.13.5"
      val scalaPb        = com.trueaccord.scalapb.compiler.Version.scalapbVersion
      val scalaTest      = "3.0.3"
      val typesafeConfig = "1.3.1"
    }
    val akkaStream          = "com.typesafe.akka"        %% "akka-stream"           % Version.akka
    val grpcNetty           = "io.grpc"                  %  "grpc-netty"            % Version.grpcJava
    val log4jApi            = "org.apache.logging.log4j" %  "log4j-api"             % Version.log4j
    val log4jCore           = "org.apache.logging.log4j" %  "log4j-core"            % Version.log4j
    val log4jSlf4jImpl      = "org.apache.logging.log4j" %  "log4j-slf4j-impl"      % Version.log4j
    val mockito             = "org.mockito"              %  "mockito-core"          % Version.mockito
    val passengerProtoScala = "io.moia"                  %% "passenger-proto-scala" % Version.passengerProto
    val scalaCheck          = "org.scalacheck"           %% "scalacheck"            % Version.scalaCheck
    val scalaPbRuntime      = "com.trueaccord.scalapb"   %% "scalapb-runtime"       % Version.scalaPb % "protobuf"
    val scalaPbRuntimeGrpc  = "com.trueaccord.scalapb"   %% "scalapb-runtime-grpc"  % Version.scalaPb
    val scalaTest           = "org.scalatest"            %% "scalatest"             % Version.scalaTest
    val typesafeConfig      = "com.typesafe"             %  "config"                % Version.typesafeConfig
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
  headerSettings

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

import de.heikoseeberger.sbtheader.license.Apache2_0
lazy val headerSettings =
  Seq(
    headers := Map(
      "conf" -> Apache2_0("2017", "MOIA GmbH"),
      "scala" -> Apache2_0("2017", "MOIA GmbH")
    )
  )

// *****************************************************************************
// Aliases
// *****************************************************************************

addCommandAlias("cstyle", ";compile;scalastyle")
addCommandAlias("ccstyle", ";clean;compile;scalastyle")
