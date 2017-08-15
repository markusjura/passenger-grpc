/*
 * Copyright © 2016-2017 MOIA GmbH All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form
 * or by any means without the express written permission of MOIA GmbH.
 */

package io.moia.mobilegateway
package clock

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{ Executors, TimeUnit }

import io.grpc.stub.StreamObserver

object ClockService {
  val InitialDelayMs = 0L
  val IntervalMs     = 1000L
}

class ClockService(repeatForSeconds: Int) extends ClockGrpc.Clock {

  /**
    * Returns the current time in milliseconds every second for `repeatForSeconds` times.
    *
    * @param request does not contain any initial data
    * @param responseObserver is used to handle the connection and to return the responses
    */
  def getTime(request: TimeRequest, responseObserver: StreamObserver[TimeResponse]): Unit = {
    val scheduler = Executors.newSingleThreadScheduledExecutor()
    val tick = new Runnable {
      val counter = new AtomicInteger(repeatForSeconds)
      def run() =
        if (counter.getAndDecrement() >= 0) {
          val currentTime = System.currentTimeMillis()
          responseObserver.onNext(TimeResponse(currentTime))
        } else {
          scheduler.shutdown()
          responseObserver.onCompleted()
        }
    }

    import ClockService._
    scheduler.scheduleAtFixedRate(tick, InitialDelayMs, IntervalMs, TimeUnit.MILLISECONDS)
  }
}
