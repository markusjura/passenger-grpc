/*
 * Copyright © 2016-2017 MOIA GmbH All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form
 * or by any means without the express written permission of MOIA GmbH.
 */

package io.moia.passenger.server

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{Executors, TimeUnit}

import io.grpc.stub.StreamObserver
import io.moia.passenger.{BookingRequest, BookingResponse, Location, LocationRequest}
import io.moia.passenger.PassengerGrpc.Passenger
import org.apache.logging.log4j.LogManager

import scala.concurrent.Future

object PassengerService {
  val InitialDelayMs = 0L
  val IntervalMs     = 1000L
  val RepeatForSeconds = 30
}

class PassengerService extends Passenger {

  import PassengerService._

  implicit val log = LogManager.getLogger(getClass())

  override def bookTrip(request: BookingRequest): Future[BookingResponse] = {
    Future.successful(BookingResponse("markusjura", BookingResponse.Status.OK))
  }

  override def trackVehicle(request: LocationRequest, responseObserver: StreamObserver[Location]): Unit = {
    val scheduler = Executors.newSingleThreadScheduledExecutor()
    val tick = new Runnable {
      val counter = new AtomicInteger(RepeatForSeconds)
      def run() =
        if (counter.getAndDecrement() >= 0) {
          val currentLocation = Location(1.0, 1.0)
          log.info(s"Send current location: $currentLocation")
          responseObserver.onNext(currentLocation)
        } else {
          scheduler.shutdown()
          responseObserver.onCompleted()
        }
    }

    scheduler.scheduleAtFixedRate(tick, InitialDelayMs, IntervalMs, TimeUnit.MILLISECONDS)
  }
}
