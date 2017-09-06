# Passenger gRPC Scala and Swift Example

This example implements a Swift client and Scala server with gRPC and Akka Streams.

## Repository Structure

* Proto definition: [api/proto/passenger.proto](api/proto/passenger.proto)
* Generated gRPC objects Scala: [api/scala](api/scala)
* Generated gRPC objects Swift: [api/swift](api/swift)
* Scala Server: [server/](server/)
* Swift Client: [client/](client/)

## Endpoints

The endpoint definition is part of the [passenger.proto](api/proto/passenger.proto) file. The following endpoints are implemented:

* TripBook (request-response): Client sends a booking request at which the server response with a successfull booking response
* TrackVehicle (server-push): After client is sending an initial request, the server responds every second with the current vehicle location
* Echo (bi-directional-streaming): Once the client has send a ping message, the server response with a pong message. This can occur several time over one TCP channel.

## License

This code is open source software licensed under the
[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0) license.
