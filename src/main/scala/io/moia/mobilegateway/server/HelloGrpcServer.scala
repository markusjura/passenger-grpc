/*
 * Copyright © 2016-2017 MOIA GmbH All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form
 * or by any means without the express written permission of MOIA GmbH.
 */

package io.moia.mobilegateway.server

import com.typesafe.config.ConfigFactory
import io.grpc.{ Server, ServerBuilder, ServerServiceDefinition }
import org.apache.logging.log4j.LogManager

class HelloGrpcServer(serverServiceDefinition: ServerServiceDefinition) {

  implicit val log = LogManager.getLogger(getClass())

  private val config     = ConfigFactory.load()
  private val serverPort = config.getInt("mobile-gateway.service.port")

  private var server: Option[Server] = None

  def start(): Unit = {
    log.info("Attempting to start server..")
    server = Option(
      ServerBuilder
        .forPort(serverPort)
        .addService(serverServiceDefinition)
        .build()
        .start()
    )
    log.info(s"Server started. Listening on port $serverPort")

    // make sure our server is stopped when jvm is shut down
    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = stopServer()
    })
  }

  private def stopServer(): Unit = server.foreach(_.shutdown())

  def blockUntilShutdown(): Unit =
    server.foreach(_.awaitTermination())

}
