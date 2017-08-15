/*
 * Copyright © 2016-2017 MOIA GmbH All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form
 * or by any means without the express written permission of MOIA GmbH.
 */

package io.moia.mobilegateway
package clock

import server.HelloGrpcServer

import scala.concurrent.ExecutionContext

object ClockServerApp {

  val RepeatForSeconds = 10

  def main(args: Array[String]): Unit = {
    val serverServiceDefinition = ClockGrpc.bindService(
      new ClockService(RepeatForSeconds),
      ExecutionContext.global
    )
    val server = new HelloGrpcServer(serverServiceDefinition)
    server.start()
    server.blockUntilShutdown()
  }

}
