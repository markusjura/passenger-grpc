//
//  UnaryManager.swift
//  grpc-client
//
//  Created by Mo Lotfi on 9/1/17.
//  Copyright © 2017 MOIA GmbH. All rights reserved.
//

import Foundation

protocol UnaryRPCManagerDelegate:class {
  func unaryRPCManager(_ unaryManager:UnaryRPCManager, didUpdate response: Io_Moia_BookingResponse?, error:Error?)
}


class UnaryRPCManager {
  private var service: Io_Moia_PassengerService?
  weak var delegate: UnaryRPCManagerDelegate?

  init(delegate:UnaryRPCManagerDelegate) {
    self.delegate = delegate
    setupServices()
  }

  public func getUser(userId:String) {
    requestUnary(parameter: userId)
  }

  // Setup Client Services
  private func setupServices() {
    self.service = Io_Moia_PassengerService(address: "localhost:50051")
  }

  /// Unary RPC , Single request, Single response
  private func requestUnary(parameter:String) {
    guard let service = self.service else {print("provider not set");return}

    /// Creates a gRPC message
    var requestMessage = Io_Moia_BookingRequest()

    /// Set message propertise
    /// needs to cast swift types to Google_Protobuf_Type.
    /// SwiftProtobuf has convenience wrappers for this operation. 
    /// See type+extensions.swift for example
    /// More on optional values https://youtu.be/778znDnjROg?t=11m56s
    requestMessage.userID = parameter.rpcValue

    /// error handling block for Unary connection
    /// The connection can be Synchronous or Asynchronous. 
    /// Synchronous connection, calls the compilation block with response/error
    do {
      _ = try service.booktrip(requestMessage, completion: { (responeMessage, callResult) in

        /// Prints the response to console
        print("Response UserID = \(responeMessage?.userID ?? "empty response")")
        print("Response State = \(String(describing: responeMessage?.status))")

        /// Handle server response
        self.handleResponse(message: responeMessage)
      })
    } catch let error {
      print("Response Error = \(error)")

      /// -Internal- Informs delegate with error message
      self.delegate?.unaryRPCManager(self, didUpdate: nil, error: error)
    }
  }

  private func handleResponse(message:Io_Moia_BookingResponse?) {
    DispatchQueue.main.async {
      /// -Internal- Informs delegate with response message
      self.delegate?.unaryRPCManager(self, didUpdate: message, error: nil)
    }
  }
}
