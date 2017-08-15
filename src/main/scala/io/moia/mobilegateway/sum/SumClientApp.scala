/*
 * Copyright © 2016-2017 MOIA GmbH All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form
 * or by any means without the express written permission of MOIA GmbH.
 */

package io.moia.mobilegateway
package sum

import com.typesafe.config.ConfigFactory
import io.grpc.ManagedChannelBuilder
import SumGrpc.SumStub
import org.apache.logging.log4j.LogManager

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{ Failure, Success }

object SumClientApp {

  implicit val log = LogManager.getLogger(getClass())

  private val config = ConfigFactory.load()
  private val Host   = config.getString("mobile-gateway.service.host")
  private val Port   = config.getInt("mobile-gateway.service.port")

  private val channel = ManagedChannelBuilder.forAddress(Host, Port).usePlaintext(true).build()
  // scalastyle:off magic.number
  private val request = SumRequest(3, 4)
  // scalastyle:on magic.number

  def main(args: Array[String]): Unit = {
    val asyncSumClient: SumStub               = SumGrpc.stub(channel)
    val asyncSumResponse: Future[SumResponse] = asyncSumClient.calcSum(request)
    asyncSumResponse.onComplete {
      case Success(sumResponse) => debug(s"received response: $sumResponse")
      case Failure(e)           => debug(s"error while calling sum service: $e")
    }
  }

}
