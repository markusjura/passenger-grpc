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
    log.info(s"Starting server on port $serverPort")
    ServerBuilder
      .forPort(serverPort)
      .addService(bindService(new PassengerService, system.dispatcher))
      .build()
      .start()
    log.info(s"Server has been started on port $serverPort")
    Await.ready(system.whenTerminated, Duration.Inf)
  }
}
