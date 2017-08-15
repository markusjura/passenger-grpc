/*
 * Copyright © 2016-2017 MOIA GmbH All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form
 * or by any means without the express written permission of MOIA GmbH.
 */

package io.moia

import org.apache.logging.log4j.Logger

package object passenger {

  type Traversable[+A] = scala.collection.immutable.Traversable[A]
  type Iterable[+A]    = scala.collection.immutable.Iterable[A]
  type Seq[+A]         = scala.collection.immutable.Seq[A]
  type IndexedSeq[+A]  = scala.collection.immutable.IndexedSeq[A]

  /**
    * Helper method to avoid the need to manually check `if (log.isDebugEnabled)` every time.
    *
    * @param msg The message to be logged
    * @param log The logging adapter instance
    */
  def debug(msg: => String)(implicit log: Logger): Unit =
    if (log.isDebugEnabled) log.debug(msg)

}
