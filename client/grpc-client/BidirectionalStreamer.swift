//
//  BidirectionalStreamer.swift
//  grpc-client
//
//  Created by Mo Lotfi on 9/2/17.
//  Copyright © 2017 MOIA GmbH. All rights reserved.
//

import Foundation


class BidirectionalStreamer  {

  private var service: Io_Moia_PassengerService?
  private var echoCall: Io_Moia_PassengerEchoCall?
  private var streaming = false

  init() {
    setupServices()
  }

  public func start() {
    echoRequest()
  }

  public func stop() {
    streaming = false
    echoRequest()
  }


  // Setup Client Services
  private func setupServices() {
    self.service = Io_Moia_PassengerService(address: "localhost:50051")
  }

  // Streaming gRPCs (bi-directional), Single request, stream response
  private func echoRequest() {
    guard let service = self.service else {print("provider not set");return}
    do {
      let echoCall = try service.echo() {call in
        print("Started Update call")
      }
      self.echoCall = echoCall
      try self.receiveUpdateMessages()
      try sendUpdateMessage()
    } catch let error {
      print(error)
    }
  }

  // Should get response here
  func receiveUpdateMessages() throws -> Void {
    print("Hit the response handler")
    guard let updateCall = echoCall else {
      return
    }
    try updateCall.receive() { response, error in
      if let responseMessage = response {
        print(responseMessage.message)
        try! self.receiveUpdateMessages()
      } else if let error = error {
        switch error {
        case .endOfStream:
          print("End of Stream")
        default:
          print("No message received. \(error)")
        }
      }
    }
  }


  private func sendUpdateMessage() throws -> Void {
    guard let updateCall = self.echoCall else {return}
    let request = Io_Moia_Ping()
    try updateCall.send(request, errorHandler: { error in
      print(error.localizedDescription)
    })
  }
}
