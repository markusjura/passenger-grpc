import de.heikoseeberger.sbtheader.HeaderPattern

// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `mobile-gateway` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .settings(settings)
    .settings(
      libraryDependencies ++= Seq(
        library.log4jApi,
        library.log4jCore,
        library.log4jSlf4jImpl,
        library.typesafeConfig,
        library.mockito    % Test,
        library.scalaCheck % Test,
        library.scalaTest  % Test
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val grpcJava       = "1.4.0"
      val log4j          = "2.8.2"
      val mockito        = "2.8.47"
      val scalaCheck     = "1.13.5"
      val scalaTest      = "3.0.3"
      val typesafeConfig = "1.3.1"
    }
    val grpcNetty      = "io.grpc"                  %  "grpc-netty"       % Version.grpcJava
    val log4jApi       = "org.apache.logging.log4j" %  "log4j-api"        % Version.log4j
    val log4jCore      = "org.apache.logging.log4j" %  "log4j-core"       % Version.log4j
    val log4jSlf4jImpl = "org.apache.logging.log4j" %  "log4j-slf4j-impl" % Version.log4j
    val mockito        = "org.mockito"              %  "mockito-core"     % Version.mockito
    val scalaCheck     = "org.scalacheck"           %% "scalacheck"       % Version.scalaCheck
    val scalaTest      = "org.scalatest"            %% "scalatest"        % Version.scalaTest
    val typesafeConfig = "com.typesafe"             %  "config"           % Version.typesafeConfig
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
  headerSettings ++
  pbSettings

lazy val commonSettings =
  Seq(
    scalaVersion := "2.12.3",
    crossScalaVersions := Seq(scalaVersion.value, "2.11.8"),
    organization := "io.moia",
    licenses := Seq("MOIA TBD" -> url("http://moia.io")),
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

lazy val headerSettings =
  Seq(
    headers := Map(
      "scala" -> (
        HeaderPattern.cStyleBlockComment,
        """|/*
           | * Copyright © 2016-2017 MOIA GmbH All rights reserved.
           | * No information contained herein may be reproduced or transmitted in any form
           | * or by any means without the express written permission of MOIA GmbH.
           | */
           |
           |""".stripMargin
      ),
      "proto" -> (
        HeaderPattern.cppStyleLineComment,
        """// Copyright © 2016-2017 MOIA GmbH All rights reserved.
          |// No information contained herein may be reproduced or transmitted in any form
          |// or by any means without the express written permission of MOIA GmbH.
          |
          |""".stripMargin
      ),
      "conf" -> (
        HeaderPattern.hashLineComment,
        """|# Copyright © 2016-2017 MOIA GmbH All rights reserved.
           |# No information contained herein may be reproduced or transmitted in any form
           |# or by any means without the express written permission of MOIA GmbH.
           |
           |""".stripMargin
      )
    )
  )

import com.trueaccord.scalapb.compiler.Version.scalapbVersion
lazy val pbSettings =
  Seq(
    PB.protoSources.in(Compile) := Seq(sourceDirectory.in(Compile).value / "proto"),
    PB.targets.in(Compile) := Seq(scalapb.gen() -> sourceManaged.in(Compile).value),
    libraryDependencies ++= Seq(
      "com.trueaccord.scalapb" %% "scalapb-runtime"      % scalapbVersion % "protobuf",
      "com.trueaccord.scalapb" %% "scalapb-runtime-grpc" % scalapbVersion,
      library.grpcNetty
    )
  )

// *****************************************************************************
// Aliases
// *****************************************************************************

addCommandAlias("cstyle", ";compile;scalastyle")
addCommandAlias("ccstyle", ";clean;compile;scalastyle")
