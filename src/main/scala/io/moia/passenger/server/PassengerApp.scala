/*
 * Copyright © 2016-2017 MOIA GmbH All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form
 * or by any means without the express written permission of MOIA GmbH.
 */

package io.moia.passenger.server

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import io.grpc.ServerBuilder
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import io.moia.passenger.PassengerGrpc.bindService
import org.apache.logging.log4j.LogManager

object PassengerApp {

  implicit val log = LogManager.getLogger(getClass())

  private val config     = ConfigFactory.load()
  private val serverPort = config.getInt("passenger.service.port")

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("passenger")
    ServerBuilder
      .forPort(serverPort)
      .addService(bindService(new PassengerService, system.dispatcher))
      .build()
      .start()
    log.info(s"Starting server on port $serverPort")
    Await.ready(system.whenTerminated, Duration.Inf)
  }
}
