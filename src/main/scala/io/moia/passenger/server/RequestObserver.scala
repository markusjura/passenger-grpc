/*
 * Copyright © 2016-2017 MOIA GmbH All rights reserved.
 * No information contained herein may be reproduced or transmitted in any form
 * or by any means without the express written permission of MOIA GmbH.
 */

package io.moia.passenger.server

import akka.stream.QueueOfferResult.{
Dropped,
Enqueued,
QueueClosed,
Failure => OfferFailure
}
import akka.stream.scaladsl.{ Flow, Sink, Source, SourceQueueWithComplete }
import akka.stream.{ Materializer, OverflowStrategy }
import io.grpc.stub.{CallStreamObserver, StreamObserver}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
  * Bridge between server-side gRPC and Akka Streams. Executes back-pressure
  * onto the gRPC client.
  */
object RequestObserver {

  /**
    * Implicitly converts a stream observer for responses to a more specific
    * `CallStreamObserver` simply by downcasting. This seems to be safe, as gRPC
    * seems to use the more specific `CallStreamObserver` when providing stream
    * observer for responses.
    *
    * @param responseObserver stream observer for responses provided by gRPC
    * @tparam A response type
    * @return stream observer for responses downcasted to `CallStreamObserver[A]`
    */
  private implicit def toCallStreamObserver[A](
    responseObserver: StreamObserver[A]): CallStreamObserver[A] =
    responseObserver.asInstanceOf[CallStreamObserver[A]]

  /**
    * Create a server-side gRPC stream observer for requests.
    *
    * @param handler transform requests into respons(es)
    * @param responseObserver stream observer for responses provided by gRPC
    * @param requestBufferSize buffer size for requests, 1 by default
    * @param ec implicit execution contetxt
    * @param mat implicit materializer
    * @tparam A request type
    * @tparam B response type
    * @return back-pressured server-side gRPC stream observer for requests
    */
  def apply[A, B](handler: Flow[A, B, Any],
    responseObserver: StreamObserver[B],
    requestBufferSize: Int = 1)(
    implicit ec: ExecutionContext,
    mat: Materializer): StreamObserver[A] = {

    responseObserver.disableAutoInboundFlowControl()
    responseObserver.request(requestBufferSize)

    val requestSource = {
      def toStreamObserver(requests: SourceQueueWithComplete[A]) = {
        def handleOnNext(value: A) =
          requests.offer(value).onComplete {
            case Success(Enqueued) =>
              responseObserver.request(1)
            case Success(Dropped) =>
              throw new IllegalStateException(
                "Dropped should be impossible!?!")
            case Success(QueueClosed) =>
              throw new IllegalStateException(
                s"Queue closed for offer $value!")
            case Success(OfferFailure(t)) =>
              throw new IllegalStateException(s"Failure for offer $value!", t)
            case Failure(t) =>
              throw new IllegalStateException(s"Failed to offer $value!", t)
          }
        new StreamObserver[A] {
          override def onError(t: Throwable) = requests.fail(t)
          override def onCompleted()         = requests.complete()
          override def onNext(a: A)          = handleOnNext(a)
        }
      }
      Source
        .queue[A](requestBufferSize, OverflowStrategy.backpressure)
        .mapMaterializedValue(toStreamObserver)
    }

    val responseSink =
      Sink.queue[B]().mapMaterializedValue { responses =>
        def pull(): Unit = {
          def onNextThenPull(b: B) = {
            responseObserver.onNext(b)
            pull()
          }
          responses.pull().onComplete {
            case Success(Some(b)) => onNextThenPull(b)
            case Success(None)    => responseObserver.onCompleted()
            case Failure(t)       => responseObserver.onError(t)
          }
        }
        pull()
      }

    requestSource.via(handler).to(responseSink).run()
  }
}
