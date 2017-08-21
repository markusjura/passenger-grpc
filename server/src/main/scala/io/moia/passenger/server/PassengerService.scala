/*
 * Copyright 2017 MOIA GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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