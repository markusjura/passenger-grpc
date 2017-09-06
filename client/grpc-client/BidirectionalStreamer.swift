//
//  BidirectionalStreamer.swift
//  grpc-client
//
//  Created by Mo Lotfi on 9/2/17.
//  Copyright © 2017 MOIA GmbH. All rights reserved.
//

import Foundation


protocol BiDirectionStreamDelegate:class {
  func bidirectionStreamManager(_ streamManager:BidirectionalStreamer,didReceive pong: String)
}

class BidirectionalStreamer  {

  private var service: Io_Moia_PassengerService?
  private var echoCall: Io_Moia_PassengerEchoCall?
  private var streaming = false
  private var delegate: BiDirectionStreamDelegate?

  init(delegate:BiDirectionStreamDelegate) {
    self.delegate = delegate
    setupServices()
  }

  public func start() {
    echoRequest()
  }

  public func stop() {
    streaming = false
    self.service = nil
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
    guard let updateCall = echoCall else {
      return
    }
    try updateCall.receive() { response, error in
      if let responseMessage = response {
        print(responseMessage.message)
        self.handleResponse(serverResponse: responseMessage)
        try! self.receiveUpdateMessages()
      } else if let error = error {
        switch error {
        case .endOfStream:
          self.stop()
          print("End of Stream")
        default:
          print("No message received. \(error)")
        }
      }
    }
  }

  private func sendUpdateMessage() throws -> Void {
    guard let updateCall = self.echoCall else {return}
    var request = Io_Moia_Ping()
    request.message = "MOIA".rpcValue
    try updateCall.send(request, errorHandler: { error in
      print(error.localizedDescription)
    })
  }

  private func handleResponse(serverResponse:Io_Moia_Pong) {
    DispatchQueue.main.async {
      self.delegate?.bidirectionStreamManager(self, didReceive: serverResponse.message.value)
    }
  }
}
