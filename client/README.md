

![alt text](https://github.com/moia-dev/grpc-client-poc/blob/master/assets/logo.png)


# gRPC Swift Example project

Swift iOS client example project using [swift-grpc](https://github.com/grpc/grpc-swift) & [swift-protobuf](https://github.com/apple/swift-protobuf)


---
>
> Note :
>  We are using Xcode9 and Swift 4 for this project. If you are targeting Xcode 8.3, 
> Please use tag `0.9.903` or `ls` when cloning swift protobuf. 
>
---
# **Welcome to Swift gRPC example project!** 

This example will guide you through necessary steps to successfully build and run an iOS application that implements following a gRPC connection with following technilogies  :

* Uniary RPC: `passengerRequest:` single request, single response, gRPS connection.
* Streaming RPC: `vehicleRequest: `Unidirectional streaming RPC, single request, streaming response.

Additionally you will install code generator plugins for `protoc`,Google's Protocol Buffer compiler, which we will use to generate client and server swift code. 


## Getting Started

Follow installation steps exactly as described below to prevent compiler error. If you have suggestions to improve theses steps, you are more than welcome to submit PR. 

## System Requirements
* A Swift 4 or later compiler (Xcode 9 or later). Both `swift-protobuf` & `swift-grpc` have support for order compilers and in order to build with them, you need to pick version of SwiftProtobuf that supports your compiler version. 


## Installing Google's protoc compiler 
to install Google's protoc compiler, simply run following command in terminal 
```
brew install protobuf
```
The SwiftProtobuf tests need a version of protoc which supports the `swift_prefix` option (introduced in protoc 3.2.0)

## Building and Installing the Code Generator Plugin
Head over to project directory (root) and clone `grpc-swift` as git submodule : 
```
$ git clone git@github.com:grpc/grpc-swift.git
```

to install code generator, execute following commands: (note that if you do not have a `.proto` file that you want to convert, you can simply [skip](https://github.com/moia-dev/grpc-client-poc/blob/master/README.md#building-grpc-subproject) to next step. This project contains sample swift gRPC generated code to get you easily up and running)

```
$ cd grpc-swift/Plugin
$ make
```
this will create `protoc-gen-swift` & `protoc-gen-swiftgrpc` which you'll need to generate swift code from your `.protp` file. 
Copy `protoc-gen-swift` &  `protoc-gen-swiftgrpc` into your `PATH`. The `protoc` program will find and use it automatically, allowing you to build Swift sources for your proto files. You will also, of course, need to add the SwiftProtobuf runtime library to your project as explained later on.


to build Swift sources for your proto files simply invoke following commands: 

```
cd <your proto file folder>
protoc --swift_out=. --swiftgrpc_out=. <your proto files>.proto
```

This will create three files `.client.pb.swift` , `.server.pb.swift` and `.pb.swift`. depending on your use cases, you need to import one or all of these files onto the example project. 


## Building gRPC subproject 
`cd` to project root directory and execute following command 

```
$ cd grpc-swift
$ make 
```
this make file uses the Swift Package Manager to generate an Xcode project for the SwiftGRPC package.This will create `SwiftGRPC.xcodeproj`. 

## Managing project dependencies 
Open Xcode and select project from the navigation panel, select target from project target list, tap on `Build Settings` and search for `Header Search Paths`. Add following line if it's not already in the search paths :

```
$(SRCROOT)/../grpc-swift/Sources/CgRPC/include
```

![alt text](https://github.com/moia-dev/grpc-client-poc/blob/master/assets/headerSearchPath.png)


Switch to `Build Phases` pane and extend `Link Binary With Libraries` and make sure **BoringSSL**, **CgRPC**, and **gRPC** frameworks are added there.

![alt text](https://github.com/moia-dev/grpc-client-poc/blob/master/assets/linkBinary.png)


and last but not least, install cocoapods dependencies by running 

```
pod install
```

This should `SwiftProtobuf` to your project which is essential for the `gRPC`. The project is set to install `0.9.904` which is Swift 4.0 release. You can change the tag if you have other compiler version. 


## Build & Run
You should be able to successfully build & run the project. gRPC client calls are defined in `ViewController` for now but they will be relocated to it's appropriate place.


## Report any issues 
If you run into problems, please send us a detailed report.
In case of `<unknown>:0: error: could not build Objective-C module 'CgRPC'` make sure that the project header search path is set correctly and it is reflecting the right folder structure.



