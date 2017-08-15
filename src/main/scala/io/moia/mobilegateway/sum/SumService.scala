/*
 * Copyright © 2016-2017 MOIA GmbH All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form
 * or by any means without the express written permission of MOIA GmbH.
 */

package io.moia.mobilegateway
package sum

import scala.concurrent.Future

protected class SumService extends SumGrpc.Sum {

  /**
    * Returns the sum of two numbers passed in the `SumRequest`.
    *
    * @param request containing two numbers to be summed up
    * @return `SumResponse` containing the sum of the two given numbers
    */
  def calcSum(request: SumRequest): Future[SumResponse] =
    Future.successful(SumResponse(request.a + request.b))
}
