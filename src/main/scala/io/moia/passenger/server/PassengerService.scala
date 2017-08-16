/*
 * Copyright © 2016-2017 MOIA GmbH All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form
 * or by any means without the express written permission of MOIA GmbH.
 */

package io.moia.passenger.server

import akka.actor.ActorSystem
import akka.stream.scaladsl.Flow
import akka.stream.{ ActorMaterializer, ThrottleMode }
import io.grpc.stub.StreamObserver
import io.moia.passenger.{BookingRequest, BookingResponse, Location, LocationRequest}
import io.moia.passenger.PassengerGrpc
import org.apache.logging.log4j.LogManager

import scala.concurrent.Future
import scala.concurrent.duration._

class PassengerService(implicit system: ActorSystem) extends PassengerGrpc.Passenger {

  import system.dispatcher

  private implicit val mat = ActorMaterializer()
  private implicit val log = LogManager.getLogger(getClass())

  override def bookTrip(request: BookingRequest): Future[BookingResponse] = {
    Future.successful(BookingResponse(request.userId, BookingResponse.Status.OK))
  }

  override def trackVehicle(request: LocationRequest, responseObserver: StreamObserver[Location]): Unit = {
    val handler =
      Flow[BookingRequest]
        .throttle(1, 3 seconds, 1, ThrottleMode.shaping)
        .map {
          _ =>
            val currentLocation = Location(Some(1.0), Some(1.0))
            log.info(s"Send current location: $currentLocation")
            currentLocation
        }
    RequestObserver(handler, responseObserver)
  }
}
