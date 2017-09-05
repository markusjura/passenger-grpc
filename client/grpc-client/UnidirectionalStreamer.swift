//
//  Streamer.swift
//  grpc-client
//
//  Created by Mo Lotfi on 9/1/17.
//  Copyright © 2017 MOIA GmbH. All rights reserved.
//

import Foundation

/// ***Not related to gRPC***
/// Class protocl that implements methods to inform delegates on changes to streaming state
protocol UniStreamDelegate:class {
  func streamManager(_ streamManager:UidirectionalStreamer,didUpdate location: (latitude:Double,longitude:Double))
}

class UidirectionalStreamer  {

  /// Service is used to make general API calls to define &initialize gRPC connection types
  private var service: Io_Moia_PassengerService?

  /// class that implements gRPC connection specific methods and API calls.
  /// Io_Moia_PassengerTrackVehicleCall is used for uni-directional connection type
  private var vehicleCall: Io_Moia_PassengerTrackVehicleCall?


  /// ***Not related to gRPC***
  private weak var delegate: UniStreamDelegate?

  /// Public property to capture current streaming state.
  private var streaming = false


  /// ***Not related to gRPC***
  /// init method for this calss
  ///
  /// - Parameter delegate: object that conforms to UniStreamDelegate and receives streaming responses
  init(delegate:UniStreamDelegate) {
    self.delegate = delegate
  }

  /// Setup Client Services
  private func setupServices() {


    /// Define and starts a gRPC channel that is used to make calls and receive responses
    /// - Parameter address : gRPC server address:port
    /// - Parameter certificates : creates a secure gRPC connection
    /// - host : Hostname instead of IPAddress for the server
    self.service = Io_Moia_PassengerService(address: "localhost:50051")
  }

  /// ***Not related to gRPC***
  /// Start services
  func start() {
    setupServices()
    startStreaming()
  }

  /// ***Not related to gRPC***
  /// stop sevices
  func stop() {
    streaming = false
    self.service = nil
  }

  private func startStreaming() {
    vehicleRequest()
  }

  /// Streaming RPCs (unidirectional), Single request, stream response
  private func vehicleRequest() {

    /// -Internal- Check if the service is nil and return if true
    guard let service = self.service else {print("provider not set");return}

    /// Check if we are not currently streaming to prevent multiple streaming calls
    if !streaming {

      /// Creates a gRPC message
      var requestMessage = Io_Moia_LocationRequest()
      /// Set message propertise
      requestMessage.userID = "MOIA"

      /// service calls are usually throwing functions and need to go inside try catch block
      do {

        /// API Call that defines and starts a gRPC connection
        /// Following parameters are set for each call under the hood
        ///
        /// - Parameter requestMessage: data containing the message to send (.unary and .serverStreaming only)
        /// - Parameter completion: a block to call with call results - Status code etc.
        self.vehicleCall = try service.trackvehicle(requestMessage, completion: { (call) in
          print("Started Stream Call")

          ///-Internal- set the streaming true
          self.streaming = true
        })

        /// Setup listener
        try receiveExtendedMessage()
      } catch let error {
        /// Handle API Call errors
        print(error)
      }
    }
  }

  private func receiveExtendedMessage() throws -> Void {
    guard let extendedCall = self.vehicleCall else {return}

    /// Call this to wait for results of gRPC connection that was defines for this service
    /// Under the hood this call performs an operation (response) on underlying opration group
    /// (streaming opration)
    try extendedCall.receive() { response, error in
      if let responseMessage = response {

        ///-Internal-
        print(responseMessage)
        ///-Internal- Handle Response
        self.handleResponse(locationResponse: responseMessage)
        if self.streaming {
          /// Reregister for response operation
          try! self.receiveExtendedMessage()
        } else {
          //          self.service = nil
        }
      } else if let error = error {
        switch error {
        case .endOfStream:
          print("Done.")
        default:
          print("No message received. \(error)")
        }
      }
    }
  }


  ///-Internal- Infrom delegate of changes to source
  private func handleResponse(locationResponse:Io_Moia_Location) {
    DispatchQueue.main.async {
      self.delegate?.streamManager(self, didUpdate: (latitude: locationResponse.lat.value,
                                                     longitude: locationResponse.lon.value))
    }
  }
}
