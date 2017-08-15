/*
 * Copyright © 2016-2017 MOIA GmbH All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form
 * or by any means without the express written permission of MOIA GmbH.
 */

package io.moia.mobilegateway
package clock

import com.typesafe.config.ConfigFactory
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import ClockGrpc.ClockStub
import org.apache.logging.log4j.LogManager

import scala.concurrent.{ Await, Promise }
import scala.concurrent.duration._

object ClockClientApp {

  implicit val log = LogManager.getLogger(getClass())

  debug("Starting tier-change-manager service...")

  private val config = ConfigFactory.load()
  private val Host   = config.getString("mobile-gateway.service.host")
  private val Port   = config.getInt("mobile-gateway.service.port")

  private val channel = ManagedChannelBuilder.forAddress(Host, Port).usePlaintext(true).build
  private val request = TimeRequest()

  def main(args: Array[String]): Unit = {
    val asyncClockClient: ClockStub = ClockGrpc.stub(channel)
    val streamCompleted             = Promise[Unit]()

    // calling getTime and registering an observer will NOT block the current thread
    val timeResponseObserver = new StreamObserver[TimeResponse] {

      def onNext(value: TimeResponse): Unit = debug(s"received: $value")

      def onError(t: Throwable): Unit = debug(s"error: $t")

      def onCompleted(): Unit = {
        streamCompleted.success(())
        debug("stream completed")
      }

    }
    asyncClockClient.getTime(request, timeResponseObserver)

    // prevents application from shutting down before stream has completed
    Await.ready(streamCompleted.future, Duration.Inf)
  }

}
