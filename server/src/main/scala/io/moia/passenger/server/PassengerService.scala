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
import akka.stream.scaladsl.{Flow, Source}
import akka.stream.{ActorMaterializer, ThrottleMode}
import io.grpc.stub.StreamObserver
import io.moia.passenger._
import org.apache.logging.log4j.LogManager

import scala.concurrent.Future
import scala.concurrent.duration._

object PassengerService {

  val VehicleLocations = List(
    Location(Some(53.557471), Some(10.003938)),
    Location(Some(53.557318), Some(10.001857)),
    Location(Some(53.557344), Some(9.999733)),
    Location(Some(53.557866), Some(9.997544)),
    Location(Some(53.558351), Some(9.995613)),
    Location(Some(53.558708), Some(9.994347)),
    Location(Some(53.559065), Some(9.995205)),
    Location(Some(53.559740), Some(9.995935)),
    Location(Some(53.560938), Some(9.996707)),
    Location(Some(53.561970), Some(9.997351))
  )
}

/**
  * Passenger service that implements the generated [[PassengerGrpc.Passenger]] service endpoints.
  */
class PassengerService(implicit system: ActorSystem) extends PassengerGrpc.Passenger {

  import PassengerService._
  import system.dispatcher
  import RequestObserver.toCallStreamObserver

  private implicit val mat = ActorMaterializer()
  private implicit val log = LogManager.getLogger(getClass)

  /**
    * Trip booking endpoint (request-response).
    * Response with the user ID from the request and an OK status.
    */
  override def bookTrip(request: BookingRequest): Future[BookingResponse] =
    Future.successful(BookingResponse(request.userId, BookingResponse.Status.OK))

  /**
    * Track vehicle endpoint (server-push).
    * Once the endpoint is called, each second the current vehicle location is sent back
    */
  override def trackVehicle(request: LocationRequest, responseObserver: StreamObserver[Location]): Unit =
    Source(VehicleLocations)
      .throttle(1, 1.second, 1, ThrottleMode.Shaping)
      .runForeach(responseObserver.onNext)
      .foreach(_ => responseObserver.onCompleted())

  /**
    * Echo endpoint (bi-directional-streaming)
    * Once a client sends a ping, the server responds with a pong message.
    */
  override def echo(responseObserver: StreamObserver[Pong]): StreamObserver[Ping] = {
    val handler =
        Flow[Ping]
          .throttle(1, 1.second, 1, ThrottleMode.shaping)
          .map(ping => Pong(ping.message.map(msg => s"Server response to message: $msg")))
    RequestObserver(handler, responseObserver)
  }
}
