//
//  type+extensions.swift
//  grpc-client
//
//  Created by Mo Lotfi on 9/3/17.
//  Copyright © 2017 MOIA GmbH. All rights reserved.
//

import SwiftProtobuf


extension String {
  var rpcValue:Google_Protobuf_StringValue {
    get {
      return Google_Protobuf_StringValue(self)
    }
  }
}

extension Int {
  var rpcInt32Value:Google_Protobuf_Int32Value {
    get {
      return Google_Protobuf_Int32Value(Int32(self))
    }
  }

  var rpcInt64Value:Google_Protobuf_Int64Value {
    get {
      return Google_Protobuf_Int64Value(Int64(self))
    }
  }
}

extension Double {
  var rpcValue:Google_Protobuf_DoubleValue {
    get {
      return Google_Protobuf_DoubleValue(self)
    }
  }
}

extension Bool {
  var rpcValue:Google_Protobuf_BoolValue {
    get {
      return Google_Protobuf_BoolValue(self)
    }
  }
}

extension Float {
  var rpcValue:Google_Protobuf_FloatValue {
    get {
      return Google_Protobuf_FloatValue(self)
    }
  }
}
